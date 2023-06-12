package com.zc.connect.nettyServer;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.zc.common.core.ThreadPool.ThreadPool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @CkassName: ServerHandler
 * @author Athena-YangChao
 * @Date: 2021/11/16
 * @Version: 1.0
 **/
@Component
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    /**
     * 用来临时保留没有处理过的请求报文
     */
    ByteBuf tempMsg = Unpooled.buffer();

    /**
     * Redis缓存工具类
     * */
    private RedisCache redisCache = SpringUtils.getBean(RedisCache.class);


    /**
     * 用于记录和管理所有客户端的Channel
     * */
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 超时处理
     * 如果5秒没有接受客户端的心跳，就触发;
     * 如果超过两次，则直接关闭;
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            //如果读通道处于空闲状态，说明没有接收到心跳命令
            if (IdleState.READER_IDLE.equals(event.state())) {
                log.info("已经5秒没有接收到客户端的信息了");
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, obj);
        }
    }

    /**
     * 连接断开执行方法
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().close();
        nettyCleanSend("");
        super.channelInactive(ctx);
    }


    /**
     * 连接被建立并准备进行通信时被调用--初次连接调用
     * */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        //log.info("连接初步建立");
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发handlerRemoved,ChannelGroup会自动移除客户端的Channel
        log.info("客户端断开, Channel对应的长ID：" + ctx.channel().id().asLongText());
        log.info("客户端断开, Channel对应的短ID：" + ctx.channel().id().asShortText());
    }

    /**
     * 读操作时捕获到异常时调用
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 每个信息入站都会调用
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf buf = (ByteBuf) msg;
            String data = ByteBufUtil.hexDump(buf).toUpperCase();
            if (StringUtils.isNull(data)) {
                ReferenceCountUtil.release(msg);
            } else {
                clients.add(ctx.channel());
                // 数据处理
                dataProcess(ctx, msg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
        ctx.flush();
    }

    /**
     * @Description: 数据处理
     * @author Athena-YangChao
     * @date 2019/11/18 15:47
     */
    private void dataProcess(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        log.info("--数据处理--");
        log.info("收到了一次数据包，长度是：" + buf.readableBytes());
        // 合并报文
        ByteBuf message = null;
        int tmpMsgSize = tempMsg.readableBytes();
        // 如果暂存有上一次余下的请求报文，则合并
        if (tmpMsgSize > 0) {
            message = Unpooled.buffer();
            message.writeBytes(tempMsg);
            message.writeBytes(buf);
            log.info("合并：上一数据包余下的长度为：" + tmpMsgSize + ",合并后长度为:" + message.readableBytes());
        } else {
            message = buf;
        }
        String data = ByteBufUtil.hexDump(message).toUpperCase();
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = ipSocket.getAddress().getHostAddress();
    }



    /**
     * 离线心跳检测不到数据 修改缓存状态值
     */
    public void nettyCleanSend(String DeviceId) {
        ThreadPool.executor.execute(() -> {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info(Thread.currentThread().getName() + "线程处理,redis缓存离线状态修改");
        });

    }


}