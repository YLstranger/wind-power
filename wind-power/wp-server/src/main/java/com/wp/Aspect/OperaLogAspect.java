package com.wp.Aspect;

import com.alibaba.fastjson.JSON;
import com.wp.annotation.MyLog;
import com.wp.constant.JwtClaimsConstant;
import com.wp.context.BaseContext;
import com.wp.entity.OperaLog;
import com.wp.properties.JwtProperties;
import com.wp.service.OperaLogService;
import com.wp.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 切面处理类，记录操作日志到数据库
 */
@Aspect
@Component
public class OperaLogAspect {

    @Autowired
    private OperaLogService operaLogService;


    //为了记录方法的执行时间
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 设置操作日志切入点，这里介绍两种方式：
     * 1、基于注解切入（也就是打了自定义注解的方法才会切入）
     *
     * @Pointcut("@annotation(org.wujiangbo.annotation.MyLog)")
     *
     * 2、基于包扫描切入
     * @Pointcut("execution(public * com.scour.controller..*.*(..))")
     */
    @Pointcut("@annotation(com.wp.annotation.MyLog)")//在注解的位置切入代码
    //@Pointcut("execution(public * org.wujiangbo.controller..*.*(..))")//从controller切入
    public void operaLogPointCut() {
    }

    @Before("operaLogPointCut()")
    public void beforeMethod(JoinPoint point) {
        startTime.set(System.currentTimeMillis());
    }

    /**
     * 设置操作异常切入点记录异常日志 扫描所有controller包下操作
     */
    @Pointcut("execution(* com.wp.controller..*.*(..))")
    public void operaExceptionLogPointCut() {
    }

    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     *
     * @param joinPoint 切入点
     * @param result    返回结果
     */
    @AfterReturning(value = "operaLogPointCut()", returning = "result")
    public void saveOperaLog(JoinPoint joinPoint, Object result) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

        try {
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取操作
            MyLog myLog = method.getAnnotation(MyLog.class);

            OperaLog operalog = new OperaLog();
            if (myLog != null) {
                operalog.setTitle(myLog.title());//设置模块名称
                operalog.setContent(myLog.content());//设置日志内容
            }

            // 将传入参数转换成json
            String params = argsArrayToString(joinPoint.getArgs());
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName + "()";
            operalog.setMethod(methodName); //设置请求方法
            operalog.setRequestMethod(request.getMethod());//设置请求方式
            operalog.setRequestParam(params); // 请求参数
            // 设置最大日志记录长度（例如500字符）
            int maxLogLength = 500;
            // 将响应结果转换为JSON字符串
            String resultJson = JSON.toJSONString(result);
            // 截取日志内容的前maxLogLength个字符，避免记录过多内容
            if (resultJson.length() > maxLogLength) {
                resultJson = resultJson.substring(0, maxLogLength) + "...";  // 添加省略号
            }
            operalog.setResponseResult(JSON.toJSONString(resultJson)); // 返回结果
            //从线程本地变量中取用户ID
            Long userId = BaseContext.getCurrentId();
            if (userId != null) {
                operalog.setOperaUserId(userId);
            }else{
                operalog.setOperaUserId(-1L);
            }
            operalog.setIp(getIp(request)); // IP地址
            operalog.setIpLocation("湖南衡阳"); // IP归属地（真实环境中可以调用第三方API根据IP地址，查询归属地）
            operalog.setRequestUrl(request.getRequestURI()); // 请求URI
            operalog.setOperaTime(LocalDateTime.now()); // 时间
            operalog.setStatus(1);//操作状态（0异常 1正常）
            Long takeTime = System.currentTimeMillis() - startTime.get();//记录方法执行耗时时间（单位：毫秒）
            operalog.setTakeTime(takeTime);
            //插入数据库
            operaLogService.insert(operalog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 异常返回通知，用于拦截异常日志信息 连接点抛出异常后执行
     */
    @AfterThrowing(pointcut = "operaExceptionLogPointCut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Throwable e) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

        OperaLog operalog = new OperaLog();
        try {
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName + "()";
            // 获取操作
            MyLog myLog = method.getAnnotation(MyLog.class);
            if (myLog != null) {
                operalog.setTitle(myLog.title());//设置模块名称
                operalog.setContent(myLog.content());//设置日志内容
            }
            // 将入参转换成json
            String params = argsArrayToString(joinPoint.getArgs());
            operalog.setMethod(methodName); //设置请求方法
            operalog.setRequestMethod(request.getMethod());//设置请求方式
            operalog.setRequestParam(params); // 请求参数
            //从线程本地变量中取用户ID
            Long userId = BaseContext.getCurrentId();
            if (userId != null) {
                operalog.setOperaUserId(userId);
            }else{
                operalog.setOperaUserId(-1L);
            }
            operalog.setIp(getIp(request)); // IP地址
            operalog.setIpLocation("湖南衡阳"); // IP归属地（真是环境中可以调用第三方API根据IP地址，查询归属地）
            operalog.setRequestUrl(request.getRequestURI()); // 请求URI
            operalog.setOperaTime(LocalDateTime.now()); // 时间
            operalog.setStatus(0);//操作状态（0异常 1正常）
            operalog.setErrorMsg(stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace()));//记录异常信息
            //插入数据库
            operaLogService.insert(operalog);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /**
     * 转换异常信息为字符串
     */
    public String stackTraceToString(String exceptionName, String exceptionMessage, StackTraceElement[] elements) {
        StringBuffer strBuff = new StringBuffer();
        for (StackTraceElement stet : elements) {
            strBuff.append(stet + "\n");
        }
        String message = exceptionName + ":" + exceptionMessage + "\n\t" + strBuff.toString();
        message = substring(message, 0, 2000);
        return message;
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (o != null) {
                    // 处理 MultipartFile 类型
                    if (o instanceof MultipartFile) {
                        MultipartFile file = (MultipartFile) o;
                        // 只记录文件的名称和大小（根据需要进行调整）
                        params.append("fileName=").append(file.getOriginalFilename())
                                .append(", fileSize=").append(file.getSize())
                                .append(" ");
                    }
                    // 处理 HttpServletResponse 或 HttpServletRequest 类型，忽略它们
                    else if (o instanceof HttpServletResponse || o instanceof HttpServletRequest) {
                        params.append(o.getClass().getSimpleName()).append(" object ");
                    } else {
                        try {
                            // 其他类型的参数转化为 JSON 字符串
                            Object jsonObj = JSON.toJSON(o);
                            params.append(jsonObj.toString()).append(" ");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 字符串截取
     *
     * @param str
     * @param start
     * @param end
     * @return
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        } else {
            if (end < 0) {
                end += str.length();
            }

            if (start < 0) {
                start += str.length();
            }

            if (end > str.length()) {
                end = str.length();
            }

            if (start > end) {
                return "";
            } else {
                if (start < 0) {
                    start = 0;
                }

                if (end < 0) {
                    end = 0;
                }
                return str.substring(start, end);
            }
        }
    }

    /**
     * 根据HttpServletRequest获取访问者的IP地址
     *
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}