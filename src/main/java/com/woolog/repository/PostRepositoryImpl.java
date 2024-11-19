package com.woolog.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woolog.domain.Post;
import com.woolog.request.PagingRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.woolog.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getPagingList(PagingRequest pagingRequest) {

        return jpaQueryFactory.selectFrom(post)
                .limit(pagingRequest.getSize())
                .offset(pagingRequest.getOffset())
                .orderBy(post.id.desc())
                .fetch();
    }
}