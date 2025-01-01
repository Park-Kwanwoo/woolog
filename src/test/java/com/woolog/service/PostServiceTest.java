package com.woolog.service;

import com.woolog.domain.Member;
import com.woolog.domain.Post;
import com.woolog.exception.PostNotFound;
import com.woolog.repository.MemberRepository;
import com.woolog.repository.post.PostRepository;
import com.woolog.request.post.PagingRequest;
import com.woolog.request.post.PostCreate;
import com.woolog.request.post.PostEdit;
import com.woolog.response.PostResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PostService postService;

    @Nested
    @DisplayName("성공 케이스")
    class successCase {
        @Test
        @DisplayName("게시글 생성 성공")
        void SUCCESS_POST_REQUEST() {

            // given
            when(postRepository.save(any(Post.class))).thenReturn(null);
            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(Member.builder().build()));

            // when
            postService.write(new PostCreate("dd", "dd"), "fake@blog.com");

            // then
            verify(postRepository, times(1)).save(any(Post.class));

        }

        @Test
        @DisplayName("게시글 1개 조회")
        void SUCCESS_GET_POST() {

            // given
            Long fakePostId = 1L;
            Post fakePost = Post.builder()
                    .title("제목입니다.")
                    .content("내용")
                    .build();

            Member member = Member.builder()
                    .nickName("fakeNickName")
                    .build();
            fakePost.setMember(member);
            when(postRepository.findById(fakePostId)).thenReturn(Optional.ofNullable(fakePost));

            // when
            PostResponse postResponse = postService.get(fakePostId);

            // then
            verify(postRepository, times(1)).findById(fakePostId);

            assertEquals( "제목입니다.", postResponse.getTitle());
            assertEquals("내용", postResponse.getContent());

        }

        @Test
        @DisplayName("게시글 리스트 조회")
        void GET_POST_LIST() {

            // given
            PagingRequest pagingRequest = PagingRequest.builder().build();
            when(postRepository.getPagingList(pagingRequest)).thenReturn(anyList());

            // when
            List<PostResponse> fakeResponsePost = postService.getPagingList(pagingRequest);

            // then
            verify(postRepository).getPagingList(pagingRequest);
            verify(postRepository, times(1)).getPagingList(pagingRequest);
            assertInstanceOf(List.class, fakeResponsePost);
        }

        @Test
        @DisplayName("게시글 수정")
        void EDIT_POST() {

            // given
            Long fakeId = anyLong();
            Post savedPost = Post.builder()
                    .title("수정 전 제목")
                    .content("수정 전 내용")
                    .build();

            Member member = Member.builder()
                    .nickName("fakeNickname")
                    .build();

            savedPost.setMember(member);

            PostEdit postEdit = PostEdit.builder()
                    .title("수정 후 제목")
                    .content("수정 후 내용")
                    .build();

            when(postRepository.findById(fakeId))
                    .thenReturn(Optional.of(savedPost));

            // when
            postService.edit(fakeId, postEdit);
            PostResponse postResponse = postService.get(fakeId);

            // then
            assertEquals("수정 후 제목",postResponse.getTitle());
            assertEquals("수정 후 내용",postResponse.getContent());

            verify(postRepository, times(2)).findById(fakeId);

        }

        @Test
        @DisplayName("게시글 삭제")
        void DELETE_POST() {

            // given
            Long fakeId = anyLong();

            Post savedPost = Post.builder()
                    .title("삭제 될 포스트 제목")
                    .content("삭제 될 포스트 내용")
                    .build();

            when(postRepository.findById(fakeId))
                    .thenReturn(Optional.of(savedPost))
                    .thenThrow(PostNotFound.class);

            // when
            postService.delete(fakeId);

            // then
            verify(postRepository, times(1)).delete(any(Post.class));

            assertThrows(PostNotFound.class, () -> postRepository.findById(fakeId));
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCase {

        @Test
        @DisplayName("게시글 조회 실패 - 존재하지 않는 Post")
        void FAILED_GET_POST() {

            // given
            when(postRepository.findById(anyLong())).thenThrow(new PostNotFound("postId", "존재하지 않는 글입니다."));

            // when
            // then
            PostNotFound e = assertThrows(PostNotFound.class, () -> postService.get(anyLong()));
            assertEquals("존재하지 않는 글입니다.", e.getMessage());
            assertEquals("postId", e.getField());

        }

        @Test
        @DisplayName("게시글 수정 실패 - 존재하지 않는 Post")
        void FAILED_EDIT_POST_NOT_EXIST() {

            // given
            when(postRepository.findById(anyLong())).thenThrow(new PostNotFound("postId", "존재하지 않는 글입니다."));

            PostEdit edit = PostEdit.builder().build();

            // when
            // then
            PostNotFound e = assertThrows(PostNotFound.class, () -> postService.edit(anyLong(), edit));

            assertEquals("존재하지 않는 글입니다.", e.getMessage());
            assertEquals("postId", e.getField());

        }

        @Test
        @DisplayName("게시글 삭제 실패 - 존재하지 않는 Post")
        void FAILED_DELETE_POST() {

            // given
            when(postRepository.findById(anyLong())).thenThrow(new PostNotFound("postId", "존재하지 않는 글입니다."));

            // when
            // then
            PostNotFound e = assertThrows(PostNotFound.class, () -> postService.delete(anyLong()));
            verify(postRepository, times(1)).findById(anyLong());
            assertEquals("존재하지 않는 글입니다.", e.getMessage());
            assertEquals("postId", e.getField());

        }
    }
}