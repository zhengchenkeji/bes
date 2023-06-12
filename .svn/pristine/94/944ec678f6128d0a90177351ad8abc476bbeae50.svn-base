package com.zc.websocket.enums;

/**
 * @author Athena-xiepufeng
 */
public enum ErrorCodeEnum
{

    // 未知错误
    ERR_UNKNOWN(-10000L, "未知的错误"),

    // 成功
    ERR_OK(0L, "成功"),

    // 密码错误
    ERR_PWD(-10001L, "密码错误"),

    // Channel属性不存在
    ERR_CHANNEL_NO_PROPERTY(-10004L, "Channel 属性不存在"),

    // Channel当前状态不可接收心跳消息
    ERR_HEARTBEAT_AT_INCORRECT_CHANNEL_STATUS(-10005L, "Channel 当前状态不可接收心跳消息"),

    // TokenSN不存在
    ERR_TOKEN_SN(-10006L, "TokenSN 不存在"),

    // TokenSN没有绑定 channel
    ERR_TOKEN_SN_NOT_BIND_CHANNEL(-10007L, "TokenSN 没有绑定 channel");


    private long internalCode; //内部代码
    private String internalMessage; //内部代码含义

    ErrorCodeEnum(Long internalCode, String internalMessage)
    {
        this.internalCode = internalCode;
        this.internalMessage = internalMessage;
    }

    public long getInternalCode()
    {
        return internalCode;
    }

    public void setInternalCode(long internalCode)
    {
        this.internalCode = internalCode;
    }

    public String getInternalMessage()
    {
        return internalMessage;
    }

    public void setInternalMessage(String internalMessage)
    {
        this.internalMessage = internalMessage;
    }

}
