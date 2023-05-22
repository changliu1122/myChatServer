package org.mychat.mychat_server.services.impl;

import IdGenerator.ModifiedSnowFlake;
import org.mychat.mychat_server.mapper.FriendsRequestMapper;
import org.mychat.mychat_server.mapper.MyFriendsMapper;
import org.mychat.mychat_server.mapper.UserMapper;
import org.mychat.mychat_server.mapper.UserMapperCustom;
import org.mychat.mychat_server.pojo.FriendsRequest;
import org.mychat.mychat_server.pojo.MyFriends;
import org.mychat.mychat_server.pojo.User;
import org.mychat.mychat_server.services.UserServices;
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
    }

    private void saveFriend(String myId, String friendId){
        //create new entry
        MyFriends myFriends = new MyFriends();
        myFriends.setMyUserId(myId);
        myFriends.setMyFriendUserId(friendId);
        var id = snowFlake.getString_nextId();
        myFriends.setId(id);
        myFriendsMapper.insert(myFriends);
    }

    @Override
    public void declineFriendRequest(FriendsRequest friendsRequest) {
        friendsRequestMapper.deleteFriendRequest(friendsRequest);
    }

    @Override
    public List<MyFriendsVo> queryFriendList(String myId) {
        return userMapperCustom.queryFriendList(myId);
    }

}
