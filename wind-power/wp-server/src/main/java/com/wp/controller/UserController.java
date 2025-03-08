package com.wp.controller;

import com.wp.annotation.MyLog;
import com.wp.annotation.PreAuthorize;
import com.wp.constant.JwtClaimsConstant;
import com.wp.constant.MessageConstant;
import com.wp.dto.PageDTO;
import com.wp.dto.UserEditDTO;
import com.wp.dto.UserLoginDTO;
import com.wp.dto.UserRegisterDTO;
import com.wp.entity.User;
import com.wp.properties.JwtProperties;
import com.wp.result.Result;
import com.wp.service.UserService;
import com.wp.utils.JwtUtil;
import com.wp.vo.PageResult;
import com.wp.vo.UserEditVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("api/user")
@Api(tags = "用户模块")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;


    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    @MyLog(title = "用户模块", content = "用户登录操作")
    public Result<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("员工登录：{}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        //对用户信息进行判断
        if (user==null){
            return Result.error(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        claims.put(JwtClaimsConstant.USER_TYPE, user.getUserType());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        //返回token
        return Result.success(token);
    }

    /**
     * 分页查询获取用户列表
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("获取用户列表")
    @PreAuthorize("api:user:page")
    @MyLog(title = "用户模块", content = "分页查询获取用户列表操作")
    public Result<PageResult> pageQuery(PageDTO pageDTO){
        log.info("分页查询用户列表：{}", pageDTO);
        PageResult pageResult = userService.pageQuery(pageDTO);
        return Result.success(pageResult);
    }


    /**
     * 编辑用户信息
     * @param userEditDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("更新用户信息")
    @PreAuthorize("api:user:update")
    @MyLog(title = "用户模块", content = "编辑用户操作")
    public Result update(@RequestBody UserEditDTO userEditDTO){
        userService.update(userEditDTO);
        return Result.success();
    }

    /**
     * 删除用户信息
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除用户信息")
    @PreAuthorize("api:user:delete")
    @MyLog(title = "用户模块", content = "删除用户操作")
    public Result delete(@PathVariable Integer id){
        log.info("删除用户id为：{}", id);
        userService.delete(id);
        return Result.success();
    }

    @GetMapping("/code")
    @ApiOperation("获取验证码")
    @MyLog(title = "用户模块", content = "获取验证码操作")
    public Result getCode(){
        String code=userService.getCode();
        return Result.success(code);
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    @MyLog(title = "用户模块", content = "用户注册操作")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO){
        log.info("用户注册信息为：{}", userRegisterDTO);
        userService.register(userRegisterDTO);
        return Result.success();
    }

    @GetMapping("/getById/{id}")
    @ApiOperation("根据id获取用户信息")
    @MyLog(title = "用户模块", content = "根据id获取用户信息操作")
    public Result<UserEditVO> getById(@PathVariable Integer id){
        log.info("根据id获取用户信息：{}", id);
        UserEditVO userEditVO = userService.getById(id);
        return Result.success(userEditVO);
    }
}

