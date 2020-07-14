package com.niu.middleware.fight.one.server.controller;

import com.niu.middleware.fight.one.api.response.BaseResponse;
import com.niu.middleware.fight.one.api.response.StatusCode;
import com.niu.middleware.fight.one.server.service.msg.MsgCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 短信验证码失效验证
 * @Author nza
 * @Date 2020/7/14
 **/
@RestController
@RequestMapping("msg/code")
public class MsgController extends AbstractController {

    @Autowired
    private MsgCodeService msgCodeService;

    // 发送短信
    @RequestMapping("send")
    public BaseResponse sendCode(@RequestParam String phone) {
        if (StringUtils.isBlank(phone)) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            response.setData(msgCodeService.getRandomCodeV1(phone));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }

    // 验证短信验证码
    @RequestMapping("validate")
    public BaseResponse validateCode(@RequestParam String phone, @RequestParam String code) {
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            response.setData(msgCodeService.validateCodeV1(phone, code));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }
}
