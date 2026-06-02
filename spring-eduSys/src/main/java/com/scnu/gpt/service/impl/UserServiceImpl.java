package com.scnu.gpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnu.gpt.entity.User;
import com.scnu.gpt.mapper.UserMapper;
import com.scnu.gpt.pojo.user.UserRegisterRequest;
import com.scnu.gpt.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Spring Security 认证入口 — 按 account 加载用户
     */
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getAccount, account)
        );
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + account);
        }
        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new DisabledException("用户已被禁用: " + account);
        }
        return new org.springframework.security.core.userdetails.User(
                account,
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    /**
     * 用户注册
     */
    @Override
    public User register(UserRegisterRequest request) {
        // 1. 禁止注册管理员
        if ("Admin".equals(request.role())) {
            throw new IllegalArgumentException("管理员账户只能后台创建");
        }
        // 2. 校验账号唯一性
        User existing = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getAccount, request.account())
        );
        if (existing != null) {
            throw new IllegalArgumentException("账号已存在: " + request.account());
        }
        // 3. 构建用户对象
        User user = new User();
        user.setAccount(request.account());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setUsername(request.username());
        user.setRole(request.role());
        user.setStatus(1);
        // 4. 保存
        userMapper.insert(user);
        return user;
    }

    /**
     * 获取用户信息（username + avatar）
     */
    @Override
    public Map<String, Object> getUserInfo(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        Map<String, Object> info = new HashMap<>();
        info.put("username", user.getUsername());
        info.put("avatar", user.getAvatar());
        return info;
    }

    /**
     * 根据 account 查询用户
     */
    @Override
    public User getByAccount(String account) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getAccount, account)
        );
    }

    // 其它业务逻辑
    @Override
    public List<User> queryUser(User requestUser) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        if (requestUser.getUserId() != null) {
            queryWrapper.eq(User::getUserId, requestUser.getUserId());
        }
        if (requestUser.getUsername() != null && !requestUser.getUsername().isEmpty()) {
            queryWrapper.like(User::getUsername, requestUser.getUsername());
        }
        if (requestUser.getRole() != null && !requestUser.getRole().isEmpty()) {
            queryWrapper.eq(User::getRole, requestUser.getRole());
        }

        return this.list(queryWrapper);
    }
}
