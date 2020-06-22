package com.niu.middleware.fight.one.model.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserVip implements Serializable{
    private Integer id;

    @NotBlank(message = "用户姓名不能为空！")
    private String name;

    @NotBlank(message = "用户手机号不能为空！")
    private String phone;

    @NotBlank(message = "用户邮箱不能为空！")
    private String email;

    //为了测试方便，到时候使用时，时间单位是  秒
    @NotNull(message = "会员天数不能为空！")
    private Integer vipDay;

    private Short isActive=1;

    private Date vipTime;

    private Date updateTime;
}