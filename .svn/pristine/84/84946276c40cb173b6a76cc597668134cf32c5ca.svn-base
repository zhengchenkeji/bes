package com.zc.websocket.core;

import com.zc.websocket.bo.ChannelProperty;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.zc.websocket.constant.AttributeKeyConst;
import com.zc.websocket.enums.ChannelStatus;
import com.zc.websocket.handler.ParseDiscern;
import com.zc.websocket.handler.WebsocketEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import com.zc.websocket.util.MsgUtil;

import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * 消息通道处理器
 * @author Athena-xiepufeng
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>
{
    private static final Logger log = Logger.getLogger(TextWebSocketFrameHandler.class.getName());

    private ChannelHandlerContext ctx;

    /**
     *  读取客户端的请求，返回客户响应
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg)
    {

        Channel channel = ctx.channel();

        /* Begin. JsonParser 的用法:适用于系统间的消息交互 。 注意与下面JsonReader的区别。*/
        JsonObject jsonObject;

        try
        {
            JsonElement jsonElement = JsonParser.parseString(msg.text());
            jsonObject = jsonElement.getAsJsonObject();
        }
        catch (JsonSyntaxException | IllegalStateException e)
        {
            e.printStackTrace();
            channel.close();
            return;
        }
        // 判断消息类型，共三中类型，即：method, result, error
        String rpcType = MsgUtil.getJsonrpcType(jsonObject);
        if (null == rpcType)
        {
            channel.close();
            return;
        }

        ParseDiscern parseDiscern = new ParseDiscern(this);

        parseDiscern.parseAndProcessByJsonRpcType(jsonObject, rpcType);

    }

    /**
     * 通道被添加回调（首先调用）
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
    {
        this.ctx = ctx;
        Channel channel = ctx.channel();
        System.out.println("Client: websocket" + channel.remoteAddress() + " entering");
        MsgUtil.channels.add(channel);
    }

    /**
     * 连接关掉的时候回调
     * @param ctx
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.ctx = null;
        Channel channel = ctx.channel();

        ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();

        String tokenSN = channelProperty.getTokenSN();

        // 判断 tokenSN 是否存在
        ServiceLoader<WebsocketEvent> load = ServiceLoader.load(WebsocketEvent.class);

        if (!load.iterator().hasNext())
        {
            log.warning("WebsocketEvent 没有实现类");
        }

        load.forEach(item ->  item.channelRemovedEvent(tokenSN));

        System.out.println("Client: websocket" + channel.remoteAddress() + " leave");
        MsgUtil.channels.remove(channel);
    }

    /**
     * 通过变成活动状态时回调
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        // 设置 channel 属性
        Channel channel = ctx.channel();

        ChannelProperty channelProperty = new ChannelProperty();
        channelProperty.setStatus(ChannelStatus.INITIAL);
        channelProperty.setStatusCount(0);

        channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).set(channelProperty);

        log.info("Channel active. remote Address:" + channel.remoteAddress());

        System.out.println("Client: websocket" + channel.remoteAddress() + " online");

    }

    /**
     * 通道变成不活跃时回调
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx)
    {
        // 清空 channel 属性
        Channel channel = ctx.channel();

        log.info("Channel deactived. remote Address:" + channel.remoteAddress());

        System.out.println("Client: websocket" + channel.remoteAddress() + " offline");

    }

    /**
     * 异常发生时调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        Channel channel = ctx.channel();
        System.out.println("Client: websocket" + channel.remoteAddress() + " exception");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }


    public ChannelHandlerContext getCtx()
    {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx)
    {
        this.ctx = ctx;
    }

}
