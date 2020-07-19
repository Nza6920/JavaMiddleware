package com.niu.middleware.fight.one.server.service.vip;

import com.niu.middleware.fight.one.model.entity.UserVip;
import com.niu.middleware.fight.one.model.mapper.UserVipMapper;
import com.niu.middleware.fight.one.server.enums.Constant;
import com.niu.middleware.fight.one.server.request.MailRequest;
import com.niu.middleware.fight.one.server.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.event.EntryEvent;
import org.redisson.api.map.event.EntryExpiredListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @Description: vip过期提醒 - 基于redisson - 监听 mapCache key 失效
 * @Author nza
 * @Date 2020/7/18
 **/
@Slf4j
@Component
public class RedissonMapCacheUserVip implements ApplicationRunner, Ordered {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserVipMapper userVipMapper;

    @Autowired
    private MailService mailService;

    @Autowired
    private Environment env;

    // 应用在启动以及运行期间, 可以不间断的执行一些我们自定义的服务逻辑
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("-----不间断的执行-------");

        this.listenUserVip();
    }

    @Override
    public int getOrder() {
        return 1;
    }

    // 监听用户会员过期的数据 - 1 到期前n天的提醒 2 到期后的提醒 - 需要给相应的用户发送通知(邮件), 告知会员即将过期
    private void listenUserVip() {
        RMapCache<Object, Object> mapCache = redissonClient.getMapCache(Constant.RedissonUserVIPKey);

        mapCache.addListener((EntryExpiredListener<String, Integer>) event -> {

            // 充值id + 类型
            String key = String.valueOf(event.getKey());
            // 充值组件
            String value = String.valueOf(event.getValue());

            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                String[] split = StringUtils.split(key, Constant.SplitCharUserVip);
                // 区分提醒类型
                Integer type = Integer.valueOf(split[1]);
                Integer id = Integer.valueOf(value);

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
            log.info("---监听到用户会员过期数据: key = {} value = {}", event.getKey(), event.getValue());
        });

    }

    // 发送邮件
    private void sendEmail(MailRequest mailRequest, UserVip vip, String subject, String content) {
        if (StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(content)) {
            mailRequest.setSubject(subject);
            if (StringUtils.isNotBlank(content)) {
                mailRequest.setContent(String.format(content, vip.getPhone()));
            }
            mailService.sendEmailByMq(mailRequest);
        }
    }
}
