package com.niu.middleware.fight.one.server.config;

import com.niu.middleware.fight.one.server.enums.Constant;
import com.niu.middleware.fight.one.server.service.notice.NoticeRedisListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @Description: 用户消息订阅-发布
 * @Author nza
 * @Date 2020/7/13
 **/
@Configuration
public class NoticeRedisConfig {


    @Autowired
    private NoticeRedisListener noticeRedisListener;

    // redis 消息监听器容器-发布订阅通过此容器完成
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory factory, MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(factory);

        // 添加一个到多个topic(频道)
        container.addMessageListener(listenerAdapter, new PatternTopic(Constant.RedisTopicNameEmail));
        return container;
    }

    @Bean
    public RedisConnectionFactory factory() {
        return new JedisConnectionFactory();
    }

    // 绑定消息 - 消息监听器 - 监听接收消息的方法
    @Bean
    public MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(noticeRedisListener, "listenMsg");
    }
}
