package com.zc.websocket.handler;

/**
 *  Session 外部交互接口
 * @author Athena-xiepufeng
 */
public interface WebsocketEvent
{

    /**
     * 解密tokenSN
     * 注意：tokenSN 需要解密必须要重写该方法
     * @param tokenSN
     * @return
     */
    default String decryptToken(String tokenSN)
    {
        return null;
    }

    /**
     * 判断 TokenSN 是否存在
     * @return
     */
    boolean isTokenSNExist(String tokenSN);

    /**
     *  心跳事件
     * @param tokenSN
     */
    void heartbeatEvent(String tokenSN);

    /**
     * 通道关闭事件
     * @param tokenSN
     */
    void channelRemovedEvent(String tokenSN);

}
