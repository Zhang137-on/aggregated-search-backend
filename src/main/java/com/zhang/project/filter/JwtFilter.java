package com.zhang.project.filter;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.zhang.project.common.PathUtils;
import com.zhang.project.jwt.JWTUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * @author Zhang
 * @date 2025年7月21日16:51:13
 */
@Component
public class JwtFilter implements Filter {
    /**
     * 白名单
     */
    private final List<String> excludePath = Arrays.asList("/api/user/getToken", "/api/user/register");

    /**
     * 拦截路径
     */
    private static final String[] PROTECTED_PATHS = {
            "/api/user/*",
            "/api/post/**"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("这个类被初始化了");
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getRequestURI();
        System.out.println("这里是拦截器拦截的请求路径" + path);
        // 检查路径是否需要拦截
        if (!JwtFilter.shouldIntercept(request)) {
            // 不需要拦截，直接放行
            System.out.println("没有拦截" + path);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 1. 如果是排除路径，则直接放行
        if(excludePath.contains(path)){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String token = request.getHeader("Authorization");
        try {
            DecodedJWT verify = JWTUtils.verify(token);
            request.setAttribute("username", verify.getClaim("username").asString());
            filterChain.doFilter(request, servletResponse);
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

    /**
     * 检查请求路径是否需要拦截
     */
    public static boolean shouldIntercept(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        for (String pattern : PROTECTED_PATHS) {
            if (PathUtils.matchPath(pattern, requestPath)) {
                return true;
            }
        }
        return false;
    }


}
