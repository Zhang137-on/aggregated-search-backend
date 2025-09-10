package com.zhang.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class BasicSecurityConfig {

    /**
     *     配置密码加密器，这是存储密码的必备项
      */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 配置用户信息，存储在内存中,JVM内存中
     * @param encoder 密码加密器
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("123456"))
                .roles("ADMIN")
                .build();
        UserDetails user = User.withUsername("user")
                .password(encoder.encode("123"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    /**
     * 配置URL访问权限
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http// 禁用CSRF保护
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // 先允许所有请求
                )
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers(new AntPathRequestMatcher("/api/user/**")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/api/login")).permitAll()
//                        .anyRequest().authenticated()
//                )
                // 添加异常处理来查看详细错误
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            System.out.println("拒绝访问路径: " + request.getRequestURI());
                            response.sendError(403, "Access Denied: " + request.getRequestURI());
                        })
                )
//                .formLogin(form -> form
//                        .loginPage("/login") // 自定义登录页地址，如果不配置则使用默认页
//                        .permitAll() // 允许所有人访问登录页
//                )
                .logout(logout -> logout
                        .permitAll() // 允许所有人注销
                );
        return http.build();
    }
}