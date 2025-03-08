package com.wp.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解，后端接口资源的鉴权
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreAuthorize {

    /**
     * 权限标志符
     */
    String value() default "";
}
