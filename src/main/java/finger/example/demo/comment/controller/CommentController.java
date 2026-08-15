package finger.example.demo.comment.controller;

import finger.example.demo.auth.security.CustomUserDetails;
import finger.example.demo.comment.domain.dto.request.CommentCreateRequest;
import finger.example.demo.comment.domain.dto.response.CommentResponse;
import finger.example.demo.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public List<CommentResponse> findAllByPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId
    ) {
        return commentService.findAllByPost(postId, getMemberId(userDetails));
    }

    @PostMapping("/comments")
    public CommentResponse create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CommentCreateRequest request
    ) {
        return commentService.create(userDetails.getMember(), request);
    }

    @DeleteMapping("/comments/{commentId}")
    public void delete(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long commentId) {
        commentService.delete(userDetails.getMember(), commentId);
    }

    @PutMapping("/comments/{commentId}/good")
    public void good(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long commentId) {
        commentService.good(userDetails.getMember(), commentId);
    }

    private Long getMemberId(CustomUserDetails userDetails) {
        return userDetails == null ? null : userDetails.getMember().getId();
    }
}
