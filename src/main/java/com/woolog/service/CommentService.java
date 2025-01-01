package com.woolog.service;

import com.woolog.config.HashEncrypt;
import com.woolog.domain.Comment;
import com.woolog.domain.Member;
import com.woolog.domain.Post;
import com.woolog.exception.CommentNotFound;
import com.woolog.exception.MemberInfoNotValidException;
import com.woolog.exception.MemberNotExist;
import com.woolog.exception.PostNotFound;
import com.woolog.repository.MemberRepository;
import com.woolog.repository.comment.CommentRepository;
import com.woolog.repository.post.PostRepository;
import com.woolog.request.MemberInfo;
import com.woolog.request.comment.CommentCreate;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Transactional
    public void delete(Long commentId, MemberInfo memberInfo, String email) {

        String hashId = memberInfo.getHashId();
        String password = memberInfo.getPassword();

        Member member = memberRepository.findByHashId(hashId)
                .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 회원입니다."));

        if (!email.equals(member.getEmail()) || !hashId.equals(member.getHashId()) || !passwordEncoder.matches(password, member.getPassword())) {
            throw new MemberInfoNotValidException();
        }

        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(CommentNotFound::new);

            commentRepository.delete(comment);
        } catch (EntityNotFoundException e) {
            throw new PostNotFound("post", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e);
        }
    }
}
