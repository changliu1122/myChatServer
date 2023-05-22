package org.mychat.mychat_server.mapper;

import org.mychat.mychat_server.pojo.FriendsRequest;

import java.util.List;

public interface FriendsRequestMapper {
    int deleteByPrimaryKey(String id);

    int insert(FriendsRequest row);

    int insertSelective(FriendsRequest row);

    FriendsRequest selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FriendsRequest row);

    int updateByPrimaryKey(FriendsRequest row);

    void deleteFriendRequest(FriendsRequest friendsRequest);

    List<String> queryRepeatRequest(String myId, String accept_user_id);
}