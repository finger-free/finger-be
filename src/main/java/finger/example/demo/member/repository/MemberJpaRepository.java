package finger.example.demo.member.repository;

import finger.example.demo.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByGoogleSubject(String googleSubject);
}
