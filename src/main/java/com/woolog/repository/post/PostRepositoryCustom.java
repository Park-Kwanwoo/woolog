package com.woolog.repository.post;

import com.woolog.domain.Post;
import com.woolog.request.post.PagingRequest;
import org.springframework.data.domain.PageImpl;

public interface PostRepositoryCustom {

    PageImpl<Post> getPagingList(PagingRequest pagingRequest);
}