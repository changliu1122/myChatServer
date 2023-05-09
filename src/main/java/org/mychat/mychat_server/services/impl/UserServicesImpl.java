package org.mychat.mychat_server.services.impl;

import org.mychat.mychat_server.mapper.UserMapper;
import org.mychat.mychat_server.pojo.User;
import org.mychat.mychat_server.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServicesImpl implements UserServices {
    @Autowired
    UserMapper userMapper;
    @Override
    public User getUserById(String id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
