package com.woolog.service;

import com.woolog.domain.Member;
import com.woolog.domain.Post;
import com.woolog.domain.PostEditor;
import com.woolog.exception.MemberInfoNotValidException;
import com.woolog.exception.MemberNotExist;
import com.woolog.exception.PostNotFound;
import com.woolog.repository.MemberRepository;
import com.woolog.repository.post.PostRepository;
import com.woolog.request.post.PagingRequest;
import com.woolog.request.post.PostCreate;
import com.woolog.request.post.PostEdit;
import com.woolog.response.PagingResponse;
import com.woolog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void write(PostCreate postCreate, String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 이메일입니다."));

        Post post = postCreate.toPost();
        post.setMember(member);

        postRepository.save(post);
    }

    @Transactional
    public PostResponse get(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("postId", "존재하지 않는 글입니다."));

        return new PostResponse(post);
    }

    @Transactional(readOnly = true)
    public PagingResponse<PostResponse> getPagingList(PagingRequest pagingRequest) {

        Page<Post> postPage = postRepository.getPagingList(pagingRequest);

        return new PagingResponse<>(postPage, PostResponse.class);
    }

    @Transactional
    public void edit(Long postId, PostEdit postEdit, String email) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("postId", "존재하지 않는 글입니다."));

        Member member = post.getMember();

        if (!member.getEmail().equals(email)) {
            throw new MemberInfoNotValidException();
        }

        PostEditor.PostEditorBuilder editorBuilder = post.toEditor();

        PostEditor postEditor = editorBuilder
                .title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();

        post.edit(postEditor);
    }

    @Transactional
    public void delete(Long postId, String email) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("postId", "존재하지 않는 글입니다."));

        Member member = post.getMember();

        if (!member.getEmail().equals(email)) {
            throw new MemberInfoNotValidException();
        }

        postRepository.delete(post);
    }
}
