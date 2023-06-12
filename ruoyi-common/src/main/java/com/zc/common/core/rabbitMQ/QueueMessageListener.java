package com.zc.common.core.rabbitMQ;

import com.zc.common.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 10:32 2023/2/22
 * @Modified By:
 */
@Component
public class QueueMessageListener {

    final Logger logger = LoggerFactory.getLogger(getClass());


    @RabbitListener(queuesToDeclare = @Queue(RabbitConfig.RABBITMQ_TOPIC))
    public void onRegistrationMessageFromMailQueue(String message) throws Exception {
        logger.info("queue {} received registration message: {}", RabbitConfig.RABBITMQ_TOPIC, message);
    }

}
