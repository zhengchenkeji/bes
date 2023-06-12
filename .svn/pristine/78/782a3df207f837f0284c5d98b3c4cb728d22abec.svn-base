package com.zc.websocket;

import com.zc.common.constant.RedisChannelConstants;
import com.zc.common.constant.RedisStreamConstants;
import com.zc.common.core.redis.pubsub.RedisPubSub;
import com.zc.common.core.redis.stream.RedisStream;
import com.zc.websocket.handler.WebSocketStarter;
import com.zc.websocket.handler.WebSocketStreamBroadcastListener;
import com.zc.websocket.handler.WebSocketStreamDirectionalListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * websocket 初始化器
 * 1.websocket 启动
 * 2.添加 WebSocket 消息订阅
 * @author Athena-xiepufeng
 */
@Component
public class WebSocketInitializer implements CommandLineRunner {

    @Autowired
    private WebSocketConfigParam param;

    @Autowired
    private RedisStream redisStream;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private WebSocketStreamBroadcastListener broadcastListener;

    @Resource
    private WebSocketStreamDirectionalListener directionalListener;

    @Override
    public void run(String... args) {

        // 添加 WebSocket 消息订阅
        redisStream.subscription(
                RedisStreamConstants.WebSocketStreamBroadcast.KEY,
                RedisStreamConstants.WebSocketStreamBroadcast.GROUP,
                RedisStreamConstants.WebSocketStreamBroadcast.CONSUMER,
                broadcastListener);
        redisStream.subscription(
                RedisStreamConstants.WebSocketDirectional.KEY,
                RedisStreamConstants.WebSocketDirectional.GROUP,
                RedisStreamConstants.WebSocketDirectional.CONSUMER,
                directionalListener);

        // WebSocket 启动
        threadPoolTaskExecutor.execute(() -> {
            WebSocketStarter webSocketStarter = new WebSocketStarter(
                    param.getPort(),
                    param.getPath(),
                    param.getPassword(),
                    param.getInterval(),
                    param.getTimeoutIntervals());
            webSocketStarter.run();
        });

    }
}
