package org.mychat.mychat_server.controller;

import org.mychat.mychat_server.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class userController {

    @Autowired
    UserServices userServices;
    @RequestMapping("/getUser")
    public String getUserById(String id, Model model){
        var user = userServices.getUserById(id);

        model.addAttribute("user",user);
        return "user_list";
    }
}
