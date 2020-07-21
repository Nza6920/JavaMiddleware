package com.niu.middleware.fight.one.server.controller;

import com.niu.middleware.fight.one.api.response.BaseResponse;
import com.niu.middleware.fight.one.api.response.StatusCode;
import com.niu.middleware.fight.one.server.request.MailRequest;
import com.niu.middleware.fight.one.server.service.mail.MailService;
import com.niu.middleware.fight.one.server.utils.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: TODO
 * @Author nza
 * @Date 2020/7/12
 **/
@RestController
@RequestMapping("mail")
public class MailController extends AbstractController {

    @Autowired
    private MailService mailService;

    @RequestMapping("send/mq")
    public BaseResponse sendMail(@RequestBody @Validated MailRequest mailRequest, BindingResult result) {
        String checkRes = ValidatorUtil.checkResult(result);
        if (StringUtils.isNotBlank(checkRes)) {
            return new BaseResponse(StatusCode.InvalidParams.getCode(), checkRes);
        }

        BaseResponse response = new BaseResponse(StatusCode.Success);

        try {
            // 直接将邮件信息充当消息塞入mq
            mailService.sendEmailByMq(mailRequest);
        } catch (Exception e) {
            log.error("异常信息: {}", e);
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }
}
