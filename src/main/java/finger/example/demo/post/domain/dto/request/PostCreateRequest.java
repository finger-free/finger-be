package finger.example.demo.post.domain.dto.request;

public record PostCreateRequest(
        Long memberId,
        String title,
        String content
) {
}
