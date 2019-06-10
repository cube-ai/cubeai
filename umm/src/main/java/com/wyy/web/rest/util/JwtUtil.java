package com.wyy.web.rest.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;


public final class JwtUtil {

    private JwtUtil() {
    }

    public static String getUserLogin(HttpServletRequest httpServletRequest) {
        String authorization = httpServletRequest.getHeader("authorization");
        if (null == authorization) {
            // 请求头中没有携带JWT，表示非登录用户
            return null;
        }

        String jwt = authorization.substring(7); // 去除前缀“bearer ”
        String payloadBase64 = jwt.substring(jwt.indexOf(".") + 1, jwt.lastIndexOf(".")); // 取出JWT中第二部分
        Base64.Decoder decoder = Base64.getDecoder();
        String payloadString;
        try {
            payloadString = new String(decoder.decode(payloadBase64), "UTF-8");
        } catch (Exception e) {
            return null;
        }
        JSONObject payloadJson = JSONObject.parseObject(payloadString);

        String userLogin = payloadJson.getString("user_name");
        if (null == userLogin) {
            return "system"; // 如果JWT中没有携带用户名，则应该是微服务间内部调用，此时将用户名强制设为system返回。
        } else {
            return userLogin;
        }
    }

    public static String getUserRoles(HttpServletRequest httpServletRequest) {
        String authorization = httpServletRequest.getHeader("authorization");
        if (null == authorization) {
            // 请求头中没有携带JWT，表示非登录用户
            return null;
        }

        String jwt = authorization.substring(7); // 去除前缀“bearer ”
        String payloadBase64 = jwt.substring(jwt.indexOf(".") + 1, jwt.lastIndexOf(".")); // 取出JWT中第二部分
        Base64.Decoder decoder = Base64.getDecoder();
        String payloadString;
        try {
            payloadString = new String(decoder.decode(payloadBase64), "UTF-8");
        } catch (Exception e) {
            return null;
        }
        JSONObject payloadJson = JSONObject.parseObject(payloadString);

        return payloadJson.getString("authorities");
    }

}
