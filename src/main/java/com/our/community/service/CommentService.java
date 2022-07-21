package com.our.community.service;

import com.our.community.dao.mapper.CommentMapper;
import com.our.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;


    public List<Comment> findCommentEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentList(entityType, entityId, offset, limit);
    }

    public int selectCommentRowsByEntityTypeAndId(int entityType, int entityId) {
        return commentMapper.selectCommentRows(entityType, entityId);
    }

}
