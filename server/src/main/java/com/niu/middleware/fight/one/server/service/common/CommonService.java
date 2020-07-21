package com.niu.middleware.fight.one.server.service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niu.middleware.fight.one.model.entity.SysLog;
import com.niu.middleware.fight.one.server.enums.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @Description: 公共Service
 * @Author nza
 * @Date 2020/7/14
 **/
@Service
@Slf4j
public class CommonService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    @Autowired
    private ObjectMapper objectMapper;

    //统一用户记录日志
    public void recordLog(final Object obj, final String operation, final String method) throws Exception {
        SysLog log = new SysLog(Constant.logOperateUser, operation, method, objectMapper.writeValueAsString(obj));

        rabbitTemplate.setExchange(env.getProperty("mq.log.exchange"));
        rabbitTemplate.setRoutingKey(env.getProperty("mq.log.routing.key"));

        Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(log))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        rabbitTemplate.send(msg);
    }
}
