package com.lxc.community.dao;
//增加显示帖子评论功能
import com.lxc.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);

    int selectCountByEntity(int entityType,int entityId);

    //添加评论
    int insertComment(Comment comment);

    Comment selectCommentById(int Id);
}
