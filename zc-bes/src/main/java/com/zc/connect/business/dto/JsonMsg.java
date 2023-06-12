package com.zc.connect.business.dto;

/**
 *  json 消息体定义
 * @author xiepufeng
 */
public class JsonMsg<T>
{
    // ip 地址
    private String ip;

    private String uuid;

    private T data;

    // 命令标识
    private Integer index;

    //端口
    private Integer post;

    //寄存器起始地址
    private Integer registerBeginAddress;

    //寄存器数量
    private Integer registerLength;

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public Integer getIndex()
    {
        return index;
    }

    public void setIndex(Integer index)
    {
        this.index = index;
    }

    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public Integer getRegisterBeginAddress() {
        return registerBeginAddress;
    }

    public void setRegisterBeginAddress(Integer registerBeginAddress) {
        this.registerBeginAddress = registerBeginAddress;
    }

    public Integer getRegisterLength() {
        return registerLength;
    }

    public void setRegisterLength(Integer registerLength) {
        this.registerLength = registerLength;
    }
}
