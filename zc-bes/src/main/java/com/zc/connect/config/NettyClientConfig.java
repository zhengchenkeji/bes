package com.zc.connect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description netty读取配置类
 * @auther YangChao
 * @date 2021/11/17/017
 * @apiNote
 */
@Configuration
@PropertySource("classpath:application-nettyClient.properties")
@ConfigurationProperties(prefix = "netty.client")
public class NettyClientConfig implements Serializable {

    /**
     *
     *  nettyClient 起始类
     */
    private String order_combination;

    /**
     * nettyClient配置
     */
    private List<NettyClientConfigAttr> config = new ArrayList<>();


    public String getOrder_combination() {
        return order_combination;
    }

    public void setOrder_combination(String order_combination) {
        this.order_combination = order_combination;
    }

    public List<NettyClientConfigAttr> getConfig() {
        return config;
    }

    public void setConfig(List<NettyClientConfigAttr> config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "NettyClientConfig{" +
                "order_combination='" + order_combination + '\'' +
                ", config=" + config +
                '}';
    }
}
