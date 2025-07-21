package com.zhang.project.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

/**
 * 创建人: 张宇豪
 * 创建时间: 2025年7月21日15:41:03
 * 描述： JWT工具类
 */
public class JWTUtils {
    private static final String SECRET = "secret";


    /**
     * 生成token
     * @param map 用户信息
     * @return token
     */
    public static String generateToken(Map<String,String> map) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,7);
        // 创建jwt builder
        JWTCreator.Builder builder = JWT.create();
        map.forEach(builder::withClaim);
        return builder.sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 验证token
     * @param token token
     * @return DecodedJWT
     */
    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }
}
