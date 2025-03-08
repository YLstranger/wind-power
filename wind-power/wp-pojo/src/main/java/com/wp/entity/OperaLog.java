package com.wp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperaLog {

    //模块标题
    private String title;

    //日志内容
    private String content;

    //请求方法
    private String method;

    //请求方式
    private String requestMethod;

    //请求参数
    private String requestParam;

    //返回结果
    private String responseResult;

    //操作用户id
    private Long operaUserId;

    //操作用户ip
    private String ip;

    //操作用户IP归属地
    private String ipLocation;

    //请求URL
    private String requestUrl;

    //操作时间
    private LocalDateTime operaTime;

    //操作状态
    private Integer status;

    //操作错误信息
    private String errorMsg;

    //记录方法执行耗时时间（单位：毫秒）
    private Long takeTime;
}
