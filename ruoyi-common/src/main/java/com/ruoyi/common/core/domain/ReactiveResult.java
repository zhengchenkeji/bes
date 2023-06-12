package com.ruoyi.common.core.domain;

/**
 * 操作消息提醒
 * 
 *  *
 */
public class ReactiveResult<T>
{

    /** 消息标识 */
    public String messageId;

    public T data;

    /**
     * 初始化一个新创建的 ReactiveResult 对象，使其表示一个空消息。
     */
    public ReactiveResult()
    {
    }

    public ReactiveResult(String messageId, T data) {
        this.messageId = messageId;
        this.data = data;
    }

    /**
     * 返回成功消息
     *
     * @param data 数据对象
     * @return 成功消息
     */
    public static<T> ReactiveResult<T> result(String messageId, T data)
    {
        return new ReactiveResult<T>(messageId, data);
    }

}
