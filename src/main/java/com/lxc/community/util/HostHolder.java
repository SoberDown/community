package com.lxc.community.util;

import com.lxc.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 并发代替session,持有用户信息
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
