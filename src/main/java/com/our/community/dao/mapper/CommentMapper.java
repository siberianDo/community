package com.our.community.dao.mapper;

import com.our.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 根据实体类型和ID分页查询
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentList(int entityType,int entityId,int offset,int limit);


    /**
     * 根据实体类型查询评论的条数
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCommentRows(int entityType,int entityId);

}
