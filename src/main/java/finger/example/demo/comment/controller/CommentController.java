package finger.example.demo.comment.controller;

import finger.example.demo.comment.domain.dto.request.CommentCreateRequest;
import finger.example.demo.comment.domain.dto.response.CommentResponse;
import finger.example.demo.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public List<CommentResponse> findAllByPost(@PathVariable Long postId) {
        return commentService.findAllByPost(postId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    @PostMapping("/comments")
    public CommentResponse create(@RequestBody CommentCreateRequest request) {
        return CommentResponse.from(commentService.create(request));
    }

    @DeleteMapping("/comments/{commentId}")
    public void delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
    }

    @PutMapping("/comments/{commentId}/good")
    public void good(@PathVariable Long commentId) {
        commentService.good(commentId);
    }
}
