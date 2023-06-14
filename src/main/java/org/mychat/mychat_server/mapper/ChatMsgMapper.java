package org.mychat.mychat_server.mapper;

import org.mychat.mychat_server.netty.ChatMSG;
import org.mychat.mychat_server.pojo.ChatMsg;

import java.util.List;

public interface ChatMsgMapper {
    int deleteByPrimaryKey(String id);

    int insert(ChatMsg row);

    int insertSelective(ChatMsg row);

    ChatMsg selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ChatMsg row);

    int updateByPrimaryKey(ChatMsg row);

    List<ChatMsg> getUnreadMsg(String accepterId);
}