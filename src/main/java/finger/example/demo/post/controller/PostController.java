package finger.example.demo.post.controller;

import finger.example.demo.post.domain.dto.request.PostCreateRequest;
import finger.example.demo.post.domain.dto.request.PostUpdateRequest;
import finger.example.demo.post.domain.dto.response.PostResponse;
import finger.example.demo.post.service.PostService;
import lombok.RequiredArgsConstructor;
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
    public PostResponse create(@RequestBody PostCreateRequest request) {
        return PostResponse.from(postService.create(request));
    }

    @PutMapping("/{postId}")
    public PostResponse update(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        return PostResponse.from(postService.update(postId, request));
    }

    @DeleteMapping("/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }

    @PutMapping("/{postId}/good")
    public void good(@PathVariable Long postId) {
        postService.good(postId);
    }
}
