package org.mychat.mychat_server.vo;

import org.mychat.mychat_server.netty.GroupChat;
import org.mychat.mychat_server.pojo.ChatMsg;

public class ChatMsgWithGroupInfo {
    private ChatMsg chatMsg;
    private GroupChat groupChat;

    public ChatMsg getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(ChatMsg chatMsg) {
        this.chatMsg = chatMsg;
    }

    public GroupChat getGroupChat() {
        return groupChat;
    }

    public void setGroupChat(GroupChat groupChat) {
        this.groupChat = groupChat;
    }
}
