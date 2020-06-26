package com.niu.middleware.fight.one.server.service.log;

import com.niu.middleware.fight.one.model.entity.SysLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/**
 * 消息监听器
 * @Description: TODO
 * @Author nza
 * @Date 2020/6/22
 **/
@Component
public class LogApplicationListener implements ApplicationListener<LogApplicationEvent> {

    public static final Logger log = LoggerFactory.getLogger(LogApplicationListener.class);

    @Autowired
    private LogService logService;

    // 监听并处理消息
    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(LogApplicationEvent event) {
        log.info("spring 消息驱动模型-监听并处理消息: {}", event);

        try {
            if (event != null) {
                SysLog entity = new SysLog(event.getUsername(), event.getOperation(), event.getMethod(), event.getParams());
                logService.recordLog(entity);
            }
        } catch (Exception e) {
            log.error("spring 消息驱动模型-监听并处理消息 发生异常", e);
        }
    }
}
