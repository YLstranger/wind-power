package com.wp.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解记录系统操作日志
 */
//Target注解决定 MyLog 注解可以加在哪些成分上，如加在类身上，或者属性身上，或者方法身上等成分
@Target({ ElementType.PARAMETER, ElementType.METHOD })
//Retention注解括号中的"RetentionPolicy.RUNTIME"意思是让 MyLog 这个注解的生命周期在程序运行时一直都存在
@Retention(RetentionPolicy.RUNTIME)
public @interface MyLog
{
    /**
     * 模块标题
     */
    String title() default "";
    /**
     * 日志内容
     */
    String content() default "";
}

