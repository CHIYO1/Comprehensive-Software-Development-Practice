package com.scnu.gpt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnu.gpt.entity.User;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.user.UserLoginRequest;
import com.scnu.gpt.pojo.user.UserLoginResponse;
import com.scnu.gpt.pojo.user.UserRegisterRequest;
import com.scnu.gpt.service.IUserService;
import com.scnu.gpt.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 *  用户控制器 — 登录、注册、用户信息
 * </p>
 *
 * @author ldw
 * @since 2025-06-03
 */
@RestController
@CrossOrigin
@RequestMapping("/users")
@Tag(name = "用户管理", description = "包括用户登录、注册、信息查询...")
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IUserService userService;

    public UserController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, IUserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    /**
     * 用户登录 — 使用 account + password
     */
    @Operation(summary = "用户登录", description = "使用账号(account)和密码登录，返回用户信息及JWT令牌")
    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        try {
            // Spring Security 认证（loadUserByUsername 按 account 查询）
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.account(),
                            request.password()
                    )
            );
            // 查询用户完整信息
            User user = userService.getByAccount(request.account());
            UserLoginResponse response = new UserLoginResponse(
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole(),
                    jwtUtils.generateToken((UserDetails) authentication.getPrincipal())
            );
            return new ApiResponse<>("200", "登录成功", response);
        } catch (DisabledException e) {
            System.out.println("用户已被禁用: " + e.getMessage());
            return new ApiResponse<>("500", "用户已被禁用，请联系管理员", null);
        } catch (AuthenticationException e) {
            System.out.println("登录验证失败: " + e.getMessage());
            return new ApiResponse<>("500", "账号或密码错误", null);
        }
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "注册新用户，仅允许Student和Teacher角色")
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody UserRegisterRequest request) {
        try {
            userService.register(request);
            return new ApiResponse<>("200", "注册成功", null);
        } catch (IllegalArgumentException e) {
            System.out.println("注册失败: " + e.getMessage());
            return new ApiResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            System.out.println("注册异常: " + e.getMessage());
            return new ApiResponse<>("500", "注册失败，请稍后重试", null);
        }
    }

    /**
     * 获取用户信息
     */
    @Operation(summary = "获取用户信息", description = "根据user_id查询用户的username和avatar")
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getUserInfo(@RequestParam("user_id") Integer userId) {
        Map<String, Object> info = userService.getUserInfo(userId);
        if (info == null) {
            return new ApiResponse<>("500", "用户不存在", null);
        }
        return new ApiResponse<>("200", "获取成功", info);
    }
}
