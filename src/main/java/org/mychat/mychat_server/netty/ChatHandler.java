package org.mychat.mychat_server.netty;

import com.alibaba.fastjson2.JSONObject;
import io.micrometer.common.util.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.mychat.mychat_server.SpringUtil;
import org.mychat.mychat_server.enums.MsgActionEnum;
import org.mychat.mychat_server.services.UserServices;
import org.mychat.mychat_server.services.impl.UserServicesImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * this is where we handle chat message from client
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup usersGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // msg from client
        // not only the msg, but also the userId, send to whom....
        String content = msg.text();
        System.out.println(content);
        // a package of the whole information
        DataContent dataContent = JSONObject.parseObject(content, DataContent.class);

        Integer action = dataContent.getAction();
        System.out.println("action:  "+ action);

        // get the channel of this user
        Channel UserChannel = ctx.channel();

        // decide which type does this msg have
        //1. initialize channel with userId at the websocket first open
        if(action == MsgActionEnum.CONNECT.type){

            String senderId = dataContent.getChatMSG().getSenderId();
            UserChanelRelation.put(senderId, UserChannel);

        }
        //2. chat history should be encrypted, stored in database, and record ths status [not delivered]
        else if (action == MsgActionEnum.CHAT.type) {
            // get message information from package(dataContent)
            ChatMSG chatMSG = dataContent.getChatMSG();
            String messageContent = chatMSG.getMsg();
            System.out.println("messageContent:  "+ messageContent);
            String senderId = chatMSG.getSenderId();
            String receiverId = chatMSG.getReceiverId();





            // save to database, tag"not delivered"
            UserServices userServices = (UserServices) SpringUtil.getBean(UserServicesImpl.class);
            // create method in userservices interface
            String msgId = userServices.saveMsg(chatMSG);
            // every chat should have its own id in database
            chatMSG.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMSG(chatMSG);

            // send this message to receiver
            // get receiver channel
            Channel receiverChannel = UserChanelRelation.get(receiverId);
            if(receiverChannel == null){
                // receiver is offline!!!!!
                System.out.println("offline 1");

            }else{

                //如果简单一点写 不保存聊天记录 就只能俩人都在线的时候发消息 离线再发消息 收不到
                // find out this receiverChannel in ChannelGroup
                Channel receiverChannelInChannelGroup = usersGroup.find(receiverChannel.id());
                System.out.println(receiverChannelInChannelGroup);

                if(receiverChannelInChannelGroup != null){
                    System.out.println("online");
                    // this receiver is online sending message !!!!!!!!!
                    receiverChannelInChannelGroup.writeAndFlush(
                            new TextWebSocketFrame(

                                    JSONObject.toJSONString(dataContentMsg)
                            )
                    );

                }else{
                    System.out.println("offline 2");
                    // offline

                }

            }

        }

        //3.receiver received the messages,then we need to change all these messages' status in the database[delivered]
        else if (action == MsgActionEnum.SIGNED.type) {
            // get spring bean object
            // the class with @Service annotation, lower class
            UserServices userServices = (UserServices)SpringUtil.getBean(UserServicesImpl.class);
            //扩展字段在signed 类型消息中 ，代表需要去签收的消息id，逗号间隔
            String msgIdsStr = dataContent.getExtend();
            String[] msgsId = msgIdsStr.split(",");

            List<String> msgIdList = new ArrayList<>();
            for (String mid: msgsId) {
                if(StringUtils.isNotBlank(mid)){
                    msgIdList.add(mid);
                }
            }


            if(msgIdList!=null && !msgIdList.isEmpty() && msgIdList.size()>0){
                //批量签收
                // create this method in interface
                userServices.updateMsgSigned(msgIdList);
            }

        }

        //4. heartbeat
        else if (action == MsgActionEnum.KEEPALIVE.type){

        }











    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        usersGroup.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        usersGroup.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // error occurs, connection must be shut down and remove from channelgroup
        ctx.channel().close();
        usersGroup.remove(ctx.channel());
    }
}
