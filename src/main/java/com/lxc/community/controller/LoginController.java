package com.lxc.community.controller;

import com.lxc.community.entity.User;
import com.lxc.community.service.UserService;
import com.lxc.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    //访问登录界面
    public String getLoginPage(){
        return "site/login";
    }

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    //访问注册界面
    public String getRrgisterPage(){
        return "site/register";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    //进行注册
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功,已向您的邮箱发送激活邮件,请您激活!");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameProblem",map.get("usernameProblem"));
            model.addAttribute("passwordProblem",map.get("passwordProblem"));
            model.addAttribute("emailProblem",map.get("emailProblem"));
          return "/site/register";
        }
    }

    //想要标准些的地址 http://localhost:8080/community/activation/id/activationcode
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    //返回激活状态
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功,您的账号已可进行登录!");
            model.addAttribute("target","/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg","无效操作,该账号已经激活!");
            model.addAttribute("target","/index");
        } else {
            model.addAttribute("msg","激活失败,您提供的激活码不正确!");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

}
