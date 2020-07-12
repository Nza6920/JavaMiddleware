package com.niu.middleware.fight.one.server.service.log;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.niu.middleware.fight.one.model.entity.SysLog;
import com.niu.middleware.fight.one.server.enums.Constant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 切面
 *
 * @Description: TODO
 * @Author nza
 * @Date 2020/6/26
 **/
@Aspect
@Component
public class LogAopAspect {

    @Autowired
    private LogService logService;


    // 起点: 使用了特点注解的触发
    @Pointcut("@annotation(com.niu.middleware.fight.one.server.service.log.LogAopAnnotation)")
    public void logPointCut() {

    }

    // 通知: 环绕通知
    @Around("logPointCut()")
    public void excuteAround(ProceedingJoinPoint joinPoint) throws Throwable {

        System.currentTimeMillis();

        Long start = System.currentTimeMillis();

        Object res = joinPoint.proceed();

        Long time = System.currentTimeMillis() - start;

        saveLog(joinPoint, time, res);

    }

    // 记录日志(aop - 动态代理)
    private void saveLog(ProceedingJoinPoint point, Long time, Object res) throws Exception {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        SysLog entity = new SysLog();

        // 获取注解上用户操作描述
        LogAopAnnotation annotation = method.getAnnotation(LogAopAnnotation.class);
        if (annotation != null) {
            entity.setOperation(annotation.value());
        }

        // 获取操作的方法名
        String className = point.getTarget().getClass().getName();
        String methodName = signature.getName();
        entity.setMethod(new StringBuilder(className).append(".").append(methodName).append("()").toString());

        // 获取请求参数
        Object[] args = point.getArgs();
        String params = new Gson().toJson(args[0]);
        entity.setParams(params);

        entity.setTime(time);
        entity.setUsername(Constant.logOperateUser);
        entity.setCreateDate(DateTime.now().toDate());

        // 方法执行结果
        if (res != null && StrUtil.isNotEmpty(res.toString())) {
            entity.setMemo(new Gson().toJson(res));
        }

        logService.recordLog(entity);
    }
}
