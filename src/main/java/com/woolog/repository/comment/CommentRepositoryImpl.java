package com.woolog.repository.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woolog.domain.Comment;
import com.woolog.request.post.PagingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static com.woolog.domain.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PageImpl<Comment> getList(PagingRequest pagingRequest, Long postId) {

        Long totalCount = jpaQueryFactory.select(comment.count())
                .from(comment)
                .where(comment.post.id.eq(postId))
                .fetchFirst();

        List<Comment> items = jpaQueryFactory.selectFrom(comment)
                .limit(pagingRequest.getSize())
                .offset(pagingRequest.getOffset())
                .where(comment.post.id.eq(postId))
                .orderBy(comment.id.desc())
                .fetch();

        return new PageImpl<>(items, pagingRequest.getPageable(), totalCount);
    }
}
