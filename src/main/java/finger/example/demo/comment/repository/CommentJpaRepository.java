package finger.example.demo.comment.repository;

import finger.example.demo.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPostId(Long postId);

    void deleteAllByPostId(Long postId);
}
