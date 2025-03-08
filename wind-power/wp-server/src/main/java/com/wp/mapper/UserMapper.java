package com.wp.mapper;

import com.github.pagehelper.Page;
import com.wp.dto.PageDTO;
import com.wp.dto.UserEditDTO;
import com.wp.entity.User;
import com.wp.vo.UserEditVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户信息
     * @param username
     */
    @Select("select * from user where username = #{username}")
    User getByUsername(String username);


    /**
     * 获取用户列表
     * @return
     */
    @Select("select * from user where user_type!=0")
    Page<User> pageQuery(PageDTO pageDTO);

    /**
     * 更新用户信息
     * @param user
     */
    void update(User user);

    /**
     * 删除用户信息
     * @param id
     */
    @Delete("delete from user where id = #{id}")
    void delete(Integer id);

    /**
     * 用户注册
     * @param user
     */
    void insert(User user);

    /**
     * 根据id查询用户信息
     */
    @Select("select username,email,user_type,avatar from user where id=#{id}")
    UserEditVO getById(Integer id);
}
