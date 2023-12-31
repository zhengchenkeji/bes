package com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler;

import com.zc.common.core.ThreadPool.ThreadPool;
import com.zc.connect.business.handler.ModbusMsgReceive;
import com.zc.connect.business.handler.ReceiptMsgHandler;
import com.zc.connect.client.bo.ChannelProperty;
import com.zc.connect.client.constant.AttributeKeyConst;
import com.zc.connect.nettyClient.ClientHandler;
import com.zc.connect.nettyClient.NettyClient;
import com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler.ParseDiscern;
import com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler.ServerHandler_Energy;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:52 2023/2/17
 * @Modified By:
 */
public class ServerHandler_modbus extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerHandler_Energy.class);

    private ChannelHandlerContext ctx;


    private ParseDiscern parseDiscern;

    private AtomicInteger readIdleTimes = new AtomicInteger(0);
    /**
     * 定义Map放click链接
     */
    public static Map<String, ChannelId> channels = new ConcurrentHashMap<>();

    /**
     * 用于记录和管理所有客户端的Channel
     */
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //记录客户端端口
    public static Integer post = null;


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

                ByteBuf byteBuf = (ByteBuf) msg;

                byteBuf.retain();
            } catch (Exception e) {
                log.error("解析协议时出错",e);
                e.printStackTrace();
            } finally {
                ByteBuf byteBuf = (ByteBuf) msg;
                byteBuf.release();
                boolean result = ReferenceCountUtil.release(msg);
//                log.info("释放" + result);
            }
        });

//        if (null != messageHandler)
//        {
//            messageHandler.modbusMessageReceived(ctx, msg);
//            return;
//        }


    }

    /**
     * 通道被添加回调（首先调用）
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception
    {
//        this.ctx = ctx;
//        Channel channel = ctx.channel();
//        System.out.println("Client:" + channel.remoteAddress() + " entering");
//        channels.add(channel);
    }

    /**
     * 通道变成活动状态时回调
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {


        String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        int post = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();

        ServerHandler_modbus.post = post;

        String ip = host;
        ctx.channel().attr(AttributeKey.valueOf("channel.property.ip_port")).set(ip);
        ctx.channel().attr(AttributeKey.valueOf("channel.property.handler_name")).set("com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler.ModbusServerHandlers");
        Channel channel = ctx.channel();

        // 当客户端打开链接后，获取客户端的Channel并且添加Channel至ChannelGroup中进行管理

        ChannelProperty channelProperty = new ChannelProperty();
        channelProperty.setIp(host);
        channelProperty.setPort(post);

        channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).set(channelProperty);

        channels.put(ip + ":" + post, ctx.channel().id());
        clients.add(ctx.channel());

        ModbusMsgReceive clientMsgReceive =  ClientHandler.modbusMsgReceive;
        clientMsgReceive.controllerState(host,post,true);

        log.info("初步连接");

    }

    /**
     * 通道变成不活动状态时回调
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        int post = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();

        ModbusMsgReceive clientMsgReceive =  ClientHandler.modbusMsgReceive;
        clientMsgReceive.controllerState(host,post,false);
        //逻辑处理
        ctx.channel().close();

        System.out.println("Client:" + host + ":" + post  + " offline");
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

        if (!channels.isEmpty())
        {
            channels.remove(channelProperty.getIp() + ":" + channelProperty.getPort());
        }

       clients.remove(channel);

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


    public ChannelHandlerContext getCtx()
    {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx)
    {
        this.ctx = ctx;
    }

    public ParseDiscern getParseDiscern()
    {
        return parseDiscern;
    }

    public void setParseDiscern(ParseDiscern parseDiscern)
    {
        this.parseDiscern = parseDiscern;
    }
}
