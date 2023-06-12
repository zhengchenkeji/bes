package com.zc.connect.client.core;

@FunctionalInterface
public interface ConnectCallback
{
    /**
     * 连接回调
     * @param result true 连接成功； false 连接失败
     */
    void callback(boolean result);
}
