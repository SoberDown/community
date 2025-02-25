package com.lxc.community.config;
/**
 * 拦截器配置类
 */

import com.lxc.community.controller.interceptor.AlphaInterceptor;
import com.lxc.community.controller.interceptor.LoginRequiredInterceptor;
import com.lxc.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    //注入拦截器
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)//添加所选的拦截器
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")//拦截器不生效的的路径
                .addPathPatterns("/register","/login");//拦截器生效路径

        registry.addInterceptor(loginTicketInterceptor)//添加所选的拦截器
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");//拦截器不生效的的路径

        registry.addInterceptor(loginRequiredInterceptor)//添加所选的拦截器
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");//拦截器不生效的的路径
    }
}
