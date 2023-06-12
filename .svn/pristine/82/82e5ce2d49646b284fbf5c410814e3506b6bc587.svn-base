package com.zc.websocket.handler.heartbeat;

import com.zc.websocket.bo.ChannelProperty;
import com.zc.websocket.constant.AttributeKeyConst;
import com.zc.websocket.core.TextWebSocketFrameHandler;
import com.zc.websocket.dto.param.HeartbeatParam;
import com.zc.websocket.dto.result.CmdExeResult;
import com.zc.websocket.dto.result.CmdMsg;
import com.zc.websocket.dto.result.JsonMsg;
import com.zc.websocket.enums.ChannelStatus;
import com.zc.websocket.enums.ErrorCodeEnum;
import com.zc.websocket.handler.CmdHandler;
import io.netty.channel.Channel;
import com.zc.websocket.util.MsgUtil;

import java.util.logging.Logger;

/**
 * 心跳消息处理器
 * @author Athena-xiepufeng
 */
public class HeartbeatCmdHandler implements CmdHandler
{

    private static final Logger log = Logger.getLogger(HeartbeatCmdHandler.class.getName());

    /*
     * 消息通道处理器
     */
    private TextWebSocketFrameHandler channelHandler;

    @Override
    public CmdExeResult execute(JsonMsg jsonMsg)
    {
        CmdExeResult result = new CmdExeResult();
        result.setJsonMsg(jsonMsg);

        // 获取 channel 属性对象
        Channel channel = channelHandler.getCtx().channel();
        ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();


        // 获取 channel 属性对象
        if (channelProperty == null)
        {

            result.setCode(ErrorCodeEnum.ERR_CHANNEL_NO_PROPERTY.getInternalCode());
            result.setMessage(ErrorCodeEnum.ERR_CHANNEL_NO_PROPERTY.getInternalMessage());
            log.warning("Error code:" + ErrorCodeEnum.ERR_TOKEN_SN.getInternalCode());

            return result;

        }

        // 判断 tokenSN 是否存在
        String tokenSN = channelProperty.getTokenSN();
        if (!MsgUtil.isTokenSNExist(tokenSN))
        {
            result.setCode(ErrorCodeEnum.ERR_TOKEN_SN.getInternalCode());
            result.setMessage(ErrorCodeEnum.ERR_TOKEN_SN.getInternalMessage());
            log.warning("Error code:" + ErrorCodeEnum.ERR_TOKEN_SN.getInternalCode() + " tokenSN:"
                    + tokenSN + " " + ErrorCodeEnum.ERR_TOKEN_SN.getInternalMessage());
            return result;

        }

        // 更新 tokenSN 的超时时间，防止会话超时超时
        MsgUtil.heartbeatEvent(tokenSN);

        // 没有绑定到 tokenSN 则不接收心跳消息
        ChannelStatus channelStatus = channelProperty.getStatus();
        if (ChannelStatus.TOKENSN_BOUND != channelStatus)
        {
            result.setCode(ErrorCodeEnum.ERR_HEARTBEAT_AT_INCORRECT_CHANNEL_STATUS.getInternalCode());
            result.setMessage(ErrorCodeEnum.ERR_HEARTBEAT_AT_INCORRECT_CHANNEL_STATUS.getInternalMessage());

            log.warning("Error code:" + ErrorCodeEnum.ERR_HEARTBEAT_AT_INCORRECT_CHANNEL_STATUS.getInternalCode()
                    + " tokenSN:" + tokenSN + " "
                    + ErrorCodeEnum.ERR_HEARTBEAT_AT_INCORRECT_CHANNEL_STATUS.getInternalMessage());

            return result;

        }

        // 刷新心跳计数器复位标志，通知定时器任务复位心跳计数器
        channelProperty.heartbeatRefresh();

        result.setCode(ErrorCodeEnum.ERR_OK.getInternalCode());
        result.setMessage(ErrorCodeEnum.ERR_OK.getInternalMessage());

        return result;
    }

    public TextWebSocketFrameHandler getChannelHandler() {
        return channelHandler;
    }

    public void setChannelHandler(TextWebSocketFrameHandler channelHandler) {
        this.channelHandler = channelHandler;
    }
}
