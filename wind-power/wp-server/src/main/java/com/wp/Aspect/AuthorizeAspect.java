package com.wp.Aspect;

import com.wp.annotation.PreAuthorize;
import com.wp.constant.JwtClaimsConstant;
import com.wp.mapper.SysUserMapper;
import com.wp.properties.JwtProperties;
import com.wp.result.Result;
import com.wp.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;


@Aspect
@Component
@Slf4j
public class AuthorizeAspect {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private SysUserMapper sysUserMapper;

    //配置切入点，@annotation
    @Pointcut("@annotation(com.wp.annotation.PreAuthorize)")
    public void authorizePointCut() {
    }

    @Around("authorizePointCut()")
    public Object handle(ProceedingJoinPoint pjp) throws Throwable {
        //查询用户的所有接口资源权限列表
        //解析请求头用户类型
        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), request.getHeader("Authorization"));
        String userType = claims.get(JwtClaimsConstant.USER_TYPE).toString();
        log.info(userType);
        //基于rbac查询用户的所有资源权限
        List<String> resources = sysUserMapper.selectUserPermsByRoleId(Integer.parseInt(userType));

        //获取当前接口资源的访问权限标志符
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
        String methodPermission = annotation.value();

        //比较
        boolean result = resources.contains(methodPermission);

        //如果当前用户角色没有权限访问该字资源接口：对不起，您没有该权限
        if (!result) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(403);//没有访问权限
            return Result.error("对不起，您没有该权限！");
        }
        return pjp.proceed();
    }
}
