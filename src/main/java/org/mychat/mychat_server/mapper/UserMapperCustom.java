package org.mychat.mychat_server.mapper;
import org.mychat.mychat_server.vo.FriendsRequestVo;
import org.mychat.mychat_server.vo.MyFriendsVo;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface UserMapperCustom {
    List<FriendsRequestVo> queryFriendRequestList(String acceptUserId);

    List<MyFriendsVo> queryFriendList(String myId);

    void batchUpdateMsgSign(List<String> msgIdList);
}