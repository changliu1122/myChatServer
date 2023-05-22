package org.mychat.mychat_server.mapper;

import org.mychat.mychat_server.pojo.FriendsRequest;

public interface FriendsRequestMapper {
    int deleteByPrimaryKey(String id);

    int insert(FriendsRequest row);

    int insertSelective(FriendsRequest row);

    FriendsRequest selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FriendsRequest row);

    int updateByPrimaryKey(FriendsRequest row);

    void deleteFriendRequest(FriendsRequest friendsRequest);
}