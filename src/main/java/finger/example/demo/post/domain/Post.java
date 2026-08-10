package finger.example.demo.post.domain;

import finger.example.demo.BaseEntity;
import finger.example.demo.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "good", nullable = false)
    private int good;

    public static Post create(Member member, String title, String content) {
        Post post = new Post();
        post.member = member;
        post.title = title;
        post.content = content;
        post.good = 0;
        return post;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void increaseGood() {
        this.good++;
    }
}
