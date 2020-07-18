package com.niu.middleware.fight.one.server.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @Description: 自定义注入redisson的操作组件
 * @Author nza
 * @Date 2020/7/18
 **/
@Configuration
public class RedissonConfig {


    @Autowired
    private Environment env;

    // 单节点
    @Bean
    public RedissonClient client() {
        Config config = new Config();

        config.useSingleServer().setAddress(env.getProperty("redisson.url.single"));

        RedissonClient client = Redisson.create(config);

        return client;
    }

    // 集群
//    @Bean
//    public RedissonClient client() {
//        Config config = new Config();
//
//        config.useClusterServers().setScanInterval(2000)
//            .addNodeAddress(StringUtils.split(env.getProperty("redisson.url.cluster"), ","));
//
//        RedissonClient client = Redisson.create(config);
//
//        return client;
//    }
}
