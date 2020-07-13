package com.niu.middleware.fight.one.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Description: redis 配置
 * @Author nza
 * @Date 2020/7/13
 **/
@Configuration
public class RedisConfig {


    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisTemplate redisTemplate() {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置 key/value 的序列化策略
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 针对hash存储的key的序列化策略
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 开启事务支持
        redisTemplate.setEnableTransactionSupport(true);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {

        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);

        stringRedisTemplate.setEnableTransactionSupport(true);

        return stringRedisTemplate;
    }
}
