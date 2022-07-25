package com.our.community.dao.mapper;

import com.our.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户的私信分页列表，每条列表显示最新的消息
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询会话总数量
    int selectConversationCount(int userId);

    //查询某具体会话的详细信息
    List<Message> selectDetailByConversationId(String conversationId, int offset, int limit);

    //查询某具体会话的信息数量
    int selectDetailConversationCount(String conversationId);

    //查询未读信息数量
    int selectUnreadConversation(int userId, String conversationId);

    //发送私信，查看私信后更新私信状态，设为已读状态
    int insertConversation(Message message);

    //更新状态
    int updateConversationStatus(List<Integer> ids, int status);
}
