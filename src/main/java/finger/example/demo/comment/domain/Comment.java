package finger.example.demo.comment.domain;

import finger.example.demo.BaseEntity;
import finger.example.demo.member.domain.Member;
import finger.example.demo.post.domain.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" , nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "good", nullable = false)
    private int good;

    public static Comment create(Member member, Post post, String content) {
        Comment comment = new Comment();
        comment.member = member;
        comment.post = post;
        comment.content = content;
        comment.good = 0;
        return comment;
    }

    public void increaseGood() {
        this.good++;
    }

    public void decreaseGood() {
        if (this.good > 0) {
            this.good--;
        }
    }
}
