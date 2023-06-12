package com.zc.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:16 2023/2/22
 * @Modified By:
 */
@Configuration
public class DirectRabbitConfig implements BeanPostProcessor {

    @Resource
    private RabbitAdmin rabbitAdmin;

    @Bean
    public Queue rabbitmqDirectQueue() {
        /**
         * 1、name:    队列名称
         * 2、durable: 是否持久化
         * 3、exclusive: 是否独享、排外的。如果设置为true，定义为排他队列。则只有创建者可以使用此队列。也就是private私有的。
         * 4、autoDelete: 是否自动删除。也就是临时队列。当最后一个消费者断开连接后，会自动删除。
         * */
        return new Queue(RabbitConfig.RABBITMQ_TOPIC, true, false, false);
    }

    @Bean
    public DirectExchange rabbitmqDirectExchange() {
        //Direct交换机
        return new DirectExchange(RabbitConfig.RABBITMQ_DIRECT_EXCHANGE, true, false);
    }
    /**
     * /绑定交换机和队列，并设置匹配键
     *
     * @return
     */
    @Bean
    public Binding bindDirect() {
        return BindingBuilder
                //绑定队列
                .bind(rabbitmqDirectQueue())
                //到交换机
                .to(rabbitmqDirectExchange())
                //设置匹配键
                .with(RabbitConfig.RABBITMQ_DIRECT_ROUTING);
    }

    //初始化rabbitAdmin对象
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 只有设置为 true，spring 才会加载 RabbitAdmin 这个类
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    //实例化bean后，在启动生产者时会自动创建交换机和队列，不用等到发送消息才创建
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //创建交换机
        rabbitAdmin.declareExchange(rabbitmqDirectExchange());
        //创建队列
        rabbitAdmin.declareQueue(rabbitmqDirectQueue());
        return bean;
    }

}
