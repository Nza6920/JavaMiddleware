package com.niu.middleware.fight.one.server.controller;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.niu.middleware.fight.one.api.response.BaseResponse;
import com.niu.middleware.fight.one.api.response.StatusCode;
import com.niu.middleware.fight.one.model.entity.SysLog;
import com.niu.middleware.fight.one.model.entity.User;
import com.niu.middleware.fight.one.model.mapper.UserMapper;
import com.niu.middleware.fight.one.server.enums.Constant;
import com.niu.middleware.fight.one.server.service.log.LogAopAnnotation;
import com.niu.middleware.fight.one.server.service.log.LogApplicationEvent;
import com.niu.middleware.fight.one.server.utils.ValidatorUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: TODO
 * @Author nza
 * @Date 2020/6/22
 **/
@RestController
@RequestMapping("log")
public class LogController extends AbstractController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;

    // 用户操作-新增用户 - spring 消息驱动模型
    @RequestMapping("user/add")
    public BaseResponse addUser(@RequestBody @Validated User user, BindingResult result) {

        String error = ValidatorUtil.checkResult(result);

        if (StrUtil.isNotEmpty(error)) {
            return new BaseResponse(StatusCode.InvalidParams.getCode(), error);
        }

        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            int res = userMapper.insertSelective(user);
            if (res > 0) {
                // 记录日志
                LogApplicationEvent event = new LogApplicationEvent(this, "niu", "add user", new Gson().toJson(user),"addUser");
                applicationEventPublisher.publishEvent(event);
            }
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }

    // 用户操作-新增用户 - spring aop
    @RequestMapping("user/add/aop")
    @LogAopAnnotation("add user - spring aop")
    public BaseResponse addUserV2(@RequestBody @Validated User user, BindingResult result) {

        String error = ValidatorUtil.checkResult(result);

        if (StrUtil.isNotEmpty(error)) {
            return new BaseResponse(StatusCode.InvalidParams.getCode(), error);
        }

        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            userMapper.insertSelective(user);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }


    // 用户操作-新增用户 - rabbit mq
    @RequestMapping("user/add/rabbitmq")
    public BaseResponse addUserV3(@RequestBody @Validated User user, BindingResult result) {
        String error = ValidatorUtil.checkResult(result);

        if (StrUtil.isNotEmpty(error)) {
            return new BaseResponse(StatusCode.InvalidParams.getCode(), error);
        }

        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            int res = userMapper.insertSelective(user);
            if (res > 0) {
                SysLog log = new SysLog(Constant.logOperateUser, "add User - rabbitmq", "addUser3");
                this.mqSendLog(log);
            }
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }

    // 将日志信息充当消息塞到mq server里面去
    private void mqSendLog(SysLog log) throws Exception{
        // 设置交换机
        rabbitTemplate.setExchange(env.getProperty("mq.log.exchange"));
        // 设置路由
        rabbitTemplate.setRoutingKey(env.getProperty("mq.log.routing.key"));

        Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(log))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();

        rabbitTemplate.send(msg);
    }
}
