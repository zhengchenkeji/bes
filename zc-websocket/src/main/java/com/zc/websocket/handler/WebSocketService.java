package com.zc.websocket.handler;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import com.zc.websocket.util.MsgUtil;

import java.util.logging.Logger;

/**
 * 给客户端发送消息
 * @author Athena-xiepufeng
 */
public class WebSocketService
{
    private static final Logger log = Logger.getLogger(WebSocketService.class.getName());

    /**
     * 给指定客户端发送消息
     * @param subscriber
     * @param content
     * @return
     */
    public static boolean postEvent(String subscriber, String content)
    {
        if (subscriber == null || subscriber.isEmpty())
        {
            return false;
        }

        Channel channel = MsgUtil.getChannelByTokenSN(subscriber);

        if (channel == null)
        {
            log.warning("发布事件失败, 因为事件订阅者 channel:" + subscriber + " 不存在");
            return false;
        }

        channel.writeAndFlush(new TextWebSocketFrame(content));

        return true;

    }

    /**
     * 群发消息
     * @param content
     * @return
     */
    public static boolean postEvent(String content)
    {
        if (content == null || content.isEmpty())
        {
            return false;
        }

        MsgUtil.channels.writeAndFlush(new TextWebSocketFrame(content));

        return true;

    }

}
