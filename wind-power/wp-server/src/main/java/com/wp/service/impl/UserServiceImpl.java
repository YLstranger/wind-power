package com.wp.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wp.constant.MessageConstant;
import com.wp.dto.PageDTO;
import com.wp.dto.UserEditDTO;
import com.wp.dto.UserLoginDTO;
import com.wp.dto.UserRegisterDTO;
import com.wp.entity.User;
import com.wp.entity.UserRole;
import com.wp.exception.AccountAlreadyExist;
import com.wp.exception.AccountNotFoundException;
import com.wp.exception.PasswordErrorException;
import com.wp.mapper.UserMapper;
import com.wp.mapper.UserRoleMapper;
import com.wp.service.UserService;
import com.wp.vo.UserDataVO;
import com.wp.vo.PageResult;
import com.wp.vo.UserEditVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 用户登录
     *
     * @param userLoginDTO
     * @return
     */
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        User user = userMapper.getByUsername(username);

        //进行各种异常判断（账号是否存在，密码是否正确）
        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码对比
        //对密码进行加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //返回user对象
        return user;
    }

    /**
     * 获取用户列表
     *
     * @return
     */
    public PageResult pageQuery(PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNumber(), pageDTO.getPageSize());

        Page<User> page = userMapper.pageQuery(pageDTO);
        long total = page.getTotal();
        List<User> userList = page.getResult();
        List<UserDataVO> userDataList = new ArrayList<>();

        for (User user : userList) {
            UserDataVO userDataVO = new UserDataVO();
            BeanUtils.copyProperties(user, userDataVO);
            if (user.getUserType() == 0) {
                userDataVO.setUserType("管理员");
            } else if (user.getUserType() == 1) {
                userDataVO.setUserType("普通用户");
            }
            userDataList.add(userDataVO);
        }
        return new PageResult(userDataList, total);
    }

    /**
     * 更新用户信息
     *
     * @param userEditDTO
     */
    public void update(UserEditDTO userEditDTO) {
        User user = new User();
        BeanUtils.copyProperties(userEditDTO, user);
        if (userEditDTO.getUserType().equals("管理员")) {
            user.setUserType(0);
        } else if (userEditDTO.getUserType().equals("普通用户")) {
            user.setUserType(1);
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    /**
     * 删除用户信息
     *
     * @param id
     */
    public void delete(Integer id) {
        //根据用户id删除user_role表中数据
        userRoleMapper.deleteByUserId(id);
        //根据用户id删除user表中数据
        userMapper.delete(id);
    }

    /**
     * 获取验证码
     *
     * @return
     */
    public String getCode() {
        Random random = new Random();

        // 创建一个列表存储验证码的字符
        List<Character> captchaList = new ArrayList<>();

        // 生成两个数字
        for (int i = 0; i < 2; i++) {
            captchaList.add((char) ('0' + random.nextInt(10))); // '0' + 随机数字
        }

        // 生成两个字母
        for (int i = 0; i < 2; i++) {
            captchaList.add((char) ('A' + random.nextInt(26))); // 'A' + 随机字母
        }

        // 打乱列表中的字符顺序
        Collections.shuffle(captchaList, random);

        // 将字符列表转换为字符串
        StringBuilder captcha = new StringBuilder();
        for (char c : captchaList) {
            captcha.append(c);
        }

        return captcha.toString();
    }

    /**
     * 用户注册
     *
     * @param userRegisterDTO
     */
    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) {
        String username=userRegisterDTO.getUsername();
        String password = userRegisterDTO.getPassword();

        //判断用户名是否已存在，用户名存在则抛出异常，该用户已经存在
        User user = userMapper.getByUsername(username);
        if (user != null) {
            throw new AccountAlreadyExist(MessageConstant.ACCOUNT_ALREADY_EXIST);
        }
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //向用户表插入数据，并返回用户ID
        user = User.builder()
                .username(userRegisterDTO.getUsername())
                .password(password)
                .email(userRegisterDTO.getEmail())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .userType(1)
                .build();
        userMapper.insert(user);

        //向用户-角色表插入数据
        UserRole userRole = UserRole.builder()
                .userId(user.getId())
                .roleId(user.getUserType())
                .build();
        userRoleMapper.insert(userRole);
    }

    /**
     * 根据用户id获取用户信息
     * @param id
     * @return
     */
    public UserEditVO getById(Integer id) {
        UserEditVO userEditVO=userMapper.getById(id);
        if (Integer.parseInt(userEditVO.getUserType())==0){
            userEditVO.setUserType("管理员");
        }else if (Integer.parseInt(userEditVO.getUserType())==1){
            userEditVO.setUserType("普通用户");
        }
        return userEditVO;
    }
}
