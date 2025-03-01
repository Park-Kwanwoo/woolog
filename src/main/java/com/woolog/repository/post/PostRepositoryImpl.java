package com.woolog.repository.post;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woolog.domain.Post;
import com.woolog.request.post.PagingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.woolog.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PageImpl<Post> getPagingList(PagingRequest pagingRequest) {

        Long totalCount = jpaQueryFactory.select(post.count())
                .from(post)
                .where(containsKeyword(pagingRequest.getKeyword()))
                .fetchFirst();

        List<Post> items = jpaQueryFactory.selectFrom(post)
                .limit(pagingRequest.getSize())
                .offset(pagingRequest.getOffset())
                .where(containsKeyword(pagingRequest.getKeyword()))
                .on()
                .orderBy(post.id.desc())
                .fetch();

        return new PageImpl<>(items, pagingRequest.getPageable(), totalCount);
    }

    private BooleanExpression containsKeyword(String keyword) {

        if (StringUtils.hasText(keyword)) {
            return post.title.contains(keyword);
        }
        return null;
    }
}