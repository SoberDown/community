package com.lxc.community.aspect;
//AOP示例
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AplhaAspect {

    @Pointcut("execution(* com.lxc.community.service.*.*(..))")//第一个*表示所有返回值都行,第二个*表示所有的类,第三个*表示所有的方法,(..)表示所有的参数
    public void pointcut(){

    }

    //在连接点之前进行织入
    @Before("pointcut()")//括号里写切入点是什么
    public void before(){
        System.out.println("before");
    }

    //在连接点之后进行织入
    @After("pointcut()")//括号里写切入点是什么
    public void after(){
        System.out.println("after");
    }

    //在返回值之后进行织入
    @AfterReturning("pointcut()")//括号里写切入点是什么
    public void afterreturning(){
        System.out.println("afterreturning");
    }

    //在抛异常之后进行织入
    @AfterThrowing("pointcut()")//括号里写切入点是什么
    public void afterthrowing(){
        System.out.println("afterthrowing");
    }

    //在连接点前后都进行织入
    @Around("pointcut()")//括号里写切入点是什么
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("around before");
        Object obj = joinPoint.proceed();//调用目标对象,被处理的那个组件的方法
        System.out.println("around after");
        return obj;
    }

}
