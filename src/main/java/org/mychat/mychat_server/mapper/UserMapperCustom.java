package org.mychat.mychat_server.mapper;
import org.mychat.mychat_server.vo.FriendsRequestVo;
import org.mychat.mychat_server.vo.MyFriendsVo;

import java.util.List;

public interface UserMapperCustom {
    List<FriendsRequestVo> queryFriendRequestList(String acceptUserId);

    List<MyFriendsVo> queryFriendList(String myId);
}