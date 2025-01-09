package com.woolog.repository.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woolog.domain.Post;
import com.woolog.request.post.PagingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static com.woolog.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PageImpl<Post> getPagingList(PagingRequest pagingRequest) {

        Long totalCount = jpaQueryFactory.select(post.count())
                .from(post)
                .fetchFirst();

        List<Post> items = jpaQueryFactory.selectFrom(post)
                .limit(pagingRequest.getSize())
                .offset(pagingRequest.getOffset())
                .orderBy(post.id.desc())
                .fetch();

        return new PageImpl<Post>(items, pagingRequest.getPageable(), totalCount);
    }
}