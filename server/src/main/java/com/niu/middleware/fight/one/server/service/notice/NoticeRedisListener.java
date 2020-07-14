package com.niu.middleware.fight.one.server.service.notice;

import com.google.gson.Gson;
import com.niu.middleware.fight.one.model.entity.Notice;
import com.niu.middleware.fight.one.server.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: redis 订阅发布机制 - 监听通知公告消息
 * @Author nza
 * @Date 2020/7/13
 **/
@Service("noticeRedisListener")
@Slf4j
public class NoticeRedisListener {

    @Autowired
    private MailService mailService;

    // 监听 并 处理 channel 中的消息 (sub: 订阅)
    public void listenMsg(String message) {
        try {
            log.info("--- 监听到消息: {}", message);

            if (StringUtils.isNotBlank(message) && message.contains("{")) {

                Notice notice = new Gson().fromJson(message, Notice.class);
                mailService.sendSimpleEmail(notice.getTitle(), notice.getContent(), "2388426660@qq.com");
            }
        } catch (Exception e) {
            log.error("订阅发布-发生异常: {}", e.getMessage());
        }
    }
}
