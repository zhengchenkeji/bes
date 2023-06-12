package com.zc.common.core.redis.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 对redis stream命令的一些实现，可单独使用
 * @author xiepufeng
 */
@Component
public class RedisStream {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private StreamMessageListenerContainer<String, ObjectRecord<String, String>> listenerContainer;
    
    /**
     * 订阅消息
     * @param key 队列
     * @param group 消费组名称
     * @param consumer 消费者名称
     * @param streamListener 消息监控器
     *  收到消息后不自动确认，需要用户选择合适的时机确认
     * 当某个消息被ACK，PEL列表就会减少
     * 如果忘记确认（ACK），则PEL列表会不断增长占用内存
     * 如果服务器发生意外，重启连接后将再次收到PEL中的消息ID列表
     */
    public void subscription(
            String key,
            String group,
            String consumer,
            StreamListener<String, ObjectRecord<String, String>> streamListener) {

       try {
           // 创建消费组
           creatGroup(key, group);
       } catch (Exception e) {
           System.out.println(e.getMessage());
       }

        // 设置消费手动提交配置
        // > 表示没消费过的消息
        // $ 表示最新的消息
        listenerContainer.receive(
                // 设置消费者分组和名称
                Consumer.from(group, consumer),
                // 设置订阅Stream的key和获取偏移量，以及消费处理类
                StreamOffset.create(key, ReadOffset.lastConsumed()), streamListener);
        // 监听容器启动
        listenerContainer.start();
    }

    /**
     * 订阅消息
     * @param key 队列
     * @param group 消费组名称
     * @param consumer 消费者名称
     * @param streamListener 消息监控器
     *  自动 ack
     */
    public void subscriptionAutoAck(
            String key,
            String group,
            String consumer,
            StreamListener<String, ObjectRecord<String, String>> streamListener) {

        try {
            // 创建消费组
            creatGroup(key, group);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // 设置消费手动提交配置
        // > 表示没消费过的消息
        // $ 表示最新的消息
        listenerContainer.receiveAutoAck(
                // 设置消费者分组和名称
                Consumer.from(group, consumer),
                // 设置订阅Stream的key和获取偏移量，以及消费处理类
                StreamOffset.create(key, ReadOffset.lastConsumed()), streamListener);
        // 监听容器启动
        listenerContainer.start();
    }

    /**
     * 创建消费组
     *
     * @param key 队列
     * @param group 消费组名称
     * @return
     */
    public String creatGroup(String key, String group) {
        return redisTemplate.opsForStream().createGroup(key, group);
    }

    /**
     * 消费组信息
     *
     * @param key 队列
     * @param group 消费组
     * @return
     */
    public StreamInfo.XInfoConsumers consumers(String key, String group) {
        return redisTemplate.opsForStream().consumers(key, group);
    }

    /**
     * 确认已消费
     *
     * @param key 队列
     * @param group 消费者
     * @param recordIds 消息id
     * @return
     */
    public Long ack(String key, String group, String... recordIds) {
        return redisTemplate.opsForStream().acknowledge(key, group, recordIds);
    }

    /**
     * 追加消息
     *
     * @param key 队列
     * @param field
     * @param value
     * @return
     */
    public String add(String key, String field, Object value) {
        Map<String, Object> content = new HashMap<>(1);
        content.put(field, value);
        return add(key, content);
    }

    public String add(String key, Map<String, Object> content) {
        return Objects.requireNonNull(redisTemplate.opsForStream().add(key, content)).getValue();
    }

    /**
     * 删除消息，这里的删除仅仅是设置了标志位，不影响消息总长度
     * 消息存储在stream的节点下，删除时仅对消息做删除标记，当一个节点下的所有条目都被标记为删除时，销毁节点
     *
     * @param key 队列
     * @param recordIds 消息id
     * @return
     */
    public Long del(String key, String... recordIds) {
        return redisTemplate.opsForStream().delete(key, recordIds);
    }

    /**
     * 消息长度
     *
     * @param key 队列
     * @return
     */
    public Long len(String key) {
        return redisTemplate.opsForStream().size(key);
    }

    /**
     * 从开始读
     *
     * @param key 队列
     * @return
     */
    public List<MapRecord<String, Object, Object>> read(String key) {
        return redisTemplate.opsForStream().read(StreamOffset.fromStart(key));
    }

    /**
     * 从指定的ID开始读
     *
     * @param key 队列
     * @param recordId 消息id
     * @return
     */
    public List<MapRecord<String, Object, Object>> read(String key, String recordId) {
        return redisTemplate.opsForStream().read(StreamOffset.from(MapRecord.create(key, new HashMap<>(1)).withId(RecordId.of(recordId))));
    }
}
