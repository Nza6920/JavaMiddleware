package com.niu.middleware.fight.one.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice implements Serializable{
    private Integer id;

    @NotBlank(message = "公告标题不能为空！")
    private String title;

    @NotBlank(message = "公告内容不能为空！")
    private String content;

    private Byte isActive=1;
}