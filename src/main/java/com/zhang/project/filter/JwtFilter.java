package com.zhang.project.filter;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.zhang.project.jwt.JWTUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Zhang
 * @date 2025年7月21日16:51:13
 */
@Component
@WebServlet(urlPatterns = "/*")
public class JwtFilter implements Filter {
    /**
     * 白名单
     */
    private final List<String> excludePath = Arrays.asList("/api/user/getToken", "/api/user/register");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getRequestURI();
        System.out.println("这里是拦截器拦截的请求路径" + path);
        // 1. 如果是排除路径，则直接放行
        if(excludePath.contains(path)){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String token = request.getHeader("Authorization");
        try {
            DecodedJWT verify = JWTUtils.verify(token);
            request.setAttribute("username", verify.getClaim("username").asString());
        } catch (Exception e) {
            // 3. 验证失败，返回 401
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
