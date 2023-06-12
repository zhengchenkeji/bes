package com.zc.common.core.redis.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;

/**
 * Redis 订阅消息监听器
 * @author Athena-xiepufeng
 */
@Component
public class RedisReceiver implements MessageListener
{

    private static final Logger log = LoggerFactory.getLogger(RedisReceiver.class);

    @Override
    public void onMessage(Message message, byte[] pattern)
    {
        // 1.获取所有接口实现
        ServiceLoader<RedisMessageDispatcher> load = ServiceLoader.load(RedisMessageDispatcher.class);

        if (!load.iterator().hasNext())
        {
            log.warn("RedisMessageDispatcher 没有实现类");
            return;
        }


        // 2.遍历所有实现类根据订阅信息分发消息
        load.forEach(redisMessageDispatcher ->
        {
            String channel = new String(message.getChannel());

            // 3.调用isChannelExist方法判断是否有当前订阅，有则发消息。
            if (redisMessageDispatcher.isChannelExist(channel))
            {
                redisMessageDispatcher.onMessage(message, pattern);
            }
        });
    }
}
