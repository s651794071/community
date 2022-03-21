package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串（激活码，用户上传的图片的名字）
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
        // 生成的随机字符串中会有"-"，我们把它替换成空
    }

    // MD5加密 password: hello --> abc23e 加密成随机字符串（但是每次都一样）
    // hello + salt （user表里的那个salt，随机字符串）提高安全性
    public static String md5(String key) {
        if(StringUtils.isBlank(key)) { // "", " ", Null 都会被判定为空
            return null; // 不去处理
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJsonString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if(map != null) {
            for (String key : map.keySet()) {
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJsonString(int code, String msg) {
        return getJsonString(code,msg,null);
    }

    public static String getJsonString(int code) {
        return getJsonString(code, null,null);
    }
}
