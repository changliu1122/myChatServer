package org.mychat.mychat_server.services;

import org.mychat.mychat_server.netty.ChatMSG;
import org.mychat.mychat_server.netty.DataContent;
import org.mychat.mychat_server.pojo.ChatMsg;
import org.mychat.mychat_server.pojo.FriendsRequest;
import org.mychat.mychat_server.pojo.GroupChatInfo;
import org.mychat.mychat_server.pojo.User;
import org.mychat.mychat_server.vo.ChatMsgWithGroupInfo;
import org.mychat.mychat_server.vo.FriendsRequestVo;
import org.mychat.mychat_server.vo.MyFriendsVo;

import java.util.List;

public interface UserServices {
    User getUserById(String id);


    User queryUsername(String username);

    User insert(User user);


    int checkPrecondition(String myId, String friendUserName);

    int sendFriendRequest(String myId, String friendUserName);

    List<FriendsRequestVo> queryFriendRequests(String myId);

    void passFriendRequest(FriendsRequest friendsRequest);

    void declineFriendRequest(FriendsRequest friendsRequest);

    List<MyFriendsVo> queryFriendList(String myId);

    User updataUserInfo(User user);

    String saveMsg(DataContent dataContent, boolean isGroupChat);

    void updateMsgSigned(List<String> msgIdList);

    List<ChatMsgWithGroupInfo> getUnreadMsg(String accepterId);

    String createGroup(GroupChatInfo groupChatInfo);
}
