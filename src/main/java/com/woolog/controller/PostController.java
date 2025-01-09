package com.woolog.controller;

import com.woolog.request.post.PagingRequest;
import com.woolog.request.post.PostCreate;
import com.woolog.request.post.PostEdit;
import com.woolog.response.PagingResponse;
import com.woolog.response.PostResponse;
import com.woolog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public void write(@RequestBody @Valid PostCreate postCreate, @AuthenticationPrincipal String email) {
        postService.write(postCreate, email);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts")
    public PagingResponse<PostResponse> getList(@ModelAttribute @Valid PagingRequest pagingRequest) {
        return postService.getPagingList(pagingRequest);
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody @Valid PostEdit postEdit, @AuthenticationPrincipal String email) {
        postService.edit(postId, postEdit, email);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId, @AuthenticationPrincipal String email) {
        postService.delete(postId, email);
    }
}
