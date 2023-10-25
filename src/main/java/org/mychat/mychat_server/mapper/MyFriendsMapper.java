package org.mychat.mychat_server.mapper;

import org.mychat.mychat_server.pojo.MyFriends;
import org.springframework.stereotype.Component;

@Component
public interface MyFriendsMapper {
    int deleteByPrimaryKey(String id);

    int insert(MyFriends row);

    int insertSelective(MyFriends row);

    MyFriends selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MyFriends row);

    int updateByPrimaryKey(MyFriends row);

    MyFriends selectOneByExample(MyFriends myFriend);
}