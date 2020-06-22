package com.niu.middleware.fight.one.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class SendRecord implements Serializable{
    private Integer id;

    private String phone;

    private String code;

    private Byte isActive=1;

    private Date sendTime;

    public SendRecord(String phone, String code) {
        this.phone = phone;
        this.code = code;
    }
}