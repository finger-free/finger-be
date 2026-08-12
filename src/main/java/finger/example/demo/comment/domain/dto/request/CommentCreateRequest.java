package finger.example.demo.comment.domain.dto.request;

public record CommentCreateRequest(
        Long memberId,
        Long postId,
        String content
) {
}
