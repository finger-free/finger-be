package finger.example.demo.post.repository;

import finger.example.demo.post.domain.PostGood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostGoodJpaRepository extends JpaRepository<PostGood, Long> {

    Optional<PostGood> findByMemberIdAndPostId(Long memberId, Long postId);

    boolean existsByMemberIdAndPostId(Long memberId, Long postId);

    void deleteAllByPostId(Long postId);
}
