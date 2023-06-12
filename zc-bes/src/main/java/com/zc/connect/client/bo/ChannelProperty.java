package com.zc.connect.client.bo;

/**
 * Channel对应的属性数据定义
 *
 * @author xiepufeng
 *
 */
public class ChannelProperty
{
    /*
     * ip 地址
     */
    private String ip;

    /*
     * 端口
     */
    private Integer port;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
