package com.wp.service;

import com.wp.dto.PageDTO;
import com.wp.dto.UserEditDTO;
import com.wp.dto.UserLoginDTO;
import com.wp.dto.UserRegisterDTO;
import com.wp.entity.User;
import com.wp.vo.PageResult;
import com.wp.vo.UserEditVO;

public interface UserService {

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);

    /**
     * 获取用户列表
     * @return
     */
    PageResult pageQuery(PageDTO pageDTO);

    /**
     * 更新用户信息
     * @param userEditDTO
     */
    void update(UserEditDTO userEditDTO);

    /**
     * 删除用户信息
     * @param id
     */
    void delete(Integer id);

    /**
     * 获取验证码
     * @return
     */
    String getCode();

    /**
     * 用户注册
     * @param userRegisterDTO
     */
    void register(UserRegisterDTO userRegisterDTO);

    /**
     * 根据用户id获取用户信息
     * @param id
     * @return
     */
    UserEditVO getById(Integer id);
}
