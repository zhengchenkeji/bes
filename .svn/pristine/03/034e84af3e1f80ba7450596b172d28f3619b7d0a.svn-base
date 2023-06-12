package com.zc.websocket.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zc.common.constant.RedisStreamConstants;
import com.zc.common.core.redis.stream.RedisStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 处理接收 Redis 缓存订阅消息
 * @author Athena-xiepufeng
 */
@Component
public class WebSocketStreamDirectionalListener implements StreamListener<String, ObjectRecord<String, String>>
{
    private static final Logger log = LoggerFactory.getLogger(WebSocketStreamDirectionalListener.class);

    @Autowired
    private RedisStream redisStream;

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        String streamKay = message.getStream();
        RecordId recordId = message.getId();

        String key = RedisStreamConstants.WebSocketDirectional.KEY;
        String group = RedisStreamConstants.WebSocketDirectional.GROUP;

        if (!Objects.equals(streamKay, key)) {
            log.error("队列不匹配 接收 key: {}, 定义的 key：{}", streamKay, key);
            return;
        }
        
        try {
            JsonElement jsonElement = JsonParser.parseString(message.getValue());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject cmdMsg = jsonObject.getAsJsonObject("cmdMsg");
            JsonElement subscriber = jsonObject.get("subscriber");
            WebSocketService.postEvent(subscriber.getAsString(), cmdMsg.toString());
            // 消费完成后确认消费（ACK）
            redisStream.ack(streamKay, group, String.valueOf(recordId));
        }catch (Exception e) {
            e.printStackTrace();
            log.error("消费异常 key: {}, 群组：{}, 消息Id：{}", streamKay, group, recordId);
        }
    }
}
