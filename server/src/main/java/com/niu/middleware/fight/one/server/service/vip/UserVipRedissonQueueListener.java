package com.niu.middleware.fight.one.server.service.vip;

import com.niu.middleware.fight.one.model.entity.UserVip;
import com.niu.middleware.fight.one.model.mapper.UserVipMapper;
import com.niu.middleware.fight.one.server.enums.Constant;
import com.niu.middleware.fight.one.server.request.MailRequest;
import com.niu.middleware.fight.one.server.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Description: 用户会员到期 - 基于 redisson 的延迟队列
 * @Author nza
 * @Date 2020/7/19
 **/
@Component
@Slf4j
public class UserVipRedissonQueueListener {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserVipMapper userVipMapper;

    @Autowired
    private Environment env;

    @Autowired
    private MailService mailService;

    @Async("threadPoolTaskExecutor")
    @Scheduled(cron = "0/5 * * * * ?")
    public void manageExpireVip() {

        RBlockingQueue<String> blockingQueue = redissonClient.getBlockingQueue(Constant.RedissonUserVipQueue);

        if (blockingQueue != null && !blockingQueue.isEmpty()) {
            String element = blockingQueue.poll();
            log.info("监听到 redisson 延迟队列消息: {}", element);

            if (StringUtils.isNotEmpty(element)) {
                String[] arr = StringUtils.split(element, Constant.SplitCharUserVip);

                Integer id = Integer.valueOf(arr[0]);
                Integer type = Integer.valueOf(arr[1]);

                UserVip vip = userVipMapper.selectByPrimaryKey(id);
                if (vip != null && vip.getIsActive() == 1 && StringUtils.isNotBlank(vip.getEmail())) {
                    String subject = StringUtils.EMPTY;
                    String content = StringUtils.EMPTY;

                    MailRequest mailRequest = new MailRequest();
                    mailRequest.setUserMails(vip.getEmail());

                    if (Constant.VipExpireFlg.First.getType().equals(type)) {
                        subject = env.getProperty("vip.expire.first.subject");
                        content = env.getProperty("vip.expire.first.content");
                    } else {
                        log.info("redisson 延迟队列第二次提醒");
                        int res = userVipMapper.updateExpireVip(vip.getId());
                        if (res > 0) {
                            subject = env.getProperty("vip.expire.end.subject");
                            content = env.getProperty("vip.expire.end.content");
                        }
                    }

                    // 发送邮件
                    sendEmail(mailRequest, vip, subject, content);
                }
            }
        }
    }

    // 发送邮件
    private void sendEmail(MailRequest mailRequest, UserVip vip, String subject, String content) {
        if (StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(content)) {
            log.info("redisson 延迟队列发送邮件");
            mailRequest.setSubject(subject);
            if (StringUtils.isNotBlank(content)) {
                mailRequest.setContent(String.format(content, vip.getPhone()));
            }
            mailService.sendEmailByMq(mailRequest);
        }
    }
}
