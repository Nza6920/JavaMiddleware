package com.niu.middleware.fight.one.server.service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niu.middleware.fight.one.model.entity.SysLog;
import com.niu.middleware.fight.one.server.enums.Constant;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @Description: 公共Service
 * @Author nza
 * @Date 2020/7/14
 **/
@Service
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
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        rabbitTemplate.convertAndSend(log, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties properties = message.getMessageProperties();
                // 设置消息持久化与消息头
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, SysLog.class);
                return message;
            }
        });

    }
}
