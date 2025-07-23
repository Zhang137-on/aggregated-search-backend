//package com.zhang.project.config;
//
//import com.zhang.project.filter.JwtFilter;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//@Configuration
//public class FilterConfig {
//    @Bean
//    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter jwtFilter) {
//        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(jwtFilter);
//        registration.addUrlPatterns("/api/*");
//        registration.setName("JwtFilter");
//        registration.setOrder(1);
//        return registration;
//    }
//}
