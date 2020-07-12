package com.niu.middleware.fight.one.server.service.log;

import java.lang.annotation.*;

/**
 * spring aop 触发点
 *
 * @Description: TODO
 * @Author nza
 * @Date 2020/6/26
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAopAnnotation {

    String value() default "";
}
