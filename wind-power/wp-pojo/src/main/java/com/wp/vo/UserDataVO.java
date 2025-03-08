package com.wp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDataVO implements Serializable {

    //主键值
    private int id;

    //用户名
    private String username;

    //邮箱
    private String email;

    //用户类型 0表示管理员 1表示普通用户
    private String userType;

    //头像
    private String avatar;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
