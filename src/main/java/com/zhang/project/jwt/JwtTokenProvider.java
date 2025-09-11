package com.zhang.project.jwt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


/**
 * @author zhangyuhao
 * jwt 工具类
 */
@Component
public class JwtTokenProvider {

    /**
     * 建议使用足够长度的密钥（HS256至少需要256位即32字节）
     */
    private final String JWT_SECRET = "your-256-bit-secret-key-that-is-at-least-32-bytes-long";

    /**
     * 令牌过期时间（例如：24小时）
     */
    private final long JWT_EXPIRATION = 86400000;

    /**
     * 生成令牌
     * @param username
     * @return
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        // 使用新的构建器模式生成JWT
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                // 使用Keys.hmacShaKeyFor()生成密钥，替代直接使用字符串
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从令牌中获取用户名
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }


    /**
     * 验证令牌
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取签名密钥
     * @return
     */
    private Key getSigningKey() {
        // 将字符串密钥转换为符合算法要求的Key对象
        byte[] keyBytes = JWT_SECRET.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}