package com.lxc.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

    //随机字符串
    public static String setUUID(){//UUID里有-,我们一般不要这个,所以使用replaceAll替换
        return UUID.randomUUID().toString().replaceAll("-","");//replaceAll把所有的"-"替换成""
    }

    //MD5加密算法--注意:只能加密不能解密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){//判断传入的值,是否为空
            return null;
        }
        // 把传入的值进行加密成随机的16位数字符串
        return DigestUtils.md5DigestAsHex(key.getBytes());//类型不同,使用getBytes进行转换
    }


}
