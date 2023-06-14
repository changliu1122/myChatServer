package org.mychat.mychat_server.controller;
import org.mychat.mychat_server.netty.ChatMSG;
import org.mychat.mychat_server.pojo.ChatMsg;
import org.mychat.mychat_server.pojo.FriendsRequest;
import org.mychat.mychat_server.pojo.User;
import org.mychat.mychat_server.services.UserServices;
import org.mychat.mychat_server.utils.MD5Utils;
import org.mychat.mychat_server.utils.MyChatServerJSONResult;
import org.mychat.mychat_server.vo.FriendsRequestVo;
import org.mychat.mychat_server.vo.MyFriendsVo;
import org.mychat.mychat_server.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping("/signup")
    @ResponseBody
    // combine login and registration pages
    public MyChatServerJSONResult signup(@RequestBody User user){

        User resultFromSQL = userServices.queryUsername(user.getUsername());
        if(resultFromSQL != null){ //username exist, should get another name
            return MyChatServerJSONResult.errorMsg("Username already exist!");//500
        }
        else{// not exist, registration!
            user.setNickname("not now");
            user.setQrcode("not now");
            user.setPassword(MD5Utils.getPwd(user.getPassword()));
            user.setFaceImage("not now");
            user.setFaceImageBig("not now");
            // because resultFromSQL is null, no such user exist, create one
            resultFromSQL = userServices.insert(user);
        }
        return new MyChatServerJSONResult(200,"Welcome! Now login and start chatting!",null);
    }

    @RequestMapping("/searchFriend")
    @ResponseBody
    public MyChatServerJSONResult searchFriend(@RequestParam String myId, @RequestParam String friendUserName){

        int status = userServices.checkPrecondition(myId,friendUserName);

        switch (status) {
            case 0 -> {//friend exist?
                return MyChatServerJSONResult.errorMsg("Friend does not exist");
            }
            case 1 -> {//friend is myself?
                return MyChatServerJSONResult.errorMsg("Can not add yourself");
            }
            case 2 -> {//already your friend?
                return MyChatServerJSONResult.errorMsg("You are already friends");
            }
            default -> { // a valid friend request
                User friend = userServices.queryUsername(friendUserName);
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(friend, userVo);
                return MyChatServerJSONResult.ok(userVo);
            }
        }
    }

    @RequestMapping("/sendFriendRequest")
    @ResponseBody
    public MyChatServerJSONResult sendFriendRequest( @RequestParam String myId,@RequestParam String friendUserName){

        var result = userServices.sendFriendRequest(myId,friendUserName);
        if(result == 0){
            return MyChatServerJSONResult.errorMsg("Request already sent!");
        }
        return MyChatServerJSONResult.ok();
    }


    //check friend request list
    @RequestMapping("/queryFriendRequest")
    @ResponseBody
    public MyChatServerJSONResult queryFriendRequests (@RequestBody String myId){
        //may have many friend requests

        List<FriendsRequestVo> list = userServices.queryFriendRequests(myId);
        return MyChatServerJSONResult.ok(list);
    }


    //ignore or accept friend request
    @RequestMapping("/operFriendRequest")
    @ResponseBody
    public MyChatServerJSONResult opFriendRequest(@RequestParam String acceptUserId,@RequestParam String sendUserId,@RequestParam Integer op){

        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setSendUserId(sendUserId);
        friendsRequest.setAcceptUserId(acceptUserId);
        //op == 0 decline; op == 1 accept
        if(op == 1){
            userServices.passFriendRequest(friendsRequest);
        }
        //whether accept or decline, the entry should be removed
        userServices.declineFriendRequest(friendsRequest);

        List<FriendsRequestVo> list = userServices.queryFriendRequests(acceptUserId);
        return MyChatServerJSONResult.ok(list);
    }

    // for contact list rendering
    @RequestMapping("/myFriends")
    @ResponseBody
    public MyChatServerJSONResult queryFriendList(@RequestBody String myId){

        List<MyFriendsVo> list = userServices.queryFriendList(myId);
        return MyChatServerJSONResult.ok(list);
    }

    @RequestMapping("/changeNickname")
    @ResponseBody
    public MyChatServerJSONResult changeNickname(@RequestBody User user){
        User newUser = userServices.updataUserInfo(user);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(newUser,userVo);
        return MyChatServerJSONResult.ok(userVo);
    }

    @RequestMapping("/getUnreadMsg")
    @ResponseBody
    public MyChatServerJSONResult getUnreadMsg(@RequestBody String accepterId){

        var result = userServices.getUnreadMsg(accepterId);
        return MyChatServerJSONResult.ok(result);
    }


}
