package com.niu.middleware.fight.one.server.controller;

import cn.hutool.core.util.StrUtil;
import com.niu.middleware.fight.one.api.response.BaseResponse;
import com.niu.middleware.fight.one.api.response.StatusCode;
import com.niu.middleware.fight.one.model.entity.UserVip;
import com.niu.middleware.fight.one.model.mapper.UserVipMapper;
import com.niu.middleware.fight.one.server.service.vip.UserVipService;
import com.niu.middleware.fight.one.server.utils.ValidatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: redisson - vip过期提醒
 * @Author nza
 * @Date 2020/7/19
 **/
@RestController
@RequestMapping("user/vip")
@Slf4j
public class UserVipController {

    @Autowired
    private UserVipService userVipService;

    @PostMapping("put1")
    public BaseResponse put(@RequestBody @Validated UserVip userVip, BindingResult result) {
        String checkRes = ValidatorUtil.checkResult(result);
        if (StrUtil.isNotBlank(checkRes)) {
            return new BaseResponse(StatusCode.InvalidParams.getCode(), checkRes);
        }

        BaseResponse response = new BaseResponse(StatusCode.Success);

        try {
            // TODO: 2020/7/19
            userVipService.addVip(userVip);
        } catch (Exception e) {
            log.error("充值会员-发送异常: {}", e.fillInStackTrace());
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }

}
