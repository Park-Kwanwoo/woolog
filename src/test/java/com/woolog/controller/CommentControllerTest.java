package com.woolog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.annotation.CustomWithMockUser;
import com.woolog.config.JwtTokenGenerator;
import com.woolog.domain.Comment;
import com.woolog.domain.Member;
import com.woolog.domain.Post;
import com.woolog.domain.Role;
import com.woolog.exception.CommentNotFound;
import com.woolog.exception.MemberNotExist;
import com.woolog.repository.MemberRepository;
import com.woolog.repository.comment.CommentRepository;
import com.woolog.repository.post.PostRepository;
import com.woolog.request.MemberInfo;
import com.woolog.request.comment.CommentCreate;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@PropertySource(value = "classpath:messages/validation.properties")
@ActiveProfiles("test")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    Member createAdmin() {

        Member admin = Member.builder()
                .email("admin@blog.com")
                .password("qwer123$")
                .name("관리자")
                .nickName("admin")
                .hashId("testAdminHash")
                .role(Role.ADMIN)
                .build();

        return memberRepository.save(admin);
    }

    Post createPost() {

        Post post = Post.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        post.setMember(createAdmin());

        return postRepository.save(post);
    }

    @AfterEach
    void clean() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }


    @Nested
    @DisplayName("성공 케이스")
    class success {

        @Test
        @CustomWithMockUser(email = "member@blog.com", nickname = "댓글러", name = "회원", role = "MEMBER")
        @DisplayName("댓글 작성 성공")
        void COMMENT_WRITE_SUCCESS() throws Exception {

            // given
            Post post = createPost();
            CommentCreate commentCreate = CommentCreate.builder()
                    .comment("댓글 달기")
                    .build();

            String request = objectMapper.writeValueAsString(commentCreate);

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            // expected
            mockMvc.perform(post("/posts/{postId}/comments",  post.getId())
                            .contentType(APPLICATION_JSON)
                            .content(request)
                            .headers(headers)
                            .cookie(cookie))
                    .andDo(print());

            Comment comment = commentRepository.findAll().getFirst();

            assertEquals("댓글 달기", comment.getComment());
            assertEquals(comment.getPost().getId(), post.getId());
        }


        @Test
        @Transactional
        @CustomWithMockUser(email = "member@blog.com", nickname = "댓글러", name = "회원", role = "MEMBER")
        @DisplayName("댓글 삭제 성공")
        void COMMENT_DELETE_SUCCESS() throws Exception {

            // given
            Post post = createPost();
            Member member = memberRepository.findByEmail("member@blog.com")
                    .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 회원입니다."));
            Comment comment = Comment.builder()
                    .comment("댓글 달기")
                    .build();
            comment.setMember(member);
            post.addComment(comment);
            commentRepository.save(comment);

            Comment savedComment = commentRepository.findAll().getFirst();

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);
            MemberInfo memberInfo = MemberInfo.builder()
                    .hashId(member.getHashId())
                    .password("qwer123$")
                    .build();

            String request = objectMapper.writeValueAsString(memberInfo);

            // expected
            mockMvc.perform(delete("/comments/{commentId}",  savedComment.getId())
                            .contentType(APPLICATION_JSON)
                            .content(request)
                            .headers(headers)
                            .cookie(cookie))
                    .andDo(print());

            CommentNotFound ex = assertThrows(CommentNotFound.class, () -> commentRepository.findById(comment.getId())
                    .orElseThrow(CommentNotFound::new));

            assertEquals("존재하지 않는 댓글입니다.", ex.getMessage());

        }
    }
}