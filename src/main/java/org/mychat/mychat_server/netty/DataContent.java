package org.mychat.mychat_server.netty;

import java.io.Serializable;
import java.util.List;

public class DataContent implements Serializable {
    // what action
    private Integer action;

    //the content from front-end contains : message, userId, receiverId... string is not enough
    private ChatMSG chatMSG;

    //extend string
    private String extend;

    private GroupChat groupChat;

    public GroupChat getGroupChat() {
        return groupChat;
    }

    public void setGroupChat(GroupChat groupChat) {
        this.groupChat = groupChat;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatMSG getChatMSG() {
        return chatMSG;
    }

    public void setChatMSG(ChatMSG chatMSG) {
        this.chatMSG = chatMSG;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
