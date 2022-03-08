package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.UserService;
import com.nowcoder.community.community.util.CommunityConstent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstent {
    @Autowired
    private UserService userService;

    @RequestMapping(path="/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path="/login",method = RequestMethod.GET)
    public String getloginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功,已经向您的邮箱发送一封激活邮件,请尽快激活!");
            model.addAttribute("target","/index");
            return"/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("username message"));
            model.addAttribute("passwordMsg",map.get("password message"));
            model.addAttribute("emailMsg",map.get("email message"));
            return"/site/register";
        }
    }
    // http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userid}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userid") int userid,@PathVariable("code") String code){
        int result = userService.activation(userid,code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，账号可以正常使用!");
            model.addAttribute("target","/login");

        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账号已激活!");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","激活失败，激活码不正确!");
            model.addAttribute("target","/index");
        }
        return"/site/operate-result";
    }
}
