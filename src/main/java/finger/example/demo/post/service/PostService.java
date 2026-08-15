package finger.example.demo.post.service;

import finger.example.demo.member.domain.Member;
import finger.example.demo.comment.repository.CommentGoodJpaRepository;
import finger.example.demo.comment.repository.CommentJpaRepository;
import finger.example.demo.post.domain.Post;
import finger.example.demo.post.domain.PostGood;
import finger.example.demo.post.domain.dto.request.PostCreateRequest;
import finger.example.demo.post.domain.dto.request.PostUpdateRequest;
import finger.example.demo.post.domain.dto.response.PostResponse;
import finger.example.demo.post.repository.PostGoodJpaRepository;
import finger.example.demo.post.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostJpaRepository postJpaRepository;
    private final PostGoodJpaRepository postGoodJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final CommentGoodJpaRepository commentGoodJpaRepository;

    public List<PostResponse> findAll(Long memberId) {
        return postJpaRepository.findAll().stream()
                .map(post -> PostResponse.from(post, isGoodByMember(post.getId(), memberId)))
                .toList();
    }

    public PostResponse findOne(Long postId, Long memberId) {
        Post post = findPost(postId);
        return PostResponse.from(post, isGoodByMember(postId, memberId));
    }

    @Transactional
    public PostResponse create(Member member, PostCreateRequest request) {
        Post post = postJpaRepository.save(Post.create(member, request.title(), request.content()));
        return PostResponse.from(post, false);
    }

    @Transactional
    public PostResponse update(Member member, Long postId, PostUpdateRequest request) {
        Post post = findPost(postId);
        validateWriter(member, post);
        post.update(request.title(), request.content());
        return PostResponse.from(post, isGoodByMember(postId, member.getId()));
    }

    @Transactional
    public void delete(Member member, Long postId) {
        Post post = findPost(postId);
        validateWriter(member, post);
        commentGoodJpaRepository.deleteAllByCommentPostId(postId);
        commentJpaRepository.deleteAllByPostId(postId);
        postGoodJpaRepository.deleteAllByPostId(postId);
        postJpaRepository.delete(post);
    }

    @Transactional
    public void good(Member member, Long postId) {
        Post post = findPost(postId);
        Long memberId = member.getId();

        postGoodJpaRepository.findByMemberIdAndPostId(memberId, postId)
                .ifPresentOrElse(
                        postGood -> {
                            postGoodJpaRepository.delete(postGood);
                            post.decreaseGood();
                        },
                        () -> {
                            postGoodJpaRepository.save(PostGood.create(member, post));
                            post.increaseGood();
                        }
                );
    }

    private Post findPost(Long postId) {
        return postJpaRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("post not found"));
    }

    private boolean isGoodByMember(Long postId, Long memberId) {
        return memberId != null && postGoodJpaRepository.existsByMemberIdAndPostId(memberId, postId);
    }

    private void validateWriter(Member member, Post post) {
        if (!post.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("post writer only");
        }
    }
}
