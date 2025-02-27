package com.lxc.community.service;
//增加显示帖子评论功能 功能写到DiscussPostController中的查询帖子里
import com.lxc.community.dao.CommentMapper;
import com.lxc.community.entity.Comment;
import com.lxc.community.util.CommunityConstant;
import com.lxc.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    public int findCommentByCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    //添加评论
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if (comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //添加评论信息
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));//将html编码转义,防止破坏代码完整性
        comment.setContent(sensitiveFilter.filter(comment.getContent()));//屏蔽词过滤
        int rows = commentMapper.insertComment(comment);

        //更新帖子的评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST){//判断类型为帖子还是评论
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }

        return rows;
    }
}
