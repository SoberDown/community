package com.lxc.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;//登录凭证
    private int status;//状态 登录有效还是无效
    private Date expired;//过期时间
}
