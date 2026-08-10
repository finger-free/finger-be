package finger.example.demo.auth.service;

import finger.example.demo.auth.client.GoogleIdTokenPayload;
import finger.example.demo.auth.client.GoogleIdTokenVerifier;
import finger.example.demo.auth.domain.dto.request.GoogleLoginRequest;
import finger.example.demo.auth.domain.dto.response.GoogleLoginResponse;
import finger.example.demo.member.domain.Member;
import finger.example.demo.member.repository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final MemberJpaRepository memberJpaRepository;

    @Transactional
    public GoogleLoginResponse loginWithGoogle(GoogleLoginRequest request) {
        GoogleIdTokenPayload payload = googleIdTokenVerifier.verify(request.idToken());

        Member member = memberJpaRepository.findByGoogleSubject(payload.subject())
                .map(existingMember -> {
                    existingMember.updateGoogleProfile(payload.email(), payload.name());
                    return existingMember;
                })
                .orElseGet(() -> memberJpaRepository.save(Member.createGoogleMember(
                        payload.subject(),
                        payload.email(),
                        payload.name()
                )));

        return GoogleLoginResponse.from(member);
    }
}
