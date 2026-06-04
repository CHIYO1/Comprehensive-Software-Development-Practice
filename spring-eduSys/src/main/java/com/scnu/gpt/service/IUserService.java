package com.scnu.gpt.service;

import com.scnu.gpt.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnu.gpt.pojo.user.UserRegisterRequest;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ldw
 * @since 2025-06-03
 */
public interface IUserService extends IService<User> {

    List<User> queryUser(User requestUser);

    /**
     * 用户注册
     * @param request 注册请求（account, password, username, role）
     * @return 注册成功的用户对象
     */
    User register(UserRegisterRequest request);

    /**
     * 获取用户基本信息
     * @param userId 用户ID
     * @return 包含 username 和 avatar 的 Map，用户不存在返回 null
     */
    Map<String, Object> getUserInfo(Integer userId);

    /**
     * 根据 account 查询用户
     * @param account 登录账号
     * @return 用户对象，不存在返回 null
     */
    User getByAccount(String account);
}
