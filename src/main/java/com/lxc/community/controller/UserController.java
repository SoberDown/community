package com.lxc.community.controller;

import com.lxc.community.annotation.LoginRequired;
import com.lxc.community.dao.UserMapper;
import com.lxc.community.entity.User;
import com.lxc.community.service.FollowService;
import com.lxc.community.service.LikeService;
import com.lxc.community.service.UserService;
import com.lxc.community.util.CommunityConstant;
import com.lxc.community.util.CommunityUtil;
import com.lxc.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.message.MultiformatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.Scanner;

@Component
@RequestMapping(path = "/user")
public class UserController implements CommunityConstant {

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    //访问账号设置界面
    @LoginRequired//有这个注解代表的方法,需要登录才能访问
    @RequestMapping(path = "/setting" ,method = RequestMethod.GET)
    public String userSetting(){
        return "/site/setting";
    }

    /**
     * 上传头像
     */
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        //空值判断
        if (headerImage == null){
            model.addAttribute("HeaderProblem","您还未选择文件!");
            return "/site/setting";
        }
        //拿到头像文件的真实文件名
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));//拿到文件的文件后缀
        if (StringUtils.isBlank(suffix)){//检查字符串是否为空白
            model.addAttribute("HeaderProblem","您的文件格式不正确!");
            return "/site/setting";
        }
        //生成随机的图像名称
        fileName = CommunityUtil.setUUID() + suffix;
        //文件存储的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败");
        }

        //更新当前用户的头像访问路径
        //http://localhost:8080/community/user/header/xxx.jpg
        User user = hostHolder.getUser();//获取当前登录用户
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userMapper.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    /**
     * 读取头像
     */
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        //服务器文件存放的路径
        fileName = uploadPath + "/" + fileName;
        //拿到文件的文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //设置响应的内容类型为图片
        response.setContentType("image/" + suffix);
        try (
                /**
                 * 创建一个输入流
                 * 因为输入流是手动创建的,所以需要手动关闭,
                 * 在jdk7以后的版本里,写在try后()里则表示会在最后自动关闭(close)
                 * */
                FileInputStream fis = new FileInputStream(fileName);
         ) {
            // 创建输出流
            OutputStream os = response.getOutputStream();
//            // 创建一个输入流
//            FileInputStream fis = new FileInputStream(fileName);
            // 创建一个缓冲区数组
            byte[] buffer = new byte[1024];//缓冲区最多读取10244哥字节
            // 读取数据到缓冲区
            int bytesRead = 0;
            while ((bytesRead = fis.read(buffer)) != -1) {//.read方法不断的返回一个返回值,返回值为-1时代表没有数据可读取了,即读取完毕
            // 处理读取的数据
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            logger.error("读取头像失败:"+e.getMessage());
        }
    }

    /**
     修改密码
     */
    @RequestMapping(path = "/updatePassword",method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String rePassword, Model model,@CookieValue("ticket") String ticket){

        //空值处理
        if (StringUtils.isBlank(oldPassword)){
            model.addAttribute("oldPasswordProblem","原密码不能为空!");
            return "site/setting";
        }
        if (StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordProblem","新密码不能为空!");
            return "site/setting";
        }
        if (StringUtils.isBlank(rePassword)){
            model.addAttribute("rePasswordProblem","确认密码不能为空!");
            return "site/setting";
        }

        //获取当前登录用户
        User user = hostHolder.getUser();
        //判断用户输入的原密码是否与存储的原密码一致
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!oldPassword.equals(user.getPassword())){
            model.addAttribute("oldPasswordProblem","该密码与原密码不符!");
            return "site/setting";
        }
        //判断新输入的密码和原密码是否一致
        newPassword = CommunityUtil.md5(newPassword+user.getSalt());
        if (newPassword.equals(user.getPassword())){
            model.addAttribute("newPasswordProblem","新密码与原密码一致!");
            return "site/setting";
        }
        //对确认密码进行加密,并进行加密判断
        rePassword = CommunityUtil.md5(rePassword+user.getSalt());
        if (!newPassword.equals(rePassword)){
            model.addAttribute("rePasswordProblem","两次密码不一致!");
            return "site/setting";
        }
        userService.updatePassword(user.getId(),newPassword);
        //修改密码成功后,需要进行登出操作
        model.addAttribute("updateSucceed","修改成功!请您重新登录！");
        userService.logout(ticket);
        return "redirect:/login";
    }

    /**
     * 个人主页
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new IllegalArgumentException("该用户不存在!");
        }

        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }

}
