package com.zc.connect.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Socket 客户端
 * @author xiepufeng
 */
@Component
public class SocketClient
{
    private static final Logger log = LoggerFactory.getLogger(SocketClient.class);

    /**
     * 用于记录和管理所有客户端的Channel
     */
    protected static final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    protected static final Bootstrap bootstrap = new Bootstrap();

    protected static final NioEventLoopGroup group = new NioEventLoopGroup();

    @PostConstruct
    public void run()
    {
        //设置线程池
        bootstrap
                .group(group)
                .channel(NioSocketChannel.class) //设置socket工厂
                .handler(new SocketClientChannelInitializer()); //设置管道

    }

    /**
     * 退出后释放连接（防止服务器重启造成的channel脏数据）
     */
    @PreDestroy
    private void destroy()
    {
        log.info("释放设备连接!");
        //清空连接缓存
        clients.clear();
        //释放线程资源
        group.shutdownGracefully();
        log.info("释放设备连接");
    }


}
