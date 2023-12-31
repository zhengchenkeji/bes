package com.zc.connect.client.core;

import com.zc.connect.client.bo.ChannelProperty;
import com.zc.connect.client.bo.ReconnectionProperty;
import com.zc.connect.client.constant.AttributeKeyConst;
import com.zc.connect.client.util.DataUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.util.HashedWheelTimer;
import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class SocketClientService
{

    private static final Logger log = LoggerFactory.getLogger(SocketClientService.class);

    /**
     * 定义Map放click链接
     */
    protected static final Map<String, ChannelId> channels = new ConcurrentHashMap<>();

    /**
     * 默认重连时间5分钟
     */
    private static final Integer DEFAULT_RECONNECT_TIME = 5;

    protected static HashedWheelTimer timer = new HashedWheelTimer();// 时间轮的度刻

    /**
     * 保存需要断线重连的信息
     */
    protected static final ConcurrentHashMap<String, ReconnectionProperty> reconnectionGroup = new ConcurrentHashMap<>();

    /**
     * 根据ip和端口号建立连接
     * @param ip ip 地址
     * @param port 端口号
     * @param connectCallback 连接回调
     * @return
     */
    public static void connect(String ip, Integer port, ConnectCallback connectCallback)
    {
        if (ip == null || port == null || connectCallback == null)
        {
            log.error("根据ip和端口号建立连接；参数错误；输出条件： (ip == null || port == null || connectCallback == null)");
            return;
        }

        // 创建连接
        ChannelFuture connect = SocketClient.bootstrap.connect(ip, port);

        // 异步监听器
        connect.addListener((ChannelFutureListener) future ->
        {

            if (future.isDone() && future.isSuccess())
            {
                Channel channel = connect.channel();

                // 当客户端打开链接后，获取客户端的Channel并且添加Channel至ChannelGroup中进行管理
                SocketClient.clients.add(channel);
                channels.put(getKey(ip, port), channel.id());

                ChannelProperty channelProperty = new ChannelProperty();
                channelProperty.setIp(ip);
                channelProperty.setPort(port);

                channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).set(channelProperty);

                connectCallback.callback(true);
                return;
            }

            connectCallback.callback(false);

            // 尝试重新连接
            ReconnectionProperty reconnectionProperty = reconnectionGroup.get(getKey(ip, port));

            if (reconnectionProperty != null)
            {
                newTimeout(reconnectionProperty);
            }

        });
    }

    /**
     * 根据ip和端口号建立连接（断开可重连接）
     * @param ip ip 地址
     * @param port 端口号
     * @param time 断开重新连接时间（分钟）
     * @param connectCallback 连接回调
     */
    public static void connect(String ip,
                                        Integer port,
                                        Integer time,
                                        ConnectCallback connectCallback)
    {
        if (ip == null || port == null  || connectCallback == null)
        {
            log.error("添加重连任务；参数错误；输出条件：" +
                    "(ip == null || port == null || connectCallback == null)");
            return;
        }

        if (time == null || time.equals(0))
        {
            time = DEFAULT_RECONNECT_TIME;
        }

        ReconnectionProperty reconnectionProperty = new ReconnectionProperty();

        reconnectionProperty.setTime(time);

        reconnectionProperty.setTask(timeout -> connect(ip, port, connectCallback));

        reconnectionGroup.put(getKey(ip, port), reconnectionProperty);

        ChannelId channelId = channels.get(getKey(ip, port));

        if (channelId == null)
        {
            connect(ip, port, connectCallback);
        }

    }

    /**
     * 根据ip和端口号删除重连任务
     * @param ip ip 地址
     * @param port 端口号
     * @return
     */
    public static Boolean deleteReconnectionTask(String ip, Integer port)
    {
        ReconnectionProperty reconnectionProperty = reconnectionGroup.remove(getKey(ip, port));

        return reconnectionProperty != null;
    }
    /**
     * 清除重连任务
     */
    public static void clearReconnectionTask()
    {
        reconnectionGroup.clear();
    }

    /**
     * 根据ip port 获取通道
     * @param ip ip地址
     * @param port 端口号
     * @return
     */
    public static Channel getChannel(String ip, Integer port)
    {
        if (ip == null || port == null)
        {
            log.error("根据ip port 获取通道；参数错误；输出条件：(ip == null || port == null)");
            return null;
        }

        ChannelId channelId = channels.get(getKey(ip, port));

        if (channelId == null)
        {
            log.error("根据ip port 获取通道；channelId 获取失败；输出条件：(channelId == null)");
            return null;
        }

        return SocketClient.clients.find(channelId);
    }

    /**
     * 获取全部通道
     * @return
     */
    public static List<Channel> getChannel()
    {
        return new ArrayList<>(SocketClient.clients);
    }

    /**
     * 根据ip和端口号发送消息（十六进制字符串）
     * @param msgHexString 消息（十六进制字符串）
     * @param ip ip地址
     * @param port 端口号
     * @throws DecoderException
     */
    public static void sendMsgHexString(String msgHexString, String ip, Integer port) throws DecoderException
    {
        if (msgHexString == null || ip == null || port == null)
        {
            log.error("根据ip和端口号发送消息（十六进制字符串）：参数不能为空；输出条件：(msgHexString == null || ip == null || port == null)");
            return;
        }

        Channel channel = SocketClientService.getChannel(ip, port);

        ByteBuf byteBuf = DataUtil.convertHexStringToByteBuf(msgHexString);

        if (channel == null)
        {
            log.error("原因：根据ip和端口号获取channel失败；输出条件：(channel == null)");
            return;
        }

        channel.writeAndFlush(byteBuf);

    }

    /**
     * 发送消息（十六进制字符串，群发）
     * @param msgHexString 消息（十六进制字符串）
     * @throws DecoderException
     */
    public static void sendMsgHexString(String msgHexString) throws DecoderException
    {
        if (msgHexString == null)
        {
            log.error("发送消息（十六进制字符串，群发）：参数不能为空；输出条件：(msgHexString == null)");
            return;
        }

        ByteBuf byteBuf = DataUtil.convertHexStringToByteBuf(msgHexString);

        SocketClient.clients.writeAndFlush(byteBuf);

    }

    /**
     * 根据ip和端口号发送消息（字符串）
     * @param msg 消息
     * @param ip ip地址
     * @param port 端口号
     */
    public static void sendMsgString(String msg, String ip, Integer port)
    {
        if (msg == null || ip == null || port == null)
        {
            log.error("根据ip和端口号发送消息（字符串）：参数不能为空；输出条件：(msg == null || ip == null || port == null)");
            return;
        }

        Channel channel = SocketClientService.getChannel(ip, port);

        if (channel == null)
        {
            log.error("原因：根据ip和端口号获取channel失败；输出条件：(channel == null)");
            return;
        }

        channel.writeAndFlush(msg);
    }

    /**
     * 发送消息（字符串，群发）
     * @param msg 消息
     */
    public static void sendMsgString(String msg)
    {
        if (msg == null)
        {
            log.error("发送消息（字符串，群发）：参数不能为空；输出条件：(msg == null)");
            return;
        }

        SocketClient.clients.writeAndFlush(msg);
    }

    protected static String getKey(String ip, Integer port)
    {
        return ip + ":" + port;
    }

    protected static void newTimeout(ReconnectionProperty reconnectionProperty)
    {
        timer.newTimeout(reconnectionProperty.getTask(), reconnectionProperty.getTime(), TimeUnit.MINUTES);
    }

}
