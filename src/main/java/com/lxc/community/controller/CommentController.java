package com.lxc.community.controller;

import com.lxc.community.entity.Comment;
import com.lxc.community.entity.DiscussPost;
import com.lxc.community.entity.Event;
import com.lxc.community.event.EventProduce;
import com.lxc.community.service.CommentService;
import com.lxc.community.service.DiscussPostService;
import com.lxc.community.util.CommunityConstant;
import com.lxc.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProduce eventProduce;

    @Autowired
    private DiscussPostService discussPostService;

    //添加评论 视图层
    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        /*
        触发评论事件 添加通知
         */
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);//存入帖子id
        //判断评论的类型是帖子还是回复
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            //得到评论的目标对象
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            //得到回复的目标对象
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        //发送通知
        eventProduce.fireEvent(event);

        return "redirect:/discuss/detail/" + discussPostId;
    }

}
