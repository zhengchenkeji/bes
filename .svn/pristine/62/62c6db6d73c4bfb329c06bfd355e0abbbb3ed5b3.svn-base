package com.zc.connect.client.core;


import com.ruoyi.common.utils.spring.SpringUtils;
import com.zc.connect.client.bo.ChannelProperty;
import com.zc.connect.client.bo.ReconnectionProperty;
import com.zc.connect.client.constant.AttributeKeyConst;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ServiceLoader;

import static com.zc.connect.client.core.SocketClientService.getKey;

/**
 * netty socket 消息处理回调类
 * @author xiepufeng
 */
public class SocketClientHandler extends SimpleChannelInboundHandler<String>
{
    private static final Logger log = LoggerFactory.getLogger(SocketClientHandler.class);

    private static DataReceiveCallback dataReceiveCallback;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringUtils.getBean(ThreadPoolTaskExecutor.class);

    static
    {
        ServiceLoader.load(DataReceiveCallback.class).forEach(item -> dataReceiveCallback = item);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)
    {
        if (dataReceiveCallback == null)
        {
            log.error("原因：DataReceiveCallback 没有实例化; 输出条件：(dataReceiveCallback == null)；远程地址："
                    + ctx.channel().remoteAddress());
            return;
        }

        Channel channel = ctx.channel();

        ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();

        // 分配线程执行
        threadPoolTaskExecutor.execute(() ->
        {
            dataReceiveCallback.receiveData(msg, channelProperty.getIp(), channelProperty.getPort());
        });

    }

    /**
     * 通道被添加回调（首先调用）
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception
    {
        log.info("通道被添加；远程地址："
                + ctx.channel().remoteAddress());
    }

    /**
     * 连接关掉的时候回调
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
    {

        Channel channel = ctx.channel();

        ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();

        if (channelProperty == null)
        {
            log.warn("原因：channelProperty 不存在; 输出条件：(channelProperty == null)；远程地址："
                    + channel.remoteAddress());
            return;
        }

        if (!SocketClientService.channels.isEmpty())
        {
            SocketClientService.channels.remove(getKey(channelProperty.getIp(), channelProperty.getPort()));
        }

        SocketClient.clients.remove(channel);

        if (dataReceiveCallback == null)
        {
            log.error("原因：DataReceiveCallback 没有实例化; 输出条件：(dataReceiveCallback == null)；远程地址："
                    + channel.remoteAddress());
            return;
        }

        // 分配线程执行
        threadPoolTaskExecutor.execute(() ->
        {
            // 回调通知在线状态为离线
            dataReceiveCallback.onlineStateInfo(false, channelProperty.getIp(), channelProperty.getPort());


            ReconnectionProperty reconnectionProperty = SocketClientService.reconnectionGroup.get(getKey(channelProperty.getIp(), channelProperty.getPort()));

            if (reconnectionProperty != null)
            {
                SocketClientService.newTimeout(reconnectionProperty);
            }
        });

    }

    /**
     * 通道变成活动状态时回调
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        Channel channel = ctx.channel();
        log.info("通道变成活动状；远程地址：" + channel.remoteAddress());
        ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();

        if (channelProperty == null)
        {
            log.warn("输出条件：(channelProperty == null)； 远程地址："
                    + channel.remoteAddress());
            return;
        }

        if (dataReceiveCallback == null)
        {
            log.error("原因：DataReceiveCallback 没有实例化; 输出条件：(dataReceiveCallback == null)；远程地址："
                    + channel.remoteAddress());
            return;
        }

        // 分配线程执行
        threadPoolTaskExecutor.execute(() ->
        {
            // 回调通知在线状态为在线
            dataReceiveCallback.onlineStateInfo(true, channelProperty.getIp(), channelProperty.getPort());
        });
    }

    /**
     * 通道变成不活动状态时回调
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        log.warn("通道变成不活动状；远程地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 异常发生时调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        log.error("netty 客户端异常回调； 远程地址：" + ctx.channel().remoteAddress() + "异常信息：" + cause.getMessage());
    }

}
