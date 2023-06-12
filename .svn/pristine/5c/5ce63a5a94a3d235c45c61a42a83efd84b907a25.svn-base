package com.zc.connect.nettyServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * @CkassName: Server
 * @author Athena-YangChao
 * @Date: 2021/11/16
 * @Descruotuib:
 * @Version: 1.0
 **/
@Component
public class NettyServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    /** 端口 */
    private int port;
    /** 处理handler */
    private Object handlerClass;

    public NettyServer() {

    }

    public NettyServer(int port,Object handlerClass) {
        this.port = port;
        this.handlerClass = handlerClass;
    }

    // NioEventLoopGroup可以在构造方法中传入需要启动的线程数，默认的情况下他会在采用计算机核心数2的方式去启动线程数量。
    /**
     * parentGroup 主要用于接收请求链接
     */
    private final EventLoopGroup parentGroup = new NioEventLoopGroup();

    /**
     * childGroup处理收发数据
     */
    private final EventLoopGroup childGroup = new NioEventLoopGroup();

    public void start() {
        try {
            // ServerBootstrap 服务端用于接收客户端的连接并为接收连接的用户创建Channel通道
            ServerBootstrap sb = new ServerBootstrap();
            // 用于处理事件循环组的方法
            sb.group(parentGroup, childGroup)
                    // 通过反射方式创建通信通道的方法
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 设置自己的管道服务，接收信息处理
                    .childHandler((ChannelHandler) handlerClass)
                    // 是一个选项配置类，可以增加一些配置参数
                    //服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝
                    .option(ChannelOption.SO_BACKLOG, 1000)
                    //Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。
                    // 可以将此功能视为TCP的心跳机制，需要注意的是：默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //绑定端口，开始接收进来的连接
            //绑定服务器，等待绑定完成，调用sync()的原因是当前线程阻塞
            ChannelFuture future = sb.bind(port).sync();
            log.info("nettyServer启动成功==>端口号:" + port);
            //关闭channel和块，直到它被关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            //关闭EventLoopGroup，释放所有资源（包括所有创建的线程）
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

}
