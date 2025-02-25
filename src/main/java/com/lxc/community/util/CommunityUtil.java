package com.lxc.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.*;

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


    /**
     * 发布帖子JSON数据
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJSONString(int code,String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if (map!=null){
            for (String key: map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }
    //JSON方法重载
    public static String getJSONString(int code,String msg){
        return getJSONString(code, msg,null);
    }
    public static String getJSONString(int code){
        return getJSONString(code, null,null);
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>() ;
        map.put("name","zhangsan");
        map.put("age",25);
        System.out.println(getJSONString(0,"ok",map));


        }
    }


