package com.woolog.controller;

import com.woolog.request.comment.CommentCreate;
import com.woolog.request.post.PagingRequest;
import com.woolog.response.CommentResponse;
import com.woolog.response.PagingResponse;
import com.woolog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public void write(@PathVariable Long postId, @RequestBody @Valid CommentCreate commentCreate, @AuthenticationPrincipal String email) {
        commentService.write(postId, commentCreate, email);
    }

    @GetMapping("/posts/{postId}/comments")
    public PagingResponse<CommentResponse> getList(@PathVariable("postId") Long postId, @ModelAttribute @Valid PagingRequest pagingRequest) {
        return commentService.getList(pagingRequest, postId);
    }

    @DeleteMapping("/comments/{commentId}")
    public void delete(@PathVariable Long commentId, @AuthenticationPrincipal String email) {
        commentService.delete(commentId, email);
    }
}
