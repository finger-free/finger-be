package finger.example.demo.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String googleSubject;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    public static Member createGoogleMember(String googleSubject, String email, String name) {
        Member member = new Member();
        member.googleSubject = googleSubject;
        member.email = email;
        member.name = name;
        return member;
    }

    public void updateGoogleProfile(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
