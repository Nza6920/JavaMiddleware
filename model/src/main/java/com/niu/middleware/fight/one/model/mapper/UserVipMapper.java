package com.niu.middleware.fight.one.model.mapper;

import com.niu.middleware.fight.one.model.entity.UserVip;
import org.apache.ibatis.annotations.Param;

public interface UserVipMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserVip record);

    int insertSelective(UserVip record);

    UserVip selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserVip record);

    int updateByPrimaryKey(UserVip record);

    int updateExpireVip(@Param("id") Integer id);
}