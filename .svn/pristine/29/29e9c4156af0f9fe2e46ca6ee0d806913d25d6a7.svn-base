package com.zc.common.core.rabbitMQ;

import com.ruoyi.common.utils.uuid.UUID;
import com.zc.common.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 10:02 2023/2/22
 * @Modified By:
 */
@Component
public class MessagingService {

    //日期格式化
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    RabbitTemplate rabbitTemplate;


    public void sendMsg(String msg) throws Exception {
        try {
            String msgId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            String sendTime = sdf.format(new Date());
            Map<String, Object> map = new HashMap<>();
            map.put("msgId", msgId);
            map.put("sendTime", sendTime);
            map.put("msg", msg);
//            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_A, RabbitConfig.ROUTINGKEY_A, map);
//            rabbitTemplate.convertAndSend(RabbitConfig.TOPIC_EXCHANGE_DEMO_NAME, "a.a", map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendRegistrationMessage(String msg) {
        rabbitTemplate.convertAndSend(RabbitConfig.RABBITMQ_DIRECT_EXCHANGE, RabbitConfig.RABBITMQ_DIRECT_ROUTING, msg);
    }

    public void sendLoginMessage(String msg) {
        rabbitTemplate.convertAndSend("login", "", msg);
    }
}
