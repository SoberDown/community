package com.lxc.community.service;
//增加显示帖子评论功能 功能写到DiscussPostController中的查询帖子里
import com.lxc.community.dao.CommentMapper;
import com.lxc.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    public int findCommentByCount(int entityType,int entityId){
        return commentMapper.selectCommentByCount(entityType,entityId);
    }
}
