package com.woolog.service;

import com.woolog.domain.Post;
import com.woolog.domain.PostEditor;
import com.woolog.exception.PostNotFound;
import com.woolog.repository.PostRepository;
import com.woolog.request.PagingRequest;
import com.woolog.request.PostCreate;
import com.woolog.request.PostEdit;
import com.woolog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void write(PostCreate postCreate) {

        Post post = postCreate.toPost();
        postRepository.save(post);
    }

    public PostResponse get(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("postId", "존재하지 않는 글입니다."));

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public List<PostResponse> getPagingList(PagingRequest pagingRequest) {

        return postRepository.getPagingList(pagingRequest).stream()
                .map(PostResponse::new)
                .toList();
    }

    @Transactional
    public void edit(Long postId, PostEdit postEdit) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("postId", "존재하지 않는 글입니다."));

        PostEditor.PostEditorBuilder editorBuilder = post.toEditor();

        PostEditor postEditor = editorBuilder
                .title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();

        post.edit(postEditor);
    }

    public void delete(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("postId", "존재하지 않는 글입니다."));
        postRepository.delete(post);
    }
}
