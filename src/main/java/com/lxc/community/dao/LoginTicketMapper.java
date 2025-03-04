package com.lxc.community.dao;

import com.lxc.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated//把登录凭证存到redis中,现在这个不推荐使用
public interface LoginTicketMapper {

    @Insert({"insert into login_ticket(user_id, ticket, status, expired) " +
            "value(#{userId},#{ticket},#{status},#{expired}) "})
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({"select id,user_id,status,expired from login_ticket where ticket=#{ticket} "})
    LoginTicket selectByTicket(String ticket);

    @Update({"update login_ticket set status=#{status} where ticket=#{ticket} "})
    int updateStatus(String ticket, int status);

}
