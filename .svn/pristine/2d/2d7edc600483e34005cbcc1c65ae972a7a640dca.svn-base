package com.zc.connect.MQTTClient.config;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 *
 * @Description: mqtt配置属性类
 *
 * @auther: wanghongjie
 * @date: 9:52 2023/4/21
 * @param:
 * @return:
 *
 */
public class MQTTClientConfigAttr implements Serializable {

    private static final long serialVersionUID = 4547948517961811063L;
    /** mqtt网址请求路径 */
    public String url;

    /** 账号 */
    public String username;

    /** 密码*/
    public String password;

    /**客户端id*/
    public String clientId;

    /**消息推送主题*/
    public String[] topic;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String client) {
        this.clientId = client;
    }

    public String[] getTopic() {
        return topic;
    }

    public void setTopic(String[] topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("url", url)
                .append("username", username)
                .append("password", password)
                .append("client", clientId)
                .append("topic", topic)
                .toString();
    }
}
