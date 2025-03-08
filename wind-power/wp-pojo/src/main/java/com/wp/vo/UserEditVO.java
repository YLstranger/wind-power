package com.wp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEditVO {

    //用户名
    private String username;

    //邮箱
    private String email;

    //用户类型 0表示管理员 1表示普通用户
    private String userType;

    //用户头像
    private String userAvatar;

}
