package com.woolog.service;

import com.woolog.domain.Comment;
import com.woolog.domain.Member;
import com.woolog.domain.Post;
import com.woolog.exception.CommentNotFound;
import com.woolog.exception.MemberInfoNotValidException;
import com.woolog.exception.MemberNotExist;
import com.woolog.exception.PostNotFound;
import com.woolog.repository.member.MemberRepository;
import com.woolog.repository.comment.CommentRepository;
import com.woolog.repository.post.PostRepository;
import com.woolog.request.comment.CommentCreate;
import com.woolog.request.post.PagingRequest;
import com.woolog.response.CommentResponse;
import com.woolog.response.PagingResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void write(Long postId, CommentCreate commentCreate, String email) {


        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotExist::new);

        Comment comment = commentCreate.toComment();
        comment.setMember(member);
        post.addComment(comment);
    }

    @Transactional(readOnly = true)
    public PagingResponse<CommentResponse> getList(PagingRequest pagingRequest, Long postId) {

        PageImpl<Comment> comments = commentRepository.getList(pagingRequest, postId);
        return new PagingResponse<>(comments, CommentResponse.class);
    }

    @Transactional
    public void delete(Long commentId, String email) {

        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(CommentNotFound::new);

            Member member = comment.getMember();

            // 새로운 에러 생성
            if (!member.getEmail().equals(email)) {
                throw new MemberInfoNotValidException();
            }

            commentRepository.delete(comment);
        } catch (EntityNotFoundException e) {
            throw new PostNotFound();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e);
        }
    }
}
