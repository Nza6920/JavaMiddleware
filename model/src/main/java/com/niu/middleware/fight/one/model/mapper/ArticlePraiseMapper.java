package com.niu.middleware.fight.one.model.mapper;

import com.niu.middleware.fight.one.model.entity.ArticlePraise;
import org.apache.ibatis.annotations.Param;

public interface ArticlePraiseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ArticlePraise record);

    int insertSelective(ArticlePraise record);

    ArticlePraise selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ArticlePraise record);

    int updateByPrimaryKey(ArticlePraise record);

    int cancelPraise(@Param("articleId") Integer articleId, @Param("userId") Integer userId);
}