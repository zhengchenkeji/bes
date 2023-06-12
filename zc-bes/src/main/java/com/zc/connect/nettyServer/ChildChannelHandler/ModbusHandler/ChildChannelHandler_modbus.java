package com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler;

import com.zc.connect.business.dto.JsonMsg;
import com.zc.connect.config.CodeConfig;
import com.zc.connect.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wanghongjie
 * @Description:上位机当modbus服务器(从站)时,下位机就是modbus的主站(客户端),客户端定时请求服务端的数据
 * @Date: Created in 15:43 2023/2/17
 * @Modified By:
 */
public class ChildChannelHandler_modbus extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 消息处理器
        // IdleStateHandler的readerIdleTime参数指定超过15秒还没收到客户端的连接，
        // 会触发IdleStateEvent事件并且交给下一个handler处理，下一个handler必须实现userEventTriggered方法处理对应事件
        pipeline.addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast(new CustomDecode());        //自定义解码器
        pipeline.addLast(new ServerHandler_modbus());
    }

    public class CustomDecode extends ByteToMessageDecoder {

        Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

            String host = ((InetSocketAddress) channelHandlerContext.channel().remoteAddress()).getAddress().getHostAddress();
            int post = ((InetSocketAddress) channelHandlerContext.channel().remoteAddress()).getPort();

            int len = in.readableBytes();       //这里得到可读取的字节长度
            in.markReaderIndex();               //包头做标记位，后面可以重新回到数据包头开始读数据
            //有数据时开始读数据包
            if (len > 0) {
                byte[] src = new byte[len];
                in.readBytes(src);          //把数据读到字节数组中(读取完之后指针会到最后一个数据)
                in.resetReaderIndex();      //重置当前指针到标记位(包头),用于重新读取接收的数据，直至接收完完整数据包


                if (len > 1) {

                    String msg = "";
                    for (byte b : src) {
                        msg = msg + String.format("%02X", b);
                    }

                    if (msg.equalsIgnoreCase(CodeConfig.UsrHeartbeat)) {//有人模块的心跳
                        in.skipBytes(in.readableBytes());//标记已读取完毕
                        //收到了尾部字节，完成接收
                        ByteBuf buf = Unpooled.wrappedBuffer(src);
                        out.add(buf);
                        return;
                    }
                    //获取前八个字节
                    String msg1 = "";
                    for (int i = 0; i < src.length; i++) {

                        msg1 = msg1 + String.format("%02X", src[i]);
                        if (i >= 3) {
                            break;
                        }
                    }

                    String TransActionId = StringUtil.hexToDecimal(msg1.substring(0, 4));//交互（通信）标识：2个字节 为此次通信事务处理标识符，一般每次通信之后将被要求加1以区别不同的通信数据报文。
                    String protocal = StringUtil.hexToDecimal(msg1.substring(4, 8));//协议标识：2个字节 表示该条指令遵循ModbusTCP协议，一般都为00 00

                    Queue<JsonMsg> queue = ModbusSendSyncMsgHandler.msgQueue.get(host + ":" + post);

                    if (queue != null && queue.size() > 0) {

                        JsonMsg queueMsg = queue.peek(); // 返回队首元素，但是不删除。

                        if ((TransActionId + protocal).equalsIgnoreCase(queueMsg.getUuid())) {
                            //获取modbus字节数
                            String msg2 =String.valueOf(src[5] & 0xFF);
                            if (len < Integer.valueOf(msg2) + 6) {
                                // 字节长度不够,直接返回,等下个包进行拼接
                                return;
                            } else {
                                in.skipBytes(in.readableBytes());//标记已读取完毕
                                //收到了尾部字节，完成接收
                                ByteBuf buf = Unpooled.wrappedBuffer(src);
                                out.add(buf);
                                return;
                            }

                        } else {
                            // 包头不对，直接断开连接
                            channelHandlerContext.close();
                        }
                    }
                }
                //非结尾字节，继续接收,当数据包的长度不够时直接return，netty在缓冲区有数据时会一直调用decode方法，所以我们只需要等待下一个数据包传输过来一起解析(解决半包问题)
                return;
            } else {
                // 发空数据包过来的家伙直接断开连接
                channelHandlerContext.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.error("TCP连接异常！，信息：{}", cause.getMessage(), cause);
//        ctx.close();
        }

    }
}
