package com.niu.middleware.fight.one.server.service.mail;

import cn.hutool.core.util.StrUtil;
import com.niu.middleware.fight.one.server.request.MailRequest;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description: TODO
 * @Author nza
 * @Date 2020/7/12
 **/
@Component
@Slf4j
public class MailMqListener {

    @Autowired
    private MailService mailService;

    // 监听消费
    @RabbitListener(queues = {"${mq.email.queue}"}, containerFactory = "mutiListenerContainer")
    public void consumeMsg(@Payload MailRequest request, @Headers Map<String, Object> headers, Channel channel) {
        try {
            log.info("收到消息: {}", request);

            if (request != null && StrUtil.isNotBlank(request.getUserMails())) {
                mailService.sendSimpleEmail(request.getSubject(), request.getContent(), request.getUserMails());

                // 获取消息唯一标识
                Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
                log.info("签收消息: {}", deliveryTag);
                channel.basicAck(deliveryTag, false);
            }
        } catch (Exception e) {
            log.error("监听消费邮件发送的消息-发送异常: {}", e);
        }
    }

}
