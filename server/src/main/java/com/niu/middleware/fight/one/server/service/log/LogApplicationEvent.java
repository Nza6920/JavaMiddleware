package com.niu.middleware.fight.one.server.service.log;

import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * spring 消息驱动模型: applicationEvent applicationListener
 *
 * @Description: TODO
 * @Author nza
 * @Date 2020/6/22
 **/
public class LogApplicationEvent extends ApplicationEvent implements Serializable {

    private String username;

    private String operation;

    private String params;

    private String method;

    public LogApplicationEvent(Object source, String username, String operation, String method) {
        super(source);
        this.username = username;
        this.operation = operation;
        this.method = method;
    }

    public LogApplicationEvent(Object source, String username, String operation, String params, String method) {
        super(source);
        this.username = username;
        this.operation = operation;
        this.params = params;
        this.method = method;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
