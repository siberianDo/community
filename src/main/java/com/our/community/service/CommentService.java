package com.our.community.service;

import com.our.community.dao.mapper.CommentMapper;
import com.our.community.entity.Comment;
import com.our.community.util.CommunityConstant;
import com.our.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;


@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;


    /**
     * 查询评论并分页，根据评论类型和ID
     *
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    public List<Comment> findCommentEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentList(entityType, entityId, offset, limit);
    }

    /**
     * 查询评论数量根据评论类型和ID
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public int selectCommentRowsByEntityTypeAndId(int entityType, int entityId) {
        return commentMapper.selectCommentRows(entityType, entityId);
    }

    /**
     * 增加评论
     *
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int insertComment(Comment comment) {

        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        //过滤标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //过滤敏感词
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        int rows = commentMapper.insertComment(comment);

        //更新评论数量（只有在评论帖子的时候才更新评论数量，当评论评论的时候不增加评论数量）
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int commentCount = commentMapper.selectCommentRows(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), commentCount);
        }

        return rows;

    }

}
