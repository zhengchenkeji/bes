package com.zc.common.core.redis.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * 消息订阅发布工具
 * @author Athena-xiepufeng
 */
@Component
public class RedisPubSub
{

    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;
    @Autowired
    private MessageListenerAdapter messageListenerAdapter;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 订阅消息
     * @param channels 频道（建议在 RedisChannelConstants 类里定义常量）
     */
    public void subscribe(String channels)
    {
        redisMessageListenerContainer.addMessageListener(messageListenerAdapter, new PatternTopic(channels));
    }

    /**
     * 订阅取消
     * @param channels
     */
    public void unsubscribe(String channels)
    {
        redisMessageListenerContainer.removeMessageListener(messageListenerAdapter, new PatternTopic(channels));
    }

    /**
     * 发布消息
     * @param channels
     * @param msg
     */
    public void publish(String channels, Object msg)
    {
        redisTemplate.convertAndSend(channels, msg);
    }
}
