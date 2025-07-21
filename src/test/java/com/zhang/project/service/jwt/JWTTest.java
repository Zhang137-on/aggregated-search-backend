package com.zhang.project.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.HashMap;

@SpringBootTest
public class JWTTest {
    static String token;

    @Test
    public static void JWTTest() {
        HashMap<String, Object> map = new HashMap<>();
        // 获取 日历对象
        Calendar calendar = Calendar.getInstance();
        // 20 秒失效的token
        calendar.add(Calendar.SECOND, 2);

        token = JWT.create()
                .withHeader(map) // header可以不写，默认值就是它
                .withClaim("userId", 21) // payload
                .withClaim("username", "zhangsan")
                .withExpiresAt(calendar.getTime()) // 过期时间
                .sign(Algorithm.HMAC256("secret")); // 签名
        System.out.println(token);
    }

    @Test
    public static void parseJWT() {
        JWTVerifier secret = JWT.require(Algorithm.HMAC256("secret")).build();
        DecodedJWT verify = secret.verify(token);
        System.out.println(verify.getClaim("userId"));
        System.out.println(verify.getClaim("username"));
        System.out.println("令牌过期时间：" + verify.getExpiresAt());
    }

    public static void main(String[] args) {
        JWTTest();
        parseJWT();
    }
}
