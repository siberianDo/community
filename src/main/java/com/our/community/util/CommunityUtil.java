package com.our.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * 密码加密
 */
public class CommunityUtil {

    //    生成激活码（随机字符串）
    public static String generateUUID() {
//        将随机字符串的”-“改为”“
        return UUID.randomUUID().toString().replace("-", "");
    }

    //    密码加密   MD5
//    普通加密，生成加密密码固定，容易破解
//    可以在密码中加入随机字符串再进行加密，提高安全性
    public static String md5(String key) {
//        首先判断是否为空，选择是否加密
        if (StringUtils.isBlank(key)) {
            return null;
        } else {
            return DigestUtils.md5DigestAsHex(key.getBytes());
        }
    }

    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

}
