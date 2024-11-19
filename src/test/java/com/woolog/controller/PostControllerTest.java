package com.woolog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.domain.Post;
import com.woolog.repository.PostRepository;
import com.woolog.request.PostCreate;
import com.woolog.request.PostEdit;
import com.woolog.response.ResponseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@PropertySource("classpath:validation.properties")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${NotBlank.title}")
    private String titleValidationMessage;

    @Value("${NotBlank.content}")
    private String contentValidationMessage;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Nested
    @DisplayName("성공 케이스")
    class successCase {

        @Test
        @DisplayName("게시글 생성 성공")
        void SUCCESS_POST_REQUEST() throws Exception {

            // given
            PostCreate request = PostCreate.builder()
                    .title("제목입니다.")
                    .content("내용")
                    .build();
            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/posts")
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            assertEquals(1L, postRepository.count());

            Post post = postRepository.findAll().get(0);
            assertEquals("제목입니다.", post.getTitle());
            assertEquals("내용", post.getContent());
        }

        @Test
        @DisplayName("게시글 단건 조회")
        void GET_POST_ONE() throws Exception {

            // given
            Post post = Post.builder()
                    .title("제목입니다.")
                    .content("내용")
                    .build();

            Post savedPost = postRepository.save(post);

            // expected
            mockMvc.perform(get("/posts/{postId}", savedPost.getId())
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("제목입니다."))
                    .andExpect(jsonPath("$.content").value("내용"))
                    .andDo(print());

            // then
            assertEquals(post.getTitle(), savedPost.getTitle());
            assertEquals(post.getContent(), savedPost.getContent());
        }

        @Test
        @DisplayName("게시글 리스트 조회")
        void GET_POST_LIST() throws Exception {

            // given
            List<Post> requestPosts = IntStream.range(1, 31)
                    .mapToObj(i -> Post.builder()
                            .title("우로그 테스트 제목" + i)
                            .content("우로그 테스트 내용" + i)
                            .build())
                    .toList();

            postRepository.saveAll(requestPosts);

            // when
            mockMvc.perform(get("/posts?page=0&size=10")
                            .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", is(10)))
                    .andExpect(jsonPath("$[0].title").value("우로그 테스트 제목30"))
                    .andDo(print());

            // then
            assertEquals(30, postRepository.count());

        }

        @Test
        @DisplayName("게시글 수정")
        void EDIT_POST() throws Exception {

            // given
            Post post = Post.builder()
                    .title("수정 전 제목")
                    .content("수정 전 내용")
                    .build();

            postRepository.save(post);

            PostEdit postEdit = PostEdit.builder()
                    .title("수정 후 제목")
                    .content("수정 후 내용")
                    .build();

            String editJson = objectMapper.writeValueAsString(postEdit);

            // expected
            mockMvc.perform(patch("/posts/{postId}", post.getId())
                            .contentType(APPLICATION_JSON)
                            .content(editJson))
                    .andExpect(status().isOk())
                    .andDo(print());

            Post editPost = postRepository.findById(post.getId()).orElseThrow(() -> new RuntimeException("존재하지 않는 글입니다. id = " + post.getId()));

            assertEquals("수정 후 제목", editPost.getTitle());
            assertEquals("수정 후 내용", editPost.getContent());
        }

        @Test
        @DisplayName("게시글 삭제")
        void DELETE_POST() throws Exception {

            // given
            Post post = Post.builder()
                    .title("수정 전 제목")
                    .content("수정 전 내용")
                    .build();

            postRepository.save(post);

            // expected
            mockMvc.perform(delete("/posts/{postId}", post.getId())
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());

            assertEquals(Optional.empty(), postRepository.findById(post.getId()));
        }

    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCase {

        @Test
        @DisplayName("제목이 존재하지 않을 때")
        void TITLE_VALUE_VALIDATION() throws Exception {

            // given
            PostCreate request = PostCreate.builder()
                    .title(null)
                    .content("내용")
                    .build();
            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/posts")
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(ResponseStatus.BAD_REQUEST.getStatus()))
                    .andExpect(jsonPath("$.code").value(ResponseStatus.BAD_REQUEST.getCode()))
                    .andExpect(jsonPath("$.description").value(ResponseStatus.BAD_REQUEST.getDescription()))
                    .andExpect(jsonPath("$.data[0].field").value("title"))
                    .andExpect(jsonPath("$.data[0].message").value(titleValidationMessage))
                    .andDo(print());
        }

        @Test
        @DisplayName("내용이 존재하지 않을 때")
        void CONTENT_VALUE_VALIDATION() throws Exception {

            // given
            PostCreate request = PostCreate.builder()
                    .title("제목입니다.")
                    .content(null)
                    .build();
            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/posts")
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(ResponseStatus.BAD_REQUEST.getStatus()))
                    .andExpect(jsonPath("$.code").value(ResponseStatus.BAD_REQUEST.getCode()))
                    .andExpect(jsonPath("$.description").value(ResponseStatus.BAD_REQUEST.getDescription()))
                    .andExpect(jsonPath("$.data[0].field").value("content"))
                    .andExpect(jsonPath("$.data[0].message").value(contentValidationMessage))
                    .andDo(print());

        }

        @Test
        @DisplayName("제목과 내용이 존재하지 않을 때")
        void TITLE_AND_CONTENT_VALUE_VALIDATION() throws Exception {

            // given
            PostCreate request = PostCreate.builder()
                    .title(null)
                    .content(null)
                    .build();
            String json = objectMapper.writeValueAsString(request);

            // expected
            mockMvc.perform(post("/posts")
                            .contentType(APPLICATION_JSON)
                            .content(json)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(ResponseStatus.BAD_REQUEST.getStatus()))
                    .andExpect(jsonPath("$.code").value(ResponseStatus.BAD_REQUEST.getCode()))
                    .andExpect(jsonPath("$.description").value(ResponseStatus.BAD_REQUEST.getDescription()))
                    .andExpect(jsonPath("$.data[0].field").value("content"))
                    .andExpect(jsonPath("$.data[0].message").value(contentValidationMessage))
                    .andExpect(jsonPath("$.data[1].field").value("title"))
                    .andExpect(jsonPath("$.data[1].message").value(titleValidationMessage))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시글 단건 조회 실패")
        void FALIED_GET_POST_ONE() throws Exception {

            // given
            Post post = Post.builder()
                    .title("제목입니다.")
                    .content("내용")
                    .build();

            Post savedPost = postRepository.save(post);

            // expected
            mockMvc.perform(get("/posts/{postId}", savedPost.getId() + 1L)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.description").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.data[0].field").value("postId"))
                    .andExpect(jsonPath("$.data[0].message").value("존재하지 않는 글입니다."))
                    .andDo(print());

        }

        @Test
        @DisplayName("게시글 수정 실패")
        void FALIED_EDIT_POST_ONE() throws Exception {

            // given
            Post post = Post.builder()
                    .title("제목입니다.")
                    .content("내용")
                    .build();

            Post savedPost = postRepository.save(post);

            PostEdit postEdit = PostEdit.builder()
                    .title("수정 후 제목")
                    .content("수정 후 내용")
                    .build();

            String editJson = objectMapper.writeValueAsString(postEdit);

            // expected
            mockMvc.perform(patch("/posts/{postId}", savedPost.getId() + 1L)
                            .contentType(APPLICATION_JSON)
                            .content(editJson))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.description").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.data[0].field").value("postId"))
                    .andExpect(jsonPath("$.data[0].message").value("존재하지 않는 글입니다."))
                    .andDo(print());

        }

        @Test
        @DisplayName("게시글 삭제 실패")
        void FALIED_DELETE_POST_ONE() throws Exception {

            // given
            Post post = Post.builder()
                    .title("제목입니다.")
                    .content("내용")
                    .build();

            Post savedPost = postRepository.save(post);

            // expected
            mockMvc.perform(delete("/posts/{postId}", savedPost.getId() + 1L)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.description").value("잘못된 요청입니다."))
                    .andExpect(jsonPath("$.data[0].field").value("postId"))
                    .andExpect(jsonPath("$.data[0].message").value("존재하지 않는 글입니다."))
                    .andDo(print());

        }
    }
}