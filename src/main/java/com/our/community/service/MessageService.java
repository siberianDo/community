package com.our.community.service;

import com.our.community.dao.mapper.MessageMapper;
import com.our.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;


    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findDetailByConversationId(String conversationId, int offset, int limit) {
        return messageMapper.selectDetailByConversationId(conversationId, offset, limit);
    }

    public int findDetailConversationCount(String conversationId) {
        return messageMapper.selectDetailConversationCount(conversationId);
    }

    public int findUnreadConversation(int userId, String conversationId) {
        return messageMapper.selectUnreadConversation(userId, conversationId);
    }
}
