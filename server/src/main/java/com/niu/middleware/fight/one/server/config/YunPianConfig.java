package com.niu.middleware.fight.one.server.config;

import com.yunpian.sdk.YunpianClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 云片配置类
 *
 * @author [nza]
 * @version 1.0 [2020/07/15 10:11]
 * @createTime [2020/07/15 10:11]
 */
@Configuration
public class YunPianConfig {

    @Autowired
    private Environment env;

    @Bean
    public YunpianClient yunpianClient() {
        return new YunpianClient(env.getProperty("yp.api.key")).init();
    }
}
