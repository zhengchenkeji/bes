package com.zc.websocket.dto.result;

/**
 * 抽象消息结果定义
 */
public abstract class AbstractResult
{
    /*
     * 消息内容
     */
    private String message;

    /*
     * 命令执行状态码 0 表示成功， <0 表示失败
     */
    private Long code;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }
}
