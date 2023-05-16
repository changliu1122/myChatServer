package org.mychat.mychat_server.services.impl;

import IdGenerator.ModifiedSnowFlake;
import org.mychat.mychat_server.mapper.UserMapper;
import org.mychat.mychat_server.pojo.User;
import org.mychat.mychat_server.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServicesImpl implements UserServices {
    @Autowired
    UserMapper userMapper;


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
    public User signUp(User user) {
        user.setId(snowFlake.getString_nextId());
        userMapper.insert(user);
        return user;
    }
}
