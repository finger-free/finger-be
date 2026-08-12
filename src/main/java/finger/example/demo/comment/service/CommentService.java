package finger.example.demo.comment.service;

import finger.example.demo.comment.domain.Comment;
import finger.example.demo.comment.domain.dto.request.CommentCreateRequest;
import finger.example.demo.comment.repository.CommentJpaRepository;
import finger.example.demo.member.domain.Member;
import finger.example.demo.member.repository.MemberJpaRepository;
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
    private final MemberJpaRepository memberJpaRepository;
    private final PostJpaRepository postJpaRepository;

    public List<Comment> findAllByPost(Long postId) {
        return commentJpaRepository.findAllByPostId(postId);
    }

    @Transactional
    public Comment create(CommentCreateRequest request) {
        Member member = memberJpaRepository.findById(request.memberId())
                .orElseThrow(() -> new RuntimeException("member not found"));
        Post post = postJpaRepository.findById(request.postId())
                .orElseThrow(() -> new RuntimeException("post not found"));

        return commentJpaRepository.save(Comment.create(member, post, request.content()));
    }

    @Transactional
    public void delete(Long commentId) {
        commentJpaRepository.delete(findComment(commentId));
    }

    @Transactional
    public void good(Long commentId) {
        findComment(commentId).increaseGood();
    }

    private Comment findComment(Long commentId) {
        return commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("comment not found"));
    }
}
