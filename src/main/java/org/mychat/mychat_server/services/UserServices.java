package org.mychat.mychat_server.services;

import org.mychat.mychat_server.pojo.User;

public interface UserServices {
    User getUserById(String id);
}
