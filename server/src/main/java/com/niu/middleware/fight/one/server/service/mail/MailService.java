package com.niu.middleware.fight.one.server.service.mail;

import com.niu.middleware.fight.one.server.request.MailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 邮件服务
 *
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2020/3/13 21:45
 **/
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private Environment env;

    @Autowired
    private JavaMailSender mailSender;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Async("threadPoolTaskExecutor")
    public void sendSimpleEmail(final String subject, final String content, final String... tos) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(subject);
            message.setText(content);
            message.setTo(tos);
            message.setFrom(env.getProperty("mail.send.from"));
            mailSender.send(message);

            log.info("----发送简单的邮件消息完毕--->");
        } catch (Exception e) {
            log.error("--发送简单的邮件消息,发生异常：", e.fillInStackTrace());
        }
    }

    public void sendEmailByMq(MailRequest mailRequest) {
        // 直接将邮件信息充当消息塞入mq
        rabbitTemplate.setExchange(env.getProperty("mq.email.exchange"));
        rabbitTemplate.setRoutingKey(env.getProperty("mq.email.routing.key"));

        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.convertAndSend(mailRequest, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties properties = message.getMessageProperties();
                // 设置消息持久化与消息头
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MailRequest.class);

                return message;
            }
        });
    }

}