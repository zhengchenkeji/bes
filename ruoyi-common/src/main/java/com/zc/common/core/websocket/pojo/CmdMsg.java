package com.zc.common.core.websocket.pojo;

/**
 * 指令结果定义
 * @param <T>
 */
public class CmdMsg<T> extends JsonMsg //implements JsonDeserializer, JsonSerializer
{

    /**
     * 命令或者方法名称
     */
    private String method;

    private T params;

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public T getParams()
    {
        return params;
    }

    public void setParams(T params)
    {
        this.params = params;
    }

}
