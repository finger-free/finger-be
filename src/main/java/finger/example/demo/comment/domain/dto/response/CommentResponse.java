package finger.example.demo.comment.domain.dto.response;

import finger.example.demo.comment.domain.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long memberId,
        Long postId,
        String content,
        int good,
        boolean liked,
        LocalDateTime createdAt
) {

    public static CommentResponse from(Comment comment) {
        return from(comment, false);
    }

    public static CommentResponse from(Comment comment, boolean liked) {
        return new CommentResponse(
                comment.getId(),
                comment.getMember().getId(),
                comment.getPost().getId(),
                comment.getContent(),
                comment.getGood(),
                liked,
                comment.getCreatedAt()
        );
    }
}
