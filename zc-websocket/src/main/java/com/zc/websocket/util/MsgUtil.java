package com.zc.websocket.util;

import com.zc.websocket.bo.ChannelProperty;
import com.google.gson.JsonObject;
import com.zc.websocket.constant.AttributeKeyConst;
import com.zc.websocket.constant.RequestObject;
import com.zc.websocket.handler.WebsocketEvent;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * @author Athena-xiepufeng
 */
public class MsgUtil {

    private static final Logger log = Logger.getLogger(MsgUtil.class.getName());

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static WebsocketEvent websocketEvent;

    /**
     * 获取消息的类型
     *
     * @param jsonObject
     * @return
     */
    public static String getJsonrpcType(JsonObject jsonObject)
    {

        if (null == jsonObject)
        {
            return null;
        }

        if (jsonObject.has(RequestObject.METHOD))
        {
            return RequestObject.METHOD;
        }

        if (jsonObject.has(RequestObject.PARAMS))
        {
            return RequestObject.PARAMS;
        }

        return null;

    }

    /**
     * 根据 tokenSN 获取其绑定的 channel
     *
     * @param tokenSN
     * @return
     */
    public static Channel getChannelByTokenSN(String tokenSN)
    {
        if (tokenSN == null || tokenSN.trim().isEmpty())
        {
            log.warning("参数错误: tokenSN 不存在");
            return null;
        }


        Channel channelTarget = null;

        for (Channel channel : channels)
        {
            ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();
            if (channelProperty == null || !tokenSN.equalsIgnoreCase(channelProperty.getTokenSN()))
            {
                continue;
            }

            channelTarget = channel;
            break;
        }

        if (channelTarget == null)
        {
            log.warning("Channel 绑定的 tokenSN:" + tokenSN + " 不存在");
            return null;
        }

        return channelTarget;
    }

    /**
     * 判断 http tokenSN 是否存在
     *
     * @param tokenSN
     * @return
     */
    public static String decryptToken(String tokenSN)
    {
        if (tokenSN == null || tokenSN.isEmpty())
        {
            log.warning("参数错误。 tokenSN 不存在l。");
            return null;
        }

        if (null != websocketEvent)
        {
            return websocketEvent.decryptToken(tokenSN);
        }

        // 判断 tokenSN 是否存在
        ServiceLoader<WebsocketEvent> load = ServiceLoader.load(WebsocketEvent.class);

        if (!load.iterator().hasNext())
        {
            log.warning("WebsocketEvent没有实现类");
            return null;
        }

        for (WebsocketEvent item : load)
        {
            websocketEvent = item;
            return item.decryptToken(tokenSN);
        }

        return null;
    }

    /**
     * 判断 http tokenSN 是否存在
     *
     * @param tokenSN
     * @return
     */
    public static boolean isTokenSNExist(String tokenSN)
    {
        if (tokenSN == null || tokenSN.isEmpty())
        {
            log.warning("参数错误。 tokenSN 不存在l。");
            return false;
        }

        if (null != websocketEvent)
        {
            return websocketEvent.isTokenSNExist(tokenSN);
        }

        // 判断 tokenSN 是否存在
        ServiceLoader<WebsocketEvent> load = ServiceLoader.load(WebsocketEvent.class);

        if (!load.iterator().hasNext())
        {
            log.warning("WebsocketEvent 没有实现类");
            return false;
        }

        for (WebsocketEvent item : load)
        {
            websocketEvent = item;
            return item.isTokenSNExist(tokenSN);
        }

        return false;
    }

    /**
     * 心跳事件
     *
     * @param tokenSN
     */
    public static void heartbeatEvent(String tokenSN)
    {
        if (tokenSN == null || tokenSN.isEmpty())
        {
            log.warning("参数错误。 tokenSN 不存在l。");
            return;
        }

        if (null != websocketEvent)
        {
            websocketEvent.heartbeatEvent(tokenSN);
            return;
        }

        ServiceLoader<WebsocketEvent> load = ServiceLoader.load(WebsocketEvent.class);

        if (!load.iterator().hasNext())
        {
            log.warning("WebsocketEvent 没有实现类");
            return;
        }

        for (WebsocketEvent item : load)
        {
            websocketEvent = item;
            item.heartbeatEvent(tokenSN);
            return;
        }

    }

}
