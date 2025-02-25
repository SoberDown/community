package com.lxc.community.controller.interceptor;

import com.lxc.community.entity.LoginTicket;
import com.lxc.community.entity.User;
import com.lxc.community.service.UserService;
import com.lxc.community.util.CookieUtil;
import com.lxc.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从Cookie中获取登录凭证
        String ticket = CookieUtil.getValue(request,"ticket");
        //判断
        if (ticket!=null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效
            if (loginTicket!=null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                /**
                注意!数据库中login_ticket表里user_id字段对应的是user表里id字段,所以这里传入的数据应该是loginTicket.getUserId!!!!而不是loginTicket.getId
                 */
                User user = userService.findUserById(loginTicket.getUserId());
                //整个请求过程中都携带用户;每一个请求一个线程,如果一次请求多了会有并发问题,现在使用多线程隔离一下每个线程
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        //判断线程中是否有存入数据,modelAndView是否为空
        if (user!=null && modelAndView!=null){
            modelAndView.addObject("loginUser",user);//将对象添加到模型,在每一个中添加loginUser这个信息
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();//在所有模板执行完后,清理线程
    }
}
