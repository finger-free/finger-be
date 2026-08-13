package finger.example.demo.comment.domain.dto.request;

public record CommentCreateRequest(
        Long postId,
        String content
) {
}
