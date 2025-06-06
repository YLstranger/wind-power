package com.wp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// Component注解和ConfigurationProperties注解缺一不可
@Component
@ConfigurationProperties(prefix = "wp.jwt")
@Data
public class JwtProperties {

    /**
     * 用户登录生成jwt令牌相关配置
     */
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;
}
