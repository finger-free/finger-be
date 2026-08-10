package finger.example.demo.auth.domain.dto.response;

import finger.example.demo.member.domain.Member;

public record GoogleLoginResponse(
        Long memberId,
        String email,
        String name
) {

    public static GoogleLoginResponse from(Member member) {
        return new GoogleLoginResponse(
                member.getId(),
                member.getEmail(),
                member.getName()
        );
    }
}
