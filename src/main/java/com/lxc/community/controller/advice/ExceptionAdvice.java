package com.lxc.community.controller.advice;
//统一异常处理
import com.lxc.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//@ControllerAdvice用于修饰类,表示该类时Controller的全局配置类
@ControllerAdvice(annotations = Controller.class)//扫描所有的Controller组件
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    //@ExceptionHandler在Controller出现异常后被调用,处理捕获到的异常
    @ExceptionHandler({Exception.class})//所有方法的父类,表示所有方法都用这个处理
    public void handlerException(Exception e, HttpServletResponse response, HttpServletRequest request) throws IOException {
        logger.error("服务器发送异常:" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()){//e.getStackTrace()是个数组
            logger.error(element.toString());
        }

        //判断是普通请求还是异步请求
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)){//要的是xml,说明是个异步请求
//            response.setContentType("application/json;charset=utf-8");直接设置成json格式
            //设置为普通请求,手动转换为json格式数据
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常!"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
