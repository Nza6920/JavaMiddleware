package com.niu.middleware.fight.one.server.service.msg;

import com.niu.middleware.fight.one.model.entity.SendRecord;
import com.niu.middleware.fight.one.model.mapper.SendRecordMapper;
import com.niu.middleware.fight.one.server.dto.SmsDto;
import com.niu.middleware.fight.one.server.enums.Constant;
import com.niu.middleware.fight.one.server.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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
        int res = sendRecordMapper.insertSelective(entity);

        if (res > 0) {
            // 调用短信api
            mqSendSms(new SmsDto(phone, code));
        }

        return code;
    }

    // 获取短信验证码-sql+定时任务调度
    public Boolean validateCodeV1(String phone, String code) {
        SendRecord record = sendRecordMapper.selectByPhoneCode(phone, code);
        return record != null;
    }

    // 将短信消息塞到mq server里面去
    private void mqSendSms(SmsDto smsDto) {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange(env.getProperty("mq.sms.exchange"));
        rabbitTemplate.convertAndSend(env.getProperty("mq.sms.routing.key"), smsDto, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        MessageProperties properties = message.getMessageProperties();
                        // 设置消息持久化与消息头
                        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, SmsDto.class);
                        return message;
                    }
                },
                new CorrelationData(smsDto.getPhone())
        );
    }

    // 获取短信验证码 - redis 的 key 过期失效 +定时任务调度
    public String getRandomCodeV2(String phone) {

        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        String key = Constant.RedisMsgCodeKey + phone;

        // 查看缓存中是否有存在的key
        if (redisTemplate.hasKey(key)) {
            return ops.get(key);
        }

        String code = RandomUtil.randomMsgCode(4);

        SendRecord entity = new SendRecord(phone, code);
        entity.setSendTime(DateTime.now().toDate());
        int res = sendRecordMapper.insertSelective(entity);

        if (res > 0) {
            // 往redis中存入验证码, 设置超时时间为30分钟
//            ops.set(phone, code, 30L, TimeUnit.MINUTES);
            ops.set(key, code, 1L, TimeUnit.MINUTES);

            // 调用短信api
            mqSendSms(new SmsDto(phone, code));
        }

        return code;
    }

    // 获取短信验证码 - redis 的 key 过期失效
    public Boolean validateCodeV2(String phone, String code) {

        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        String key = Constant.RedisMsgCodeKey + phone;

        // 获取缓存中的code
        String cacheCode = ops.get(key);

        return StringUtils.equals(cacheCode, code);
    }
}
