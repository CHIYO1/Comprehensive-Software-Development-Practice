package com.scnu.gpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnu.gpt.entity.User;
import com.scnu.gpt.mapper.UserMapper;
import com.scnu.gpt.pojo.user.UserLoginRequest;
import com.scnu.gpt.pojo.user.UserLoginResponse;
import com.scnu.gpt.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnu.gpt.util.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ldw
 * @since 2025-06-03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService, UserDetailsService {
    private final UserMapper userMapper;
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userMapper.selectById(Integer.parseInt(userId));
        return new org.springframework.security.core.userdetails.User(
                userId,
                user.getPassword(), // BCrypt加密后的密码
                Collections.singleton(new SimpleGrantedAuthority(user.getUserType()))
        );
    }
    //其它业务逻辑
    @Override
    public List<User> queryUser(User requestUser) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        if (requestUser.getUserId() != null) {
            queryWrapper.eq(User::getUserId, requestUser.getUserId());
        }
        if (requestUser.getUserName() != null && !requestUser.getUserName().isEmpty()) {
            queryWrapper.like(User::getUserName, requestUser.getUserName());
        }
        if (requestUser.getUserType() != null && !requestUser.getUserType().isEmpty()) {
            queryWrapper.eq(User::getUserType, requestUser.getUserType());
        }

        return this.list(queryWrapper);
    }


}
