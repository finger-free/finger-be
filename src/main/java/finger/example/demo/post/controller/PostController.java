package finger.example.demo.post.controller;

import finger.example.demo.auth.security.CustomUserDetails;
import finger.example.demo.post.domain.dto.request.PostCreateRequest;
import finger.example.demo.post.domain.dto.request.PostUpdateRequest;
import finger.example.demo.post.domain.dto.response.PostResponse;
import finger.example.demo.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public List<PostResponse> findAll() {
        return postService.findAll().stream()
                .map(PostResponse::from)
                .toList();
    }

    @GetMapping("/{postId}")
    public PostResponse findOne(@PathVariable Long postId) {
        return PostResponse.from(postService.findOne(postId));
    }

    @PostMapping
    public PostResponse create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PostCreateRequest request
    ) {
        return PostResponse.from(postService.create(userDetails.getMember(), request));
    }

    @PutMapping("/{postId}")
    public PostResponse update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @RequestBody PostUpdateRequest request
    ) {
        return PostResponse.from(postService.update(userDetails.getMember(), postId, request));
    }

    @DeleteMapping("/{postId}")
    public void delete(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        postService.delete(userDetails.getMember(), postId);
    }

    @PutMapping("/{postId}/good")
    public void good(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        postService.good(userDetails.getMember(), postId);
    }
}
