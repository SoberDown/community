package com.lxc.community.controller.interceptor;
//判断用户是否登录
import com.lxc.community.annotation.LoginRequired;
import com.lxc.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){//判断拦截到的是否是个方法
            /**
             * HandlerMethod左边是对象,右边是类;当对象是右边类或子类所创建对象时,返回true;否则返回false
             * 注意!!!如果handler拦截到的是方法,那么此时handler就是HandlerMethod类型的,所以接下来进行转型
             */
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();//获取拦截到的方法对象
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);//看拦截到的对象中,有没有LoginRequired这个注解
            if (loginRequired!=null && hostHolder.getUser()==null){//代表有这个注解.需要登录才能访问;但是user为空,又没有登录;所以是错误情况
                //失败则重定向返回登录界面,因为是接口声明不能随便return
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
