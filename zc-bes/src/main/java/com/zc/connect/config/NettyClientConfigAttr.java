package com.zc.connect.config;

import java.io.Serializable;

/**
 * @auther YangChao
 * @description netty配置属性类
 * @date 2021/11/18/018
 * @apiNote
 */
public class NettyClientConfigAttr implements Serializable {

    /** ip */
    public String ip;

    /** 端口号 */
    public int port;

    /** 指令 */
    public String order;

    /** handler处理类 */
    public String handler_name;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getHandler_name() {
        return handler_name;
    }

    public void setHandler_name(String handler_name) {
        this.handler_name = handler_name;
    }

    @Override
    public String toString() {
        return "NettyClientConfigAttr{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", order=" + order +
                ", handler_name='" + handler_name + '\'' +
                '}';
    }
}
