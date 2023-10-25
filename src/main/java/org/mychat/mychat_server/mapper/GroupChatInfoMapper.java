package org.mychat.mychat_server.mapper;

import org.mychat.mychat_server.pojo.GroupChatInfo;
import org.springframework.stereotype.Component;

@Component
public interface GroupChatInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(GroupChatInfo row);

    int insertSelective(GroupChatInfo row);

    GroupChatInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GroupChatInfo row);

    int updateByPrimaryKeyWithBLOBs(GroupChatInfo row);

    int updateByPrimaryKey(GroupChatInfo row);
}