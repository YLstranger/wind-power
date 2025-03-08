package com.wp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper {

    /**
     * 通过角色id来查询权限列表
     * @param roleId
     * @return
     */
    @Select("select p.resource from role r,role_permission rp,permission p where r.id=rp.role_id and rp.permission_id=p.id and r.id=#{roleId}")
    List<String> selectUserPermsByRoleId(int roleId);
}
