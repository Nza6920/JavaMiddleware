package com.niu.middleware.fight.one.server.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 短信DTO
 *
 * @author [nza]
 * @version 1.0 [2020/07/15 10:55]
 * @createTime [2020/07/15 10:55]
 */
@Data
public class SmsDto implements Serializable {
    public SmsDto() {
    }

    public SmsDto(String phone, String code) {
        this.phone = phone;
        this.code = code;
    }

    private String phone;

    private String code;
}
