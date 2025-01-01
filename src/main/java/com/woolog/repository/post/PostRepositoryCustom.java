package com.woolog.repository.post;

import com.woolog.domain.Post;
import com.woolog.request.post.PagingRequest;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getPagingList(PagingRequest pagingRequest);
}