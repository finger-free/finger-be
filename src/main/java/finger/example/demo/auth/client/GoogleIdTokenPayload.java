package finger.example.demo.auth.client;

public record GoogleIdTokenPayload(
        String subject,
        String email,
        boolean emailVerified,
        String name
) {
}
