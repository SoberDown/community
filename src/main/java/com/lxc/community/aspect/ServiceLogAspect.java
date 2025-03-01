package com.lxc.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.lxc.community.service.*.*(..))")//第一个*表示所有返回值都行,第二个*表示所有的类,第三个*表示所有的方法,(..)表示所有的参数
    public void pointcut(){

    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint){//joinPoint指代要调用的方法
        //用户[xxx],在[xxx],访问了[com.lxc.community.service.xxx()]
        //获取request对象,使用RequestContextHolder转型成它的子类ServletRequestAttributes,功能更多些
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //通过attributes获取request对象
        HttpServletRequest request = attributes.getRequest();
        //通过request获取ip地址
        String ip = request.getRemoteHost();
        //获取时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        //joinPoint.getSignature().getDeclaringType()得到目标的类名
        //joinPoint.getSignature().getName()得到目标的方法名
        String target = joinPoint.getSignature().getDeclaringType() + "." + joinPoint.getSignature().getName();

        //添加日志
        logger.info(String.format("用户[%s],在[%s],访问了[%s].",ip,now,target));
    }

}
