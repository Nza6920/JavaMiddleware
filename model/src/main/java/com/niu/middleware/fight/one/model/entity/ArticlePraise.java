package com.niu.middleware.fight.one.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class ArticlePraise implements Serializable{
    private Integer id;

    private Integer articleId;

    private Integer userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date pTime;

    private Date updateTime;

    public ArticlePraise(Integer articleId, Integer userId, Date pTime) {
        this.articleId = articleId;
        this.userId = userId;
        this.pTime = pTime;
    }
}