package org.mychat.mychat_server.controller;

import org.mychat.mychat_server.pojo.User;
import org.mychat.mychat_server.services.UserServices;
import org.mychat.mychat_server.utils.MD5Utils;
import org.mychat.mychat_server.utils.MyChatServerJSONResult;
import org.mychat.mychat_server.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
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



    @RequestMapping("/login")
    @ResponseBody
    public MyChatServerJSONResult Login(@RequestBody User user){//axios needs @RequestBody
        var name =  user.getUsername();
        System.out.println(name+"----");


        User userResult = userServices.queryUsername(user.getUsername());
        // user already exist
        if(userResult != null){
            var pwd = user.getPassword();
            var pwd_database = userResult.getPassword();
            // encryption then compare
            // wrong pwd!!
            if(!pwd_database.equals(MD5Utils.getPwd(pwd))){
                return MyChatServerJSONResult.errorMsg("wrong password!");
            }
        }
        //new user , sign up first!
        else{
            return new MyChatServerJSONResult(201,"Please sign in!",null);
        }

        UserVo userVo = new UserVo();
        // copy properties without password
        BeanUtils.copyProperties(userResult,userVo);
        return MyChatServerJSONResult.ok(userVo);
    }


}
