package com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @auther YangChao
 * @description
 * @date 2021/11/18/018
 * @apiNote
 */
public class ChildChannelHandler_Energy extends ChannelInitializer<SocketChannel> {
    private static final Logger log = LoggerFactory.getLogger(ChildChannelHandler_Energy.class);

    /**
     * 用于记录和管理所有客户端的Channel
     */
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        log.info("能源handler");
        //检测连接有效性（心跳）,此处功能：5秒内read()未被调用则触发一次useEventTrigger()方法
        ch.pipeline().addLast(new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS));
        // 解码器 LineBasedFrameDecoder，使用换行符\n或者\r\n作为依据，遇到\n或者\r\n都认为是一条完整的消息。
        ch.pipeline().addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));

        // 编码器LineBasedFrameEncoder，自动对每条消息末端添加换行符
        ch.pipeline().addLast(new LineBasedFrameEncoder("\n"));
        // 解码器 StringDecoder，将字符串自动转成utf8编码
        ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
        // 编码器 StringEncoder，将字符串自动转成utf8编码
        ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
        //ServerHandler实现了业务逻辑
        ch.pipeline().addLast(new ServerHandler_Energy());
    }

    /**
     * 自定义编码器
     *
     * @author xiepufeng
     */
    public class LineBasedFrameEncoder extends MessageToByteEncoder<ByteBuf> {

        // 分隔符
        private String separator;

        public LineBasedFrameEncoder(String separator) {
            if (null == separator || separator.isEmpty()) {
                separator = "\n";
            }

            this.separator = separator;
        }

        public LineBasedFrameEncoder(Class<? extends ByteBuf> outboundMessageType, String separator) {
            super(outboundMessageType);
            this.separator = separator;
        }


        @Override
        protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
            msg.writeBytes(Unpooled.copiedBuffer(separator, Charset.forName("utf-8")));
            out.writeBytes(msg);
        }
    }

//    private static final Logger logger = LoggerFactory.getLogger(LineBasedFrameDecoder.class);


   /* public class LineBasedFrameDecoder extends ByteToMessageDecoder {


        static final int PACKET_SIZE = Integer.MAX_VALUE;

        // 用来临时保留没有处理过的请求报文
        ByteBuf tempMsg = Unpooled.buffer();


        public LineBasedFrameDecoder(int maxValue) {
        }


        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
            System.out.println(Thread.currentThread() + "收到了一次数据包，长度是：" + in.readableBytes());

            // 合并报文
            ByteBuf message = null;
            int tmpMsgSize = tempMsg.readableBytes();
            // 如果暂存有上一次余下的请求报文，则合并
            if (tmpMsgSize > 0) {
                message = Unpooled.buffer();
                message.writeBytes(tempMsg);
                message.writeBytes(in);
                System.out.println("合并：上一数据包余下的长度为：" + tmpMsgSize + ",合并后长度为:" + message.readableBytes());
            } else {
                message = in;
            }

            int size = message.readableBytes();
            int counter = size / PACKET_SIZE;
//            for (int i = 0; i < counter; i++) {
//                byte[] request = new byte[PACKET_SIZE];
//                // 每次从总的消息中读取220个字节的数据
//                message.readBytes(request);
//
//                // 将拆分后的结果放入out列表中，交由后面的业务逻辑去处理
//                out.add(Unpooled.copiedBuffer(request));
//            }
            byte[] request = new byte[size];
            message.readBytes(request);
            out.add(Unpooled.copiedBuffer(request));

            // 多余的报文存起来
            // 第一个报文： i+  暂存
            // 第二个报文： 1 与第一次
//            size = message.readableBytes();
//            if (size != 0) {
//                System.out.println("多余的数据长度：" + size);
//                // 剩下来的数据放到tempMsg暂存
//                tempMsg.clear();
//                tempMsg.writeBytes(message.readBytes(size));
//            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            System.err.println("--------数据读异常----------: ");
            cause.printStackTrace();
            ctx.close();
        }
    }*/

}
