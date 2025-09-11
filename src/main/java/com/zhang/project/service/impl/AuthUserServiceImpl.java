package com.zhang.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhang.project.mapper.UserMapper;
import com.zhang.project.model.entity.User;
import com.zhang.project.service.AuthUserService;
import com.zhang.project.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhangyuhao
 */
@Service
public class AuthUserServiceImpl implements UserDetailsService, AuthUserService {
    @Resource
    public UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库查询用户
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserName, username);
        User user = userMapper.selectOne(userLambdaQueryWrapper);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }
        // 2. 将数据库中的用户转换为Spring Security需要的UserDetails对象
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getUserPassword())
                .roles(user.getUserRole())
                .build();
    }
}
