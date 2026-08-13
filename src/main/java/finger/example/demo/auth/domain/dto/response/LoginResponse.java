package finger.example.demo.auth.domain.dto.response;

import finger.example.demo.member.domain.Member;

public record LoginResponse(
        Long memberId,
        String email,
        String name,
        String accessToken
) {

    public static LoginResponse from(Member member, String accessToken) {
        return new LoginResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                accessToken
        );
    }
}
