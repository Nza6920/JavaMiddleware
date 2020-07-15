package com.niu.middleware.fight.one.server.service.msg;

import com.niu.middleware.fight.one.model.entity.SendRecord;
import com.niu.middleware.fight.one.model.mapper.SendRecordMapper;
import com.niu.middleware.fight.one.server.dto.SmsDto;
import com.niu.middleware.fight.one.server.service.sms.SmsMessageService;
import com.niu.middleware.fight.one.server.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Description: 短信验证码Service
 * @Author nza
 * @Date 2020/7/14
 **/
@Service
@Slf4j
public class MsgCodeService {

    @Autowired
    private SendRecordMapper sendRecordMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private Environment env;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SmsMessageService smsMessageService;

    // 获取短信验证码-sql+定时任务调度
    public String getRandomCodeV1(String phone) {

        // 检查是否有未过期的验证码
        SendRecord record = sendRecordMapper.selectByPhoneCode(phone, null);
        if (record != null && StringUtils.isNotBlank(record.getCode())) {
            return record.getCode();
        }

        String code = RandomUtil.randomMsgCode(4);

        SendRecord entity = new SendRecord(phone, code);
        entity.setSendTime(DateTime.now().toDate());
        sendRecordMapper.insertSelective(entity);

        // 调用短信api
        mqSendSms(new SmsDto(phone, code));

        return code;
    }

    // 获取短信验证码-sql+定时任务调度
    public Boolean validateCodeV1(String phone, String code) {
        SendRecord record = sendRecordMapper.selectByPhoneCode(phone, code);
        return record != null;
    }

    // 将短信消息塞到mq server里面去
    private void mqSendSms(SmsDto smsDto) {
        // 设置交换机
        rabbitTemplate.setExchange(env.getProperty("mq.sms.exchange"));
        // 设置路由
        rabbitTemplate.setRoutingKey(env.getProperty("mq.sms.routing.key"));
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        rabbitTemplate.convertAndSend(smsDto, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties properties = message.getMessageProperties();
                // 设置消息持久化与消息头
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, SmsDto.class);
                return message;
            }
        });
    }
}
