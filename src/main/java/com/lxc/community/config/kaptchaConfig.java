package com.lxc.community.config;
/**
 * 验证码配置类
 */

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class kaptchaConfig {

    @Bean
    public Producer kaptchaProducer(){
        //配置信息
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.font.size","32");//字号大小
        properties.setProperty("kaptcha.textproducer.font.color","black");//字体颜色
        properties.setProperty("kaptcha.textproducer.char.string","0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");//用那些来拼接验证码
        properties.setProperty("kaptcha.textproducer.char.length","4");//验证码字数
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");//使用那种干扰类

        //创建验证码对象
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        //把配置信息存进config对象里
        Config config = new Config(properties);
        //把config对象里的配置信息 赋值给验证码对象的配置类
        kaptcha.setConfig(config);
        return kaptcha;
    }

}
