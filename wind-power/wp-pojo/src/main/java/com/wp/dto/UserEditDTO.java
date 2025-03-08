package com.wp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEditDTO {

    //用户id
    private int id;

    //用户名
    private String username;

    //邮箱
    private String email;

    //用户类型 管理员|普通用户
    private String userType;

    //用户头像
    private String avatar;
}
