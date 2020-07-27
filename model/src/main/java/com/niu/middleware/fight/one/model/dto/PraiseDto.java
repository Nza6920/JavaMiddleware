package com.niu.middleware.fight.one.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户点赞
 */
@Data
@NoArgsConstructor
public class PraiseDto implements Serializable {

    //当前用户id
    @NotNull(message = "当前用户id不能为为空！")
    private Integer userId;

    //文章id
    @NotNull(message = "当前文章id不能为为空！")
    private Integer articleId;

    //文章标题 ~ 开发技巧 ~ 服务于排行榜(如微博的热搜，只显示其标题，而不需要再根据id查询db获取标题...)
    @NotBlank(message = "当前文章标题不能为空！")
    private String title;

    public PraiseDto(Integer userId, Integer articleId, String title) {
        this.userId = userId;
        this.articleId = articleId;
        this.title = title;
    }
}