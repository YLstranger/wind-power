package com.wp.mapper;

import com.wp.entity.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper {

    /**
     * 添加用户角色
     * @param userRole
     */
    @Insert("insert into user_role(user_id, role_id) values(#{userId}, #{roleId})")
    void insert(UserRole userRole);

    /**
     * 删除用户角色
     * @param userId
     */
    @Delete("delete from user_role where user_id = #{userId}")
    void deleteByUserId(Integer userId);
}
