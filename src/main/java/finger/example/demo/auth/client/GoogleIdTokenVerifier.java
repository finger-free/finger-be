package finger.example.demo.auth.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Clock;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GoogleIdTokenVerifier {

    private static final String GOOGLE_JWKS_URL = "https://www.googleapis.com/oauth2/v3/certs";
    private static final List<String> GOOGLE_ISSUERS = List.of("accounts.google.com", "https://accounts.google.com");
    private static final long KEY_CACHE_MILLIS = 60 * 60 * 1000;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Clock clock;
    private final String clientId;
    private final Map<String, RSAPublicKey> publicKeys = new ConcurrentHashMap<>();
    private volatile long publicKeysExpiredAt = 0L;

    public GoogleIdTokenVerifier(@Value("${google.oauth.client-id:}") String clientId) {
        this.objectMapper = new ObjectMapper();
        this.clientId = clientId;
        this.httpClient = HttpClient.newHttpClient();
        this.clock = Clock.systemUTC();
    }

    public GoogleIdTokenPayload verify(String idToken) {
        if (clientId == null || clientId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Google OAuth client id is not configured.");
        }
        if (idToken == null || idToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idToken is required.");
        }

        String[] parts = idToken.split("\\.");
        if (parts.length != 3) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google ID token.");
        }

        try {
            Map<String, Object> header = decodeJson(parts[0]);
            Map<String, Object> claims = decodeJson(parts[1]);

            String algorithm = asString(header.get("alg"));
            String keyId = asString(header.get("kid"));
            if (!"RS256".equals(algorithm) || keyId == null || keyId.isBlank()) {
                throw unauthorized();
            }

            RSAPublicKey publicKey = findPublicKey(keyId);
            if (!isSignatureValid(publicKey, parts[0] + "." + parts[1], parts[2])) {
                refreshPublicKeys();
                publicKey = findPublicKey(keyId);
                if (!isSignatureValid(publicKey, parts[0] + "." + parts[1], parts[2])) {
                    throw unauthorized();
                }
            }

            validateClaims(claims);

            return new GoogleIdTokenPayload(
                    asString(claims.get("sub")),
                    asString(claims.get("email")),
                    Boolean.TRUE.equals(claims.get("email_verified")),
                    asString(claims.get("name"))
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw unauthorized();
        }
    }

    private Map<String, Object> decodeJson(String value) throws Exception {
        byte[] decoded = Base64.getUrlDecoder().decode(value);
        return objectMapper.readValue(decoded, new TypeReference<>() {
        });
    }

    private RSAPublicKey findPublicKey(String keyId) throws Exception {
        if (System.currentTimeMillis() > publicKeysExpiredAt || publicKeys.isEmpty()) {
            refreshPublicKeys();
        }
        RSAPublicKey publicKey = publicKeys.get(keyId);
        if (publicKey == null) {
            refreshPublicKeys();
            publicKey = publicKeys.get(keyId);
        }
        if (publicKey == null) {
            throw unauthorized();
        }
        return publicKey;
    }

    @SuppressWarnings("unchecked")
    private synchronized void refreshPublicKeys() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(GOOGLE_JWKS_URL)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw unauthorized();
        }

        Map<String, Object> jwks = objectMapper.readValue(response.body(), new TypeReference<>() {
        });
        List<Map<String, Object>> keys = (List<Map<String, Object>>) jwks.get("keys");
        Map<String, RSAPublicKey> refreshedKeys = new ConcurrentHashMap<>();
        for (Map<String, Object> key : keys) {
            if (!"RSA".equals(asString(key.get("kty")))) {
                continue;
            }
            String keyId = asString(key.get("kid"));
            String modulus = asString(key.get("n"));
            String exponent = asString(key.get("e"));
            if (keyId != null && modulus != null && exponent != null) {
                refreshedKeys.put(keyId, createPublicKey(modulus, exponent));
            }
        }

        publicKeys.clear();
        publicKeys.putAll(refreshedKeys);
        publicKeysExpiredAt = System.currentTimeMillis() + KEY_CACHE_MILLIS;
    }

    private RSAPublicKey createPublicKey(String modulus, String exponent) throws Exception {
        BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode(modulus));
        BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode(exponent));
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(n, e));
        return (RSAPublicKey) publicKey;
    }

    private boolean isSignatureValid(RSAPublicKey publicKey, String signingInput, String signature) throws Exception {
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(publicKey);
        verifier.update(signingInput.getBytes(StandardCharsets.US_ASCII));
        return verifier.verify(Base64.getUrlDecoder().decode(signature));
    }

    private void validateClaims(Map<String, Object> claims) {
        String issuer = asString(claims.get("iss"));
        String subject = asString(claims.get("sub"));
        String email = asString(claims.get("email"));

        if (!GOOGLE_ISSUERS.contains(issuer) || subject == null || subject.isBlank() || email == null || email.isBlank()) {
            throw unauthorized();
        }
        if (!hasExpectedAudience(claims.get("aud"))) {
            throw unauthorized();
        }
        if (!Boolean.TRUE.equals(claims.get("email_verified"))) {
            throw unauthorized();
        }

        long now = clock.instant().getEpochSecond();
        if (asLong(claims.get("exp")) <= now) {
            throw unauthorized();
        }
        Object notBefore = claims.get("nbf");
        if (notBefore != null && asLong(notBefore) > now) {
            throw unauthorized();
        }
    }

    private boolean hasExpectedAudience(Object audience) {
        if (audience instanceof String aud) {
            return clientId.equals(aud);
        }
        if (audience instanceof List<?> audiences) {
            return audiences.contains(clientId);
        }
        return false;
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google ID token.");
    }
}
