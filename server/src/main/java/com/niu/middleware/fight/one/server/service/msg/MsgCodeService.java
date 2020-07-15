package com.niu.middleware.fight.one.server.service.msg;

import com.niu.middleware.fight.one.model.entity.SendRecord;
import com.niu.middleware.fight.one.model.mapper.SendRecordMapper;
import com.niu.middleware.fight.one.server.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
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
        return code;

    }

    // 获取短信验证码-sql+定时任务调度
    public Boolean validateCodeV1(String phone, String code) {
        SendRecord record = sendRecordMapper.selectByPhoneCode(phone, code);
        return record != null;
    }


}
