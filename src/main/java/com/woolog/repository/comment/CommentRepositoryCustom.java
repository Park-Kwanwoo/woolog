package com.woolog.repository.comment;

import com.woolog.domain.Comment;
import com.woolog.request.post.PagingRequest;
import org.springframework.data.domain.PageImpl;

public interface CommentRepositoryCustom {

    PageImpl<Comment> getList(PagingRequest pagingRequest, Long postId);
}
