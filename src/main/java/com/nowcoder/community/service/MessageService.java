package com.nowcoder.community.service;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.mapper.MessageMapper;
import org.apache.ibatis.annotations.Mapper;
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

    public int findConversationsCount(int userId) {
        return  messageMapper.selectConversationsCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLettersCount(String conversationId) {
        return  messageMapper.selectLettersCount(conversationId);
    }

    public int findLettersUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLettersUnreadCount(userId, conversationId);
    }
}
