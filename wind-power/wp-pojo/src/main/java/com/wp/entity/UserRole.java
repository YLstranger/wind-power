package com.wp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    //用户ID
    private Integer userId;

    //角色ID
    private Integer roleId;
}
