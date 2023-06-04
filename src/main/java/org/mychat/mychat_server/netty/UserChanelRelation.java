package org.mychat.mychat_server.netty;


import io.netty.channel.Channel;

import java.util.HashMap;

/**
 *  bind userId with channel
 */
public class UserChanelRelation {
    private static HashMap<String, Channel> manage = new HashMap<String, Channel>();
    public static void put(String senderId, Channel channel){
        manage.put(senderId,channel);
    }

    // get receiver channel
    public static Channel get(String receiverId){
        return manage.get(receiverId);
    }

}
