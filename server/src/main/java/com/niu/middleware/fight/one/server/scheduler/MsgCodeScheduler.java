package com.niu.middleware.fight.one.server.scheduler;

import com.google.common.base.Joiner;
import com.niu.middleware.fight.one.model.entity.SendRecord;
import com.niu.middleware.fight.one.model.mapper.SendRecordMapper;
import com.niu.middleware.fight.one.server.service.common.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 短信验证码及时失效
 * @Author nza
 * @Date 2020/7/14
 **/
@Component
@Slf4j
public class MsgCodeScheduler {


    @Autowired
    private SendRecordMapper sendRecordMapper;

    @Autowired
    private CommonService commonService;

    // 定时检测验证码的有效性
    @Scheduled(cron = "0/30 * * * * ?")
    @Async("threadPoolTaskExecutor")
    public void schedulerCheckCode() {

        try {
            List<SendRecord> sendRecords = sendRecordMapper.selectTimeoutCodes();
            if (sendRecords != null && !sendRecords.isEmpty()) {
                Set<Integer> ids = sendRecords.stream().map(SendRecord::getId).collect(Collectors.toSet());
                String join = Joiner.on(",").join(ids);
                sendRecordMapper.updateTimeoutCode(join);

                // 发送mq消息, 记录日志
                commonService.recordLog(ids, "短信验证码失效", "schedulerCheckCode");
            }
        } catch (Exception e) {
            log.error("定时检测验证码-发生异常: {}", e);
        }
    }
}
