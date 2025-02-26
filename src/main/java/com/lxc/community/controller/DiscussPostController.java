package com.lxc.community.controller;

import com.lxc.community.entity.Comment;
import com.lxc.community.entity.DiscussPost;
import com.lxc.community.entity.Page;
import com.lxc.community.entity.User;
import com.lxc.community.service.CommentService;
import com.lxc.community.service.DiscussPostService;
import com.lxc.community.service.UserService;
import com.lxc.community.util.CommunityConstant;
import com.lxc.community.util.CommunityUtil;
import com.lxc.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant{

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    /**
     * 发布帖子
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUtil.getJSONString(403,"您还未登录!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //报错情况后面同意处理
        return CommunityUtil.getJSONString(0,"发布成功");
    }

    /**
     * 查询帖子信息方法 查询评论方法 查询回复方法
     * @param discussPostId
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //查询帖子的作者用于展示
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        /*
        查询评论分页信息
         */
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());//从帖子中查找

        //评论:给帖子的评论
        //回复:给评论的评论
        /**
         * 评论列表
         */
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST,post.getId(),page.getOffset(),page.getLimit());
        //评论的显示层对象列表
        List<Map<String,Object>> commentVOList = new ArrayList<>();
        if (commentVOList != null){
            for (Comment comment : commentList) {
                //一个评论的显示层对象
                Map<String,Object> commentVO = new HashMap<>();
                //向评论的显示层对象中,添加评论内容
                commentVO.put("comment",comment);
                //向评论的显示层对象中,添加评论作者
                commentVO.put("user",userService.findUserById(comment.getUserId()));

                /**
                 * 回复列表
                 */
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);//回复就不分页,有多少差多少
                //回复的显示层对象列表
                List<Map<String,Object>> replyVOList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply : replyList) {
                        //一个回复的显示层对象
                        Map<String,Object> replyVO = new HashMap<>();
                        //向回复的显示层对象中,添加回复内容
                        replyVO.put("reply",reply);
                        //向回复的显示层对象中,添加回复作者
                        replyVO.put("user",userService.findUserById(reply.getUserId()));
                        //查回复目标的user,看是否拉黑;若查到target!=0,则代表已拉黑
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());//
                        replyVO.put("target",target);

                        replyVOList.add(replyVO);
                    }
                }
                //把回复对象列表装到评论的VO对象中;此时回复的Vo对象中 不止有评论的信息,还有回复的信息
                commentVO.put("replys",replyVOList);
                //每个评论的回复数量
                int replyCount = commentService.findCommentByCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replyCount",replyCount);

                commentVOList.add(commentVO);
            }
        }
        model.addAttribute("comments",commentVOList);

        return "/site/discuss-detail";
    }

}
