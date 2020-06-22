package com.niu.middleware.fight.one.model.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserOrder implements Serializable{
    private Integer id;

    @NotNull(message = "用户id不能为空！")
    private Integer userId;

    private String orderNo;

    private Byte payStatus=1;

    private Byte isActive=1;

    private Date orderTime;

    private Date updateTime;
}