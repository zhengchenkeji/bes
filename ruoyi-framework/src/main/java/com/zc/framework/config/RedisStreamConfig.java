package com.zc.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

/**
 * @author xiepufeng
 */
@Configuration
public class RedisStreamConfig {

    @Bean
    public StreamMessageListenerContainer
            .StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> streamMessageListenerContainerOptions() {
        // 创建Stream消息监听容器配置
        return StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
        // 设置阻塞时间
        .pollTimeout(Duration.ofSeconds(1))
        // 配置消息类型
        .targetType(String.class)
        .build();
    }

    /**
     * 创建Stream消息监听容器
     * @return
     */
    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer(RedisConnectionFactory factory, StreamMessageListenerContainer
            .StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options) {
        // 创建Stream消息监听容器
        return StreamMessageListenerContainer.create(factory, options);
    }

}
