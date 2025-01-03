package com.woolog.service;

import com.woolog.domain.Comment;
import com.woolog.domain.Member;
import com.woolog.domain.Post;
import com.woolog.exception.CommentNotFound;
import com.woolog.exception.MemberNotExist;
import com.woolog.exception.PostNotFound;
import com.woolog.repository.MemberRepository;
import com.woolog.repository.comment.CommentRepository;
import com.woolog.repository.post.PostRepository;
import com.woolog.request.comment.CommentCreate;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
                .orElseThrow(() -> new PostNotFound("postId", "존재하지 않는 글입니다."));

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 이메일입니다."));

        Comment comment = commentCreate.toComment();
        comment.setMember(member);
        post.addComment(comment);
    }

    @Transactional(readOnly = true)
    public void delete(Long commentId, String email) {

        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(CommentNotFound::new);

            Member member = comment.getMember();

            // 새로운 에러 생성
            if (!member.getEmail().equals(email)) {
                throw new RuntimeException(email);
            }

            commentRepository.delete(comment);
        } catch (EntityNotFoundException e) {
            throw new PostNotFound("post", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e);
        }
    }
}
