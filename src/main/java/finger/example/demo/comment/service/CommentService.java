package finger.example.demo.comment.service;

import finger.example.demo.comment.domain.Comment;
import finger.example.demo.comment.domain.CommentGood;
import finger.example.demo.comment.domain.dto.request.CommentCreateRequest;
import finger.example.demo.comment.repository.CommentGoodJpaRepository;
import finger.example.demo.comment.repository.CommentJpaRepository;
import finger.example.demo.member.domain.Member;
import finger.example.demo.post.domain.Post;
import finger.example.demo.post.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentJpaRepository commentJpaRepository;
    private final CommentGoodJpaRepository commentGoodJpaRepository;
    private final PostJpaRepository postJpaRepository;

    public List<Comment> findAllByPost(Long postId) {
        return commentJpaRepository.findAllByPostId(postId);
    }

    @Transactional
    public Comment create(Member member, CommentCreateRequest request) {
        Post post = postJpaRepository.findById(request.postId())
                .orElseThrow(() -> new RuntimeException("post not found"));

        return commentJpaRepository.save(Comment.create(member, post, request.content()));
    }

    @Transactional
    public void delete(Member member, Long commentId) {
        Comment comment = findComment(commentId);
        validateWriter(member, comment);
        commentJpaRepository.delete(comment);
    }

    @Transactional
    public void good(Member member, Long commentId) {
        Comment comment = findComment(commentId);
        Long memberId = member.getId();

        commentGoodJpaRepository.findByMemberIdAndCommentId(memberId, commentId)
                .ifPresentOrElse(
                        commentGood -> {
                            commentGoodJpaRepository.delete(commentGood);
                            comment.decreaseGood();
                        },
                        () -> {
                            commentGoodJpaRepository.save(CommentGood.create(member, comment));
                            comment.increaseGood();
                        }
                );
    }

    private Comment findComment(Long commentId) {
        return commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("comment not found"));
    }

    public boolean isGoodByMember(Long commentId, Long memberId) {
        return memberId != null && commentGoodJpaRepository.existsByMemberIdAndCommentId(memberId, commentId);
    }

    private void validateWriter(Member member, Comment comment) {
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("comment writer only");
        }
    }
}
