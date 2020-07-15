package com.niu.middleware.fight.one.server.service.sms;

import cn.hutool.core.util.StrUtil;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 短信业务类
 *
 * @author [nza]
 * @version 1.0 [2020/07/15 09:44]
 * @createTime [2020/07/15 09:44]
 */
@Service
@Slf4j
public class SmsMessageService {

    @Autowired
    private YunpianClient yunpianClient;

    @Autowired
    private Environment env;

    public void sendCode(String phone, String code) {
        // 构建 msg
        String msg = StrUtil.replace(env.getProperty("yp.template"), env.getProperty("yp.template.placeholder"), code);

        Map<String, String> param = yunpianClient.newParam(2);
        param.put(YunpianClient.MOBILE, phone);
        param.put(YunpianClient.TEXT, msg);
        Result<SmsSingleSend> res = yunpianClient.sms().single_send(param);

        // 如果不成功记录日志
        if (!res.isSucc()) {
            log.error("云片短信发送异常: {}", res.getMsg());
        }
    }
}
