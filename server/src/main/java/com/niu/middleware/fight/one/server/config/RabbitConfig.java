package com.niu.middleware.fight.one.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 自定义注入配置
 *
 * @Description: TODO
 * @Author nza
 * @Date 2020/6/26
 **/
@Configuration
public class RabbitConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitConfig.class);

    @Autowired
    private Environment env;

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    // 单一消费者实例
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory singleListenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory, connectionFactory);
//        factory.setConnectionFactory(connectionFactory); 同上
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setDefaultRequeueRejected(false);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setBatchSize(1);
        return factory;
    }

    // 多实例消费者实例
    @Bean(name = "mutiListenerContainer")
    public SimpleRabbitListenerContainerFactory mutiListenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory, connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setDefaultRequeueRejected(false);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.concurrency", int.class));
        factory.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.max-concurrency", int.class));
        factory.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.simple.prefetch", int.class));
        return factory;
    }

    // rabbitmq 自定义注入模板操作组件
    @Bean
    public RabbitTemplate rabbitTemplate() {
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                log.info("消息发送成功: correlationDate={}, ack={}, s={}", correlationData, b, s);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息发送失败: message={}, replyCode={}, replyText={}, exchange={}, routingKey={}", message, replyCode, replyText, exchange, routingKey);
            }
        });

        return rabbitTemplate;
    }

    // 预先创建交换机, 路由及其绑定
    @Bean
    public TopicExchange logExchange() {
        return new TopicExchange(env.getProperty("mq.log.exchange"), true, false);
    }

    @Bean
    public Queue logQueue() {
        return new Queue(env.getProperty("mq.log.queue"), true);
    }

    @Bean
    public Binding logBinding() {
        return BindingBuilder.bind(logQueue()).to(logExchange()).with(env.getProperty("mq.log.routing.key"));
    }

    // 预先创建交换机, 路由及其绑定
    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(env.getProperty("mq.email.exchange"), true, false);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(env.getProperty("mq.email.queue"), true);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange()).with(env.getProperty("mq.email.routing.key"));
    }

    @Bean
    public TopicExchange smsExchange() {
        return new TopicExchange(env.getProperty("mq.sms.exchange"), true, false);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(env.getProperty("mq.sms.queue"), true);
    }

    @Bean
    public Binding smsBinding() {
        return BindingBuilder.bind(smsQueue()).to(smsExchange()).with(env.getProperty("mq.sms.routing.key"));
    }
}
