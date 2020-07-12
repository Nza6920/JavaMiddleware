package com.niu.middleware.fight.one.server.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2020/3/13 16:11
 **/
@Data
public class MailRequest implements Serializable {

    @NotBlank(message = "用户邮箱不能为空！")
    private String userMails;

    @NotBlank(message = "邮件主题不能为空！")
    private String subject;

    @NotBlank(message = "邮件内容不能为空！")
    private String content;

}