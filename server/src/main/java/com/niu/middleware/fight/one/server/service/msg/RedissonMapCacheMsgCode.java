package com.niu.middleware.fight.one.server.service.msg;

import com.niu.middleware.fight.one.model.mapper.SendRecordMapper;
import com.niu.middleware.fight.one.server.enums.Constant;
import com.niu.middleware.fight.one.server.service.common.CommonService;
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
import org.springframework.stereotype.Component;

/**
 * @Description: 短信验证码失效 - 基于redisson - 监听 mapCache key 失效
 * @Author nza
 * @Date 2020/7/18
 **/
@Slf4j
@Component
public class RedissonMapCacheMsgCode implements ApplicationRunner, Ordered {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SendRecordMapper sendRecordMapper;

    @Autowired
    private CommonService commonService;

    // 应用在启动以及运行期间, 可以不间断的执行一些我们自定义的服务逻辑
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("-----不间断的执行-------");

        this.listenExpireCode();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    // 监听mapCache里过期失效的验证码
    private void listenExpireCode() {
        RMapCache<String, String> mapCache = redissonClient.getMapCache(Constant.RedissonMsgCodeKey);

        mapCache.addListener(new EntryExpiredListener<String, String>() {
            @Override
            public void onExpired(EntryEvent<String, String> entryEvent) {
                String phone = entryEvent.getKey();
                String msgCode = entryEvent.getValue();

                log.info("---- 当前手机号: {}, 验证码: {} ----即将失效", phone, msgCode);

                if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(msgCode)) {
                    int res = sendRecordMapper.updateExpirePhoneCode(phone, msgCode);

                    if (res > 0) {
                        try {
                            commonService.recordLog(phone + "--" + msgCode, "mapCache监听验证码失效", "listenExpireCode");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
