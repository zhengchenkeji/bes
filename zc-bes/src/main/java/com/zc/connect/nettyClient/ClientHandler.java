package com.zc.connect.nettyClient;

import com.zc.common.core.ThreadPool.ThreadPool;
import com.zc.connect.business.handler.ClientMsgReceive;
import com.zc.connect.business.handler.ModbusMsgReceive;
import com.zc.connect.business.handler.ReceiptMsgHandler;
import com.zc.connect.client.bo.ChannelProperty;
import com.zc.connect.client.constant.AttributeKeyConst;
import com.zc.connect.nettyClient.service.NettyClientService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Athena-YangChao
 * @program: netty
 * @description 客户端消息处理
 * @create: 2021-11-16
 **/
@Component
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    public static ModbusMsgReceive modbusMsgReceive;

    static {

        ServiceLoader<ModbusMsgReceive> modbusLoad = ServiceLoader.load(ModbusMsgReceive.class);

        for (ModbusMsgReceive item : modbusLoad)
        {
            modbusMsgReceive = item;
            break;
        }
    }

    private AtomicInteger readIdleTimes = new AtomicInteger(0);
    /**
     * 客户端接收消息处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //计数清零
        readIdleTimes = new AtomicInteger(0);
        String handlerClass = (String) ctx.channel().attr(AttributeKey.valueOf("channel.property.handler_name")).get();
        // 利用线程池处理协议解析 不占用channelRead
        ThreadPool.executor.execute(() -> {
            try {
                Object order_combination_class = Class.forName(handlerClass).newInstance();
                Class clazz = Class.forName(handlerClass);
                Method method1 = clazz.getMethod("response_analysis", ChannelHandlerContext.class ,Object.class);
                method1.invoke(order_combination_class, ctx,msg);

//                service.ackSyncMsg(msg); // 同步消息返回

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 连接被建立并准备进行通信时被调用--初次连接调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        int post = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
        String ip_port = host + ":" + post;
        ctx.channel().attr(AttributeKey.valueOf("channel.property.ip_port")).set(ip_port);
        ctx.channel().attr(AttributeKey.valueOf("channel.property.handler_name")).set("com.zc.connect.nettyClient.ClientHandlers.ModbusClientHandlers");


        Channel channel = ctx.channel();

        // 当客户端打开链接后，获取客户端的Channel并且添加Channel至ChannelGroup中进行管理

        ChannelProperty channelProperty = new ChannelProperty();
        channelProperty.setIp(host);
        channelProperty.setPort(post);

        channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).set(channelProperty);

        NettyClient.channels.put(ip_port, ctx.channel().id());
        NettyClient.clients.add(ctx.channel());

        ModbusMsgReceive clientMsgReceive =  ClientHandler.modbusMsgReceive;
        clientMsgReceive.controllerState(host,post,true);
        log.info("初步连接");
    }

    /**
     * channel不活跃
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        try {
            String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
            int post = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();

            ModbusMsgReceive clientMsgReceive =  ClientHandler.modbusMsgReceive;
            clientMsgReceive.controllerState(host,post,false);
            //逻辑处理
            ctx.channel().close();
        } catch (Exception e) {
            //逻辑处理
            ctx.channel().close();
        }


    }

    /**
     * 连接断开触发事件
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发handlerRemoved,ChannelGroup会自动移除客户端的Channel
//        Channel channel = ctx.channel();
//        System.out.println("Client:" + channel.remoteAddress() + " leave");
//        NettyClient.channels.remove(channel);


        Channel channel = ctx.channel();

        ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();

        if (channelProperty == null)
        {
//            log.warn("原因：channelProperty 不存在; 输出条件：(channelProperty == null)；远程地址："
//                    + channel.remoteAddress());
            return;
        }

        if (!NettyClient.channels.isEmpty())
        {
            NettyClient.channels.remove(channelProperty.getIp() + ":" + channelProperty.getPort());
        }

        NettyClient.clients.remove(channel);

//        try {
//            String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
//            int post = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
//            String ip_port = host + ":" + post;
//            Set keySet = NettyClient.channels.keySet();
//            for(Object keyName : keySet){
//                if (ip_port.equals(keyName)) {
//                    NettyClient.channels.remove(ip_port);
//                }
//            }
//        } catch (Exception e) {
//
//        }

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
        Channel channel = ctx.channel();
        System.out.println("Client:" + channel.remoteAddress() + " exception");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        //ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        int post = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();

        String eventType = null;

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                eventType = "读空闲";
                readIdleTimes.incrementAndGet(); // 读空闲的计数加1

                System.out.println("第:" +readIdleTimes.get() + "次长期没收到服务器推送数据");
                //可以选择重新连接
//                NettyClient.connect(host,post);

            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                eventType = "写空闲";
//                System.out.println("长期未向服务器发送数据");
                //发送心跳包

            }
            if (readIdleTimes.get() > 3) {
//                System.out.println("[server]读空闲超过3次，关闭连接，释放更多资源");
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
