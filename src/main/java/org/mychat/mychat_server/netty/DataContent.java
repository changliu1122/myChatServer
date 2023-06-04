package org.mychat.mychat_server.netty;

import java.io.Serializable;

public class DataContent implements Serializable {
    // what action
    private Integer action;

    //the content from front-end contains : message, userId, receiverId... string is not enough
    private ChatMSG chatMSG;

    //extend string
    private String extend;


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
