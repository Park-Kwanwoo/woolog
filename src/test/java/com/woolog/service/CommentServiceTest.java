package com.woolog.service;

import com.woolog.domain.Comment;
import com.woolog.domain.Member;
import com.woolog.domain.Post;
import com.woolog.exception.CommentNotFound;
import com.woolog.repository.MemberRepository;
import com.woolog.repository.comment.CommentRepository;
import com.woolog.repository.post.PostRepository;
import com.woolog.request.MemberInfo;
import com.woolog.request.comment.CommentCreate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("댓글 서비스 단위 테스트")
    public class success {

        @Test
        @DisplayName("댓글 작성 성공")
        void COMMENT_WRITE_SUCCESS() {

            // given
            Post fakePost = Post.builder().build();
            Member fakeMember = Member.builder().build();
            CommentCreate commentCreate = CommentCreate.builder()
                    .comment("댓글 내용")
                    .build();

            when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(fakePost));
            when(memberRepository.findByEmail("member@blog.com")).thenReturn(Optional.ofNullable(fakeMember));

            // when
            commentService.write(1L, commentCreate, "member@blog.com");

            // then
            verify(postRepository, times(1)).findById(1L);
            verify(memberRepository, times(1)).findByEmail("member@blog.com");
            assertEquals(1L, fakePost.getComments().size());

        }

        @Test
        @DisplayName("댓글 삭제 성공")
        void COMMENT_DELETE_SUCCESS() {

            // given
            Member fakeMember = Member
                    .builder()
                    .email("member@blog.com")
                    .hashId("testMemberHash")
                    .build();
            Comment fakeComment = Comment.builder().build();
            MemberInfo memberInfo = MemberInfo.builder()
                    .hashId("testMemberHash")
                    .build();

            when(memberRepository.findByHashId("testMemberHash")).thenReturn(Optional.ofNullable(fakeMember));
            when(commentRepository.findById(1L)).thenReturn(Optional.ofNullable(fakeComment))
                    .thenThrow(CommentNotFound.class);
            when(passwordEncoder.matches(null, null)).thenReturn(true);

            // when
            commentService.delete(1L, memberInfo, "member@blog.com");

            // then
            verify(memberRepository, times(1)).findByHashId("testMemberHash");
            verify(commentRepository, times(1)).findById(1L);
            verify(commentRepository, times(1)).delete(fakeComment);

            assertThrows(CommentNotFound.class, () -> commentRepository.findById(1L));
        }
    }
}