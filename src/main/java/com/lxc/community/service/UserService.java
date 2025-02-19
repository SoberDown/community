package com.lxc.community.service;

import com.lxc.community.dao.UserMapper;
import com.lxc.community.entity.User;
import com.lxc.community.util.CommunityConstant;
import com.lxc.community.util.CommunityUtil;
import com.lxc.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    //注入邮件客户端
    private MailClient mailClient;

    @Autowired
    //注入模板引擎
    private TemplateEngine templateEngine;

    @Value("${server.servlet.context-path}")
    //注入项目名
    private String contextPath;

    @Value("${community.path.domain}")
    //注入域名(目前使用本机地址)
    private String domain;


    public User findUserById(int id){
        return userMapper.selectById(id);
    }


    /**
    注册业务
     */
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();

        //对传入的值进行处理,空值处理
        if (user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameProblem","账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordProblem","密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailProblem","邮箱不能为空!");
            return map;
        }

        //验证账号是否存在
        User u = userMapper.selectByName(user.getUsername());
        if (u != null){
            map.put("usernameProblem","该账号已存在!");
            return map;
        }

        //验证邮箱是否存在
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null){
            map.put("emailProblem","该邮箱已被注册!");
            return map;
        }

        /**
        注册用户
         */
        user.setSalt(CommunityUtil.setUUID().substring(0,5));//生成的随机字符串取5位
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));//覆盖密码 拼接md5加密过的密码和随机5位字符串
        user.setType(0);//普通用户
        user.setStatus(0);//未激活
        user.setActivationCode(CommunityUtil.setUUID());//激活码
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));//%d为String.format的整数占位符,使用后面的随机数进行替换
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        /**
        发送激活邮件 邮件模板里有两个变量,所以需要赋值两次动态数据
         */
        Context context = new Context();
        //context里添加email
        context.setVariable("email",user.getEmail());
            //想要标准些的地址 http://localhost:8080/community/activation/id/activationcode
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        //context里添加url
        context.setVariable("url",url);
        //利用模板引擎,生成邮件内容
        String acemail = templateEngine.process("mail/activation", context);
//        System.out.println(acemail);
        //发送邮件
        mailClient.sendMail(user.getEmail(), "激活账号",acemail);

        return map;
    }

    /**
    回显激活状态
     */
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;//重复激活
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;//激活成功
        } else {
            return ACTIVATION_FAILURE;//激活失败
        }

    }

}
