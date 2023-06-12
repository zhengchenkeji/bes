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
@PropertySource("classpath:application-nettyServer.properties")
@ConfigurationProperties(prefix = "netty.server")
public class NettyServerConfig implements Serializable {
    private static final long serialVersionUID = 7519565773190844250L;

    /**
     * nettyServer配置
     */
    private List<NettyServerConfigAttr> config = new ArrayList<>();

    public List<NettyServerConfigAttr> getConfig() {
        return config;
    }

    public void setConfig(List<NettyServerConfigAttr> list) {
        this.config = list;
    }

    @Override
    public String toString() {
        return "NettyConfig{" +
                "config=" + config +
                '}';
    }
}
