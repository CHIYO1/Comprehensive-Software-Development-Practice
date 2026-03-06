package com.scnu.gpt.service;

import com.scnu.gpt.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnu.gpt.pojo.user.UserLoginRequest;
import com.scnu.gpt.pojo.user.UserLoginResponse;

import java.util.List;

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
}
