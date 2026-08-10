package finger.example.demo.post.domain.dto.response;

import finger.example.demo.post.domain.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        Long memberId,
        String title,
        String content,
        int good,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getMember().getId(),
                post.getTitle(),
                post.getContent(),
                post.getGood(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
