package com.niu.middleware.fight.one.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class SysLog implements Serializable{
    private Long id;

    private String username;

    private String operation;

    private String method;

    private String params;

    private Long time;

    private String ip="127.0.0.1";

    private Date createDate=new Date();

    private String memo;


    public SysLog(String username, String operation, String method) {
        this.username = username;
        this.operation = operation;
        this.method = method;
    }

    public SysLog(String username, String operation, String method, String params) {
        this.username = username;
        this.operation = operation;
        this.method = method;
        this.params = params;
    }
}