package com.scnu.gpt.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnu.gpt.entity.User;
import com.scnu.gpt.pojo.ApiResponse;
import com.scnu.gpt.pojo.user.UserLoginRequest;
import com.scnu.gpt.pojo.user.UserLoginResponse;
import com.scnu.gpt.service.IUserService;
import com.scnu.gpt.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ldw
 * @since 2025-06-03
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
@Tag(name = "用户管理", description = "包括用户登录...") // API分组
public class UserController {
    private final AuthenticationManager authenticationManager; // 认证管理器
    private final JwtUtils jwtUtils; // JWT工具
    private final IUserService userService;

    // 构造函数依赖注入
    public UserController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, IUserService IUserService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = IUserService;
    }

    // 登录接口
    @Operation(
            summary = "用户登录。返回用户信息及安全认证token",
            description = "如题")
    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        try{
            // 自动验证登录信息并生成token认证信息
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.userId(), // 用户名
                            request.password()  // 密码
                    )
            );
            //用户信息查询
            User user = userService.getById(Integer.parseInt(request.userId()));
            UserLoginResponse response = new UserLoginResponse(user.getUserId(),user.getUserName(),user.getUserType(),jwtUtils.generateToken(
                    (UserDetails) authentication.getPrincipal()));
            return new ApiResponse<>("200","登录成功", response); // 生成令牌并返回
        }catch (AuthenticationException e) {
            System.out.println("登录验证失败"+e.getMessage());
            return new ApiResponse<>("500","登录验证失败",null);
        }
    }
//    @Operation(
//            summary = "用户登录",
//            description = "如题") // 接口注解
//    @PostMapping("/login")
//    public UserLoginResponse userLogin(@RequestBody UserLoginRequest request) {
//
//        return IUserService.userLogin(request);
//    }
//
//    @Operation(
//            summary = "用户查询",
//            description = "根据传入的User条件，动态查询User列表")
//    @PostMapping("/queryUsers")
//    public List<User> queryUsers(@RequestBody User requestUser ) {
//
//        return IUserService.queryUser(requestUser);
//    }
//
//    @Operation(
//            summary = "添加用户",
//            description = "用于注册/添加新用户（学生/教师）")
//    @PostMapping("/add")
//    public String addUser(@RequestBody User user) {
//        boolean saved = IUserService.save(user);
//        return saved ? "用户添加成功" : "用户添加失败";
//    }
//
//    @Operation(
//            summary = "修改用户信息",
//            description = "支持修改用户昵称、密码、类型等信息")
//    @PutMapping("/update")
//    public String updateUser(@RequestBody User user) {
//        boolean updated = IUserService.updateById(user);
//        return updated ? "用户信息修改成功" : "用户信息修改失败";
//    }
//
//    @Operation(
//            summary = "删除用户",
//            description = "根据用户ID删除对应用户")
//    @DeleteMapping("/delete")
//    public String deleteUser(@RequestParam Integer userId) {
//        boolean removed = IUserService.removeById(userId);
//        return removed ? "用户删除成功" : "用户删除失败";
//    }
}
