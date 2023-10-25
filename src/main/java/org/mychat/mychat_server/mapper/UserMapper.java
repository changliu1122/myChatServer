package org.mychat.mychat_server.mapper;

import org.mychat.mychat_server.pojo.User;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper {
    int deleteByPrimaryKey(String id);

    int insert(User row);

    int insertSelective(User row);

    User selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(User row);

    int updateByPrimaryKey(User row);

    User queryUsername(String username);
}