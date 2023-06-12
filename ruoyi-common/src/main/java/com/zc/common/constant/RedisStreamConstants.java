package com.zc.common.constant;

/**
 * redis Stream 消息队列、组、消费者定义
 */
public class RedisStreamConstants {

    /**
     * websocket 队列、组、消费者定义（指定客户端）
     */
    public static class WebSocketDirectional {
        public final static String KEY = "websocket:directional";
        public final static String GROUP = "group1";
        public final static String CONSUMER = "consumer1";
    }

    /**
     * websocket 队列、组、消费者定义（广播）
     */
    public static class WebSocketStreamBroadcast {
        public final static String KEY = "websocket:broadcast";
        public final static String GROUP = "group1";
        public final static String CONSUMER = "consumer1";
    }
}
