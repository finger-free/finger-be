package finger.example.demo.comment.repository;

import finger.example.demo.comment.domain.CommentGood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentGoodJpaRepository extends JpaRepository<CommentGood, Long> {

    Optional<CommentGood> findByMemberIdAndCommentId(Long memberId, Long commentId);

    boolean existsByMemberIdAndCommentId(Long memberId, Long commentId);
}
