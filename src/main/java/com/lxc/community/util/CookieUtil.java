package com.lxc.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.http.HttpRequest;

/**
 * 从request中取Cookie的值
 */
public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name){//要取的值的key是name
        //控制判断
        if (request == null || name == null){
            throw new IllegalArgumentException("参数为空!");
        }
        //遍历Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(name)){//判断取到Cookie的key,和传入的key是否一致
                    return cookie.getValue();//有一致的,返回这个key所对应的值
                }
            }
        }

        return null;//遍历完都一致的,返回null
    }
}
