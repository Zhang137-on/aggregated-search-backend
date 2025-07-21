//package com.zhang.project.config;
//
//import com.zhang.project.filter.JwtFilter;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FilterConfig {
//    @Bean
//    public FilterRegistrationBean<JwtFilter> loginFilter() {
//        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
//        // 设置自定义的 JWT 过滤器
//        registrationBean.setFilter(new JwtFilter());
//        // 拦截所有 /api/** 路径，但排除 /api/user/getToken
//        // 拦截一级路径（如 /api/user）
//        registrationBean.addUrlPatterns("/api/*");
//        registrationBean.addUrlPatterns("/api/*/*");
//        registrationBean.addUrlPatterns("/api/*/*/*");
//        // 显式排除不需要拦截的路径
//        registrationBean.addInitParameter("exclusions", "/api/user/getToken,/api/user/register");
//
//        // 设置过滤器名称和顺序（可选）
//        registrationBean.setName("jwtFilter");
//        // 执行顺序
//        registrationBean.setOrder(1);
//        return registrationBean;
//    }
//}
