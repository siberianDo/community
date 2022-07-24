package com.our.community.dao.mapper;

import com.our.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 根据实体类型和ID分页查询
     *
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentList(int entityType, int entityId, int offset, int limit);


    /**
     * 根据实体类型查询评论的条数
     *
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCommentRows(int entityType, int entityId);


    /**
     * 添加评论的同时也要对帖子的对应评论数进行更新，eg.帖子的评论数
     *
     * @param comment
     * @return
     */
    int insertComment(Comment comment);

}
