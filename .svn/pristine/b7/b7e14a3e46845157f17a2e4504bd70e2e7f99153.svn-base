package com.zc.connect.business.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zc.connect.business.constant.Cmd;
import com.zc.connect.business.constant.JsonAttr;
import com.zc.connect.business.dto.JsonMsg;
import com.zc.connect.business.dto.ReceiveMsg;
import com.zc.connect.nettyServer.bo.ChannelProperty;
import com.zc.connect.nettyServer.constant.AttributeKeyConst;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Type;

/**
 * 心跳逻辑处理类
 * @author xiepufeng
 */
public class HeartbeatHandler
{
    public static void handler(ChannelHandlerContext ctx, JsonObject jsonObject)
    {
        if (null == ctx || null == jsonObject)
        {
            return;
        }

        if (jsonObject.get(JsonAttr.CODE) != null)
        {
            int code = jsonObject.get(JsonAttr.CODE).getAsInt();

            if (code > 0)
            {
                return;
            }
        }

        // 刷新心跳计数器
        refresh(ctx);

        // 给客户端发送心跳响应数据包
        respond(ctx);

        // 回调
        ReceiptMsgHandler.threadPoolExecutor.execute(() -> callback(ctx));

    }


    /**
     * 刷新心跳计数器
     * @param ctx
     */
    public static void refresh(ChannelHandlerContext ctx)
    {
        if (null == ctx)
        {
            return;
        }

        // 设置 channel 属性
        Channel channel = ctx.channel();

        ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();

        // 刷新心跳计数器复位标志，通知定时器任务复位心跳计数器
        channelProperty.heartbeatRefresh();
    }

    /**
     * 给客户端发送心跳响应数据包
     * @param ctx
     */
    public static void respond(ChannelHandlerContext ctx)
    {
        if (null == ctx)
        {
            return;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<JsonMsg>()
        {
        }.getType();

        JsonMsg jsonMsg = new ReceiveMsg();

        jsonMsg.setIndex(Cmd.HEARTBEAT);

        String msg = gson.toJson(jsonMsg, type);

        msg = CrcUtil.addVerifyCRC(msg);

        if (null == msg)
        {
            return;
        }

        ctx.channel().writeAndFlush(msg);
    }

    private static void callback(ChannelHandlerContext ctx)
    {
        ClientMsgReceive clientMsgReceive = ReceiptMsgHandler.clientMsgReceive;

        // 设置控制器状态为在线
        if (null != clientMsgReceive)
        {
            // 设置 channel 属性
            Channel channel = ctx.channel();

            ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();

            String ip = channelProperty.getTokenSN();

            clientMsgReceive.heartbeatCallback(ip);
        }
    }
}
