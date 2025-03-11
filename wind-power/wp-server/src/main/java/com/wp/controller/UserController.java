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
import com.wp.exception.CaptchaTimeoutException;
import com.wp.exception.RequestLimitException;
import com.wp.properties.JwtProperties;
import com.wp.result.Result;
import com.wp.service.UserService;
import com.wp.utils.JwtUtil;
import com.wp.vo.PageResult;
import com.wp.vo.UserEditVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; //lombok注解里面包含了SLF4J这个日志框架
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("api/user")
@Api(tags = "用户模块")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    private static final String CODE_KEY_PREFIX = "captcha:code:";  // 验证码存储键前缀
    private static final long CODE_EXPIRE_SECONDS = 60;            // 60秒有效期


    /**
     * 用户登录
     *
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    @MyLog(title = "用户模块", content = "用户登录操作")
    public Result<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("员工登录：{}", userLoginDTO);

        // 验证用户输入的验证码
        String key = CODE_KEY_PREFIX + userLoginDTO.getEmail();
        String storedCode = redisTemplate.opsForValue().get(key);
        if (storedCode ==null){
            throw new CaptchaTimeoutException(MessageConstant.Captcha_NOT_EXIST);
        }
        if (!userLoginDTO.getInputCode().equals(storedCode)) {
            throw new CaptchaTimeoutException(MessageConstant.Captcha_IS_ERROR);
        }


        User user = userService.login(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        // 生成用户凭证，包含用户ID和用户类型
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
     *
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("获取用户列表")
    @PreAuthorize("api:user:page")
    @MyLog(title = "用户模块", content = "分页查询获取用户列表操作")
    public Result<PageResult> pageQuery(PageDTO pageDTO) {
        log.info("分页查询用户列表：{}", pageDTO);
        PageResult pageResult = userService.pageQuery(pageDTO);
        return Result.success(pageResult);
    }


    /**
     * 编辑用户信息
     *
     * @param userEditDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("更新用户信息")
    @PreAuthorize("api:user:update")
    @MyLog(title = "用户模块", content = "编辑用户操作")
    public Result update(@RequestBody UserEditDTO userEditDTO) {
        userService.update(userEditDTO);
        return Result.success();
    }

    /**
     * 删除用户信息
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除用户信息")
    @PreAuthorize("api:user:delete")
    @MyLog(title = "用户模块", content = "删除用户操作")
    public Result delete(@PathVariable Integer id) {
        log.info("删除用户id为：{}", id);
        userService.delete(id);
        return Result.success();
    }

    /**
     * 生成并发送验证码（带60秒限制）
     */
    @GetMapping("/code")
    @ApiOperation("获取验证码")
    @MyLog(title = "用户模块", content = "获取验证码操作")
    public Result<String> sendCode(String email) {
        String key = CODE_KEY_PREFIX +email;
        // 检查是否在冷却期内
        if (redisTemplate.hasKey(key)) {
            long remainSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            throw new RequestLimitException(MessageConstant.REQUEST_LIMIT_EXCEEDED);
        }
        // 生成验证码
        String code = userService.getCode();
        // 存储至Redis，60秒过期
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        return Result.success(code);
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    @MyLog(title = "用户模块", content = "用户注册操作")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册信息为：{}", userRegisterDTO);
        userService.register(userRegisterDTO);
        return Result.success();
    }

    @GetMapping("/getById/{id}")
    @ApiOperation("根据id获取用户信息")
    @MyLog(title = "用户模块", content = "根据id获取用户信息操作")
    public Result<UserEditVO> getById(@PathVariable Integer id) {
        log.info("根据id获取用户信息：{}", id);
        UserEditVO userEditVO = userService.getById(id);
        return Result.success(userEditVO);
    }
}

