package com.lxc.community.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//写到方法上,描述方法
@Retention(RetentionPolicy.RUNTIME)//方法有效时长,在运行时有效
public @interface LoginRequired {


}
