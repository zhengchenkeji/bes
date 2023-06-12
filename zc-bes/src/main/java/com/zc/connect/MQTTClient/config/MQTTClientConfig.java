package com.zc.connect.MQTTClient.config;

import com.zc.connect.config.NettyServerConfigAttr;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Description: mqtt读取配置类
 *
 * @auther: wanghongjie
 * @date: 9:52 2023/4/21
 * @param:
 * @return:
 *
 */
@Configuration
@PropertySource(value = { "classpath:application.yml" })
@ConfigurationProperties(prefix = "mqtt")
public class MQTTClientConfig implements Serializable {

    private static final long serialVersionUID = -1332093842989049470L;
    /**
     * nettyServer配置
     */
    private List<MQTTClientConfigAttr> list = new ArrayList<>();


    public List<MQTTClientConfigAttr> getList() {
        return list;
    }

    public void setList(List<MQTTClientConfigAttr> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "MQTTClientConfigAttr{" +
                "list=" + list +
                '}';
    }
}
