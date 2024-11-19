package com.woolog.repository;

import com.woolog.domain.Post;
import com.woolog.request.PagingRequest;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getPagingList(PagingRequest pagingRequest);
}