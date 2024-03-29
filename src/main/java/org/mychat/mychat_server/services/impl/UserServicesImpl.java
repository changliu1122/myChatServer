package org.mychat.mychat_server.services.impl;

import IdGenerator.ModifiedSnowFlake;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.mychat.mychat_server.enums.MsgActionEnum;
import org.mychat.mychat_server.enums.MsgSignFlagEnum;
import org.mychat.mychat_server.mapper.*;
import org.mychat.mychat_server.netty.ChatMSG;
import org.mychat.mychat_server.netty.DataContent;
import org.mychat.mychat_server.netty.GroupChat;
import org.mychat.mychat_server.netty.UserChanelRelation;
import org.mychat.mychat_server.pojo.*;
import org.mychat.mychat_server.services.UserServices;
import org.mychat.mychat_server.vo.ChatMsgWithGroupInfo;
import org.mychat.mychat_server.vo.FriendsRequestVo;
import org.mychat.mychat_server.vo.MyFriendsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServicesImpl implements UserServices {
    @Autowired
    UserMapper userMapper;

    @Autowired
    MyFriendsMapper myFriendsMapper;
    @Autowired
    FriendsRequestMapper friendsRequestMapper;
    @Autowired
    UserMapperCustom userMapperCustom;

    @Autowired
    ChatMsgMapper chatMsgMapper;

    @Autowired
    GroupChatInfoMapper groupChatInfoMapper;


    ModifiedSnowFlake snowFlake = new ModifiedSnowFlake(1,1);
    @Override
    public User getUserById(String id) {
        return userMapper.selectByPrimaryKey(id);
    }

    // userMapper has the sql method in xml that we can use here
    @Override
    public User queryUsername(String username) {
        return userMapper.queryUsername(username);
    }

    @Override
    public User insert(User user) {
        user.setId(snowFlake.getString_nextId());
        userMapper.insert(user);
        return user;
    }

    @Override
    public int checkPrecondition(String myId, String friendUserName) {
        //friend exist?
        var user = userMapper.queryUsername(friendUserName);
        if(user == null){
            return 0;
        }
        //friend is myself?
        if(user.getId().equals(myId)){
            return 1;
        }
        //already your friend? 也就是说要在my_friends 这个表里面查找一条数据 这条数据包含了 my_user_id 和 my_friend_user_id,
        // 这条数据表明 一个id 与 另一个id 互为好友
        //myfriends 这个类 就是一条数据 代表 两个 id 互为好友
        MyFriends myFriend = new MyFriends();
        myFriend.setMyUserId(myId);
        myFriend.setMyFriendUserId(user.getId());
        var result = myFriendsMapper.selectOneByExample(myFriend);
        if(result != null){
            return 2;
        }

        //a valid friend request
        return 3;
    }

    @Override
    public int sendFriendRequest(String myId, String friendUserName) {
        User friend = userMapper.queryUsername(friendUserName);
        String accept_user_id = friend.getId();
        //check if request already exist!
        List<String> repeatRequests = friendsRequestMapper.queryRepeatRequest(myId,accept_user_id);
        //does not exist keep going!
        if(repeatRequests.isEmpty()){
            FriendsRequest friendsRequest = new FriendsRequest();
            String id = snowFlake.getString_nextId();
            friendsRequest.setId(id);
            friendsRequest.setSendUserId(myId);
            friendsRequest.setAcceptUserId(friend.getId());
            friendsRequest.setRequestDateTime(new Date());
            friendsRequestMapper.insert(friendsRequest);

            //server notify the request receiver, when receiver is online, otherwise just
            // load all request when login, that there is a new friend request
            Channel receiveChannel = UserChanelRelation.get(friendsRequest.getAcceptUserId());
            if(receiveChannel != null){ //receiver is online!
                // update your friends contact list, should contain yourself in his list
                DataContent dataContent = new DataContent();
                dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);

                receiveChannel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(dataContent)));
            }
            return 1;
        }
        //does exist
        return 0;
    }

    @Override
    public List<FriendsRequestVo> queryFriendRequests(String myId) {
        return userMapperCustom.queryFriendRequestList(myId);
    }

    @Override
    public void passFriendRequest(FriendsRequest friendsRequest) {
        //save friend for both
        saveFriend(friendsRequest.getAcceptUserId(), friendsRequest.getSendUserId());
        saveFriend(friendsRequest.getSendUserId(), friendsRequest.getAcceptUserId());
        friendsRequestMapper.deleteFriendRequest(friendsRequest);

        // server notify the request sender actively, that the request was passed
        Channel sendChannel = UserChanelRelation.get(friendsRequest.getSendUserId());
        if(sendChannel != null){
            // update your friends contact list, should contain yourself in his list
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);

            sendChannel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(dataContent)));
        }
    }

    private void saveFriend(String myId, String friendId){
        //create new entry
        MyFriends myFriends = new MyFriends();
        myFriends.setMyUserId(myId);
        myFriends.setMyFriendUserId(friendId);
        var id = snowFlake.getString_nextId();
        myFriends.setId(id);
        myFriendsMapper.insertSelective(myFriends);
    }

    @Override
    public void declineFriendRequest(FriendsRequest friendsRequest) {
        friendsRequestMapper.deleteFriendRequest(friendsRequest);
    }

    @Override
    public List<MyFriendsVo> queryFriendList(String myId) {
        return userMapperCustom.queryFriendList(myId);
    }

    @Override
    public User updataUserInfo(User user) {
        userMapper.updateByPrimaryKeySelective(user);
        return userMapper.selectByPrimaryKey(user.getId());
    }

    @Override
    public String saveMsg(DataContent dataContent, boolean isGroupChat) {
        var chatMSG = dataContent.getChatMSG();
        // create pojo ChatMsg object
        ChatMsg msg = new ChatMsg();
        if(isGroupChat){
            msg.setSignFlag(MsgSignFlagEnum.unsignedGroupChat.type); //
            msg.setGroupId(dataContent.getGroupChat().getGroupId());
        }else{
            msg.setSignFlag(MsgSignFlagEnum.unsign.type);
        }

        String msgId = snowFlake.getString_nextId();
        msg.setId(msgId);
        msg.setAcceptUserId(chatMSG.getReceiverId());
        msg.setSendUserId(chatMSG.getSenderId());
        msg.setCreateTime(new Date());

        msg.setMsg(chatMSG.getMsg());

        chatMsgMapper.insert(msg);

        return msgId;
    }

    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        userMapperCustom.batchUpdateMsgSign(msgIdList);
    }

    @Override
    public List<ChatMsgWithGroupInfo> getUnreadMsg(String accepterId) {

        System.out.println("start check");
        List<ChatMsgWithGroupInfo> result = new ArrayList<>();
        var lst = chatMsgMapper.getUnreadMsg(accepterId);

        for(ChatMsg c : lst){
            ChatMsgWithGroupInfo chatMsgWithGroupInfo = new ChatMsgWithGroupInfo();
            chatMsgWithGroupInfo.setChatMsg(c);
            if(c.getGroupId()!= null){
                var groupId = c.getGroupId();
                GroupChatInfo g = groupChatInfoMapper.selectByPrimaryKey(groupId);
                GroupChat groupChat = new GroupChat();
                groupChat.setGroupId(groupId);
                groupChat.setGroupName(g.getGroupname());

                List<String> members = new ArrayList<>();

                String m = g.getGroupmembers();
                m = m.replaceAll("\\[","");
                m = m.replaceAll("\\]","");
                m = m.replaceAll("\"","");

                String[] s = m.split(",");
                for(int i = 0; i<s.length;i++){
                    members.add(s[i]);
                }
                groupChat.setGroupMembers(members);

                for(String i: groupChat.getGroupMembers()){
                    System.out.println(i);
                }

                chatMsgWithGroupInfo.setGroupChat(groupChat);
            }
            result.add(chatMsgWithGroupInfo);
        }
        return result;

    }
    @Override
    public String createGroup(GroupChatInfo groupChatInfo) {
        String id  = snowFlake.getString_nextId();
        groupChatInfo.setId(id);

        var m = groupChatInfo.getGroupmembers();
        m = m.replaceAll("\\[","");
        m = m.replaceAll("\\]","");
        m = m.replaceAll("\"","");

        groupChatInfo.setGroupmembers(m);

        groupChatInfoMapper.insert(groupChatInfo);

        return id;
    }

}
