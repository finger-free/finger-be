package finger.example.demo.post.service;

import finger.example.demo.member.domain.Member;
import finger.example.demo.member.repository.MemberJpaRepository;
import finger.example.demo.post.domain.Post;
import finger.example.demo.post.domain.PostGood;
import finger.example.demo.post.domain.dto.request.PostCreateRequest;
import finger.example.demo.post.domain.dto.request.PostUpdateRequest;
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
    private final MemberJpaRepository memberJpaRepository;

    public List<Post> findAll() {
        return postJpaRepository.findAll();
    }

    public Post findOne(Long postId) {
        return findPost(postId);
    }

    @Transactional
    public Post create(PostCreateRequest request) {
        Member member = memberJpaRepository.findById(request.memberId())
                .orElseThrow(() -> new RuntimeException("member not found"));

        return postJpaRepository.save(Post.create(member, request.title(), request.content()));
    }

    @Transactional
    public Post update(Long postId, PostUpdateRequest request) {
        Post post = findPost(postId);
        post.update(request.title(), request.content());
        return post;
    }

    @Transactional
    public void delete(Long postId) {
        postJpaRepository.delete(findPost(postId));
    }

    @Transactional
    public void good(Long postId, Long memberId) {
        Post post = findPost(postId);
        Member member = findMember(memberId);

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

    private Member findMember(Long memberId) {
        return memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("member not found"));
    }
}
