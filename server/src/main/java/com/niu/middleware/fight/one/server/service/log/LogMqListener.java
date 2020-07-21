package com.niu.middleware.fight.one.server.service.log;

import com.niu.middleware.fight.one.model.entity.SysLog;
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
 * @Description: 日志消息队列监听
 * @Author nza
 * @Date 2020/7/12
 **/
@Component
@Slf4j
public class LogMqListener {

    @Autowired
    private LogService logService;

    // 指定监听的队列, 以及监听消费处理消息的模式(单一消费者-单一线程)
    @RabbitListener(queues = {"${mq.log.queue}"}, containerFactory = "singleListenerContainer")
    public void consumerLogMsg(@Payload SysLog sysLog, @Headers Map<String, Object> headers,
                               Channel channel) {
        try {
            log.info("日志监听-消费者-监听到消息");

            logService.recordLog(sysLog);

            // 获取消息唯一标识
            Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
            log.info("签收消息: {}", deliveryTag);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("日志监听-消费者-发生异常: {}", e);
        }
    }
}
