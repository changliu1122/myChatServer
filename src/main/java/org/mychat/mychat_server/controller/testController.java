package org.mychat.mychat_server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class testController {
    @RequestMapping("/test")
     public String test(){
         return "test";
     }

     @RequestMapping("/user")
    public String test2(){
        return "user_list";
     }
}
