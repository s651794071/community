package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表，针对每个会话返回一条最新的的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话数量
    int selectConversationsCount(int userId);

    // 查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话所包含的私信数量
    int selectLettersCount(String conversationId);

    // 查询未读私信的数量
    int selectLettersUnreadCount(int userId, String conversationId);
    // 这个conversationId动态拼接，不一定传，因为我们这里想查所有未读私信的数量和每个私信的未读数量

}
