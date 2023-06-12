package com.zc.connect.client.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 初始化器
 * @author xiepufeng
 */
public class SocketClientChannelInitializer extends ChannelInitializer<SocketChannel>
{
    @Override
    protected void initChannel(SocketChannel ch)
    {
        ch.pipeline()
                .addLast(new StringDecoder(CharsetUtil.UTF_8)) // 字符串解码器
                .addLast(new StringEncoder(CharsetUtil.UTF_8)) // 字符串编码器
                .addLast(new SocketClientHandler()); // 业务处理器
    }
}
