package com.zc.websocket.handler.login;

import com.zc.websocket.bo.ChannelProperty;
import com.zc.websocket.bo.ChannelPropertyCommon;
import com.zc.websocket.constant.AttributeKeyConst;
import com.zc.websocket.core.TextWebSocketFrameHandler;
import com.zc.websocket.dto.param.LoginParam;
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
 * 登录消息的处理器
 * @author Athena-xiepufeng
 */
public class LoginCmdHandler implements CmdHandler
{

    private static final Logger log = Logger.getLogger(LoginCmdHandler.class.getName());

    /*
     * 消息通道处理器
     */
    private TextWebSocketFrameHandler channelHandler;

    @Override
    public CmdExeResult execute(JsonMsg jsonMsg)
    {
        @SuppressWarnings("unchecked")
        CmdMsg<LoginParam> loginCmdMsg = (CmdMsg<LoginParam>) jsonMsg;

        CmdExeResult result = new CmdExeResult();
        result.setJsonMsg(jsonMsg);

        ChannelPropertyCommon cpc = channelHandler.getCtx().channel().attr(AttributeKeyConst.CHANNEL_PROPERTY_COMMON_KEY).get();

        // 登录密码校验
        if (!loginCmdMsg.getParams().getPassword().equals(cpc.getPassword()))
        {
            result.setCode(ErrorCodeEnum.ERR_PWD.getInternalCode());
            result.setMessage(ErrorCodeEnum.ERR_PWD.getInternalMessage());
            log.warning("登录失败。 密码错误！");
            return result;
        }

        String tokenSN = loginCmdMsg.getParams().getTokenSN();

        // 解密tokenSN
        String token = MsgUtil.decryptToken(tokenSN);

        if (token != null)
        {
            loginCmdMsg.getParams().setTokenSN(token);
        }else
        {
            // 判断 tokenSN 是否存在
            if (!MsgUtil.isTokenSNExist(tokenSN))
            {
                result.setCode(ErrorCodeEnum.ERR_TOKEN_SN.getInternalCode());
                result.setMessage(ErrorCodeEnum.ERR_TOKEN_SN.getInternalMessage());
                log.warning("Error code:" + ErrorCodeEnum.ERR_TOKEN_SN.getInternalCode() + " tokenSN:"
                        + tokenSN + " " + ErrorCodeEnum.ERR_TOKEN_SN.getInternalMessage());
                return result;
            }
        }

        // 绑定tokenSN和channel的关系
        Channel channel = channelHandler.getCtx().channel();
        if (!updateChannelProperty(loginCmdMsg, channel))
        {

            result.setCode(ErrorCodeEnum.ERR_TOKEN_SN_NOT_BIND_CHANNEL.getInternalCode());
            result.setMessage(ErrorCodeEnum.ERR_TOKEN_SN_NOT_BIND_CHANNEL.getInternalMessage());
            log.info("tokenSN:"
                    + loginCmdMsg.getParams().getTokenSN() + " tokenSN 和 channel绑定失败");
            return result;
        }

        result.setCode(ErrorCodeEnum.ERR_OK.getInternalCode());
        result.setMessage(ErrorCodeEnum.ERR_OK.getInternalMessage());

        log.info( "tokenSN:"
                + loginCmdMsg.getParams().getTokenSN() + " 登录 webSocket 成功");

        return result;

    }

    /**
     * 更新 channel 属性及状态。
     *
     * @param loginCmdMsg
     * @param channel
     */
    public Boolean updateChannelProperty(CmdMsg<LoginParam> loginCmdMsg, Channel channel)
    {
        ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();
        if (channelProperty == null)
        {
            log.warning("Error: 更新 Channel Property，无法获取 channelProperty");
            return false;
        }

        String tokenSN = loginCmdMsg.getParams().getTokenSN();

        channelProperty.setTokenSN(tokenSN);
        channelProperty.setStatus(ChannelStatus.TOKENSN_BOUND);
        channelProperty.setStatusCount(0);

        log.info("tokenSN 绑定到 channelProperty。 tokenSN:" + loginCmdMsg.getParams().getTokenSN());

        return true;
    }

    public TextWebSocketFrameHandler getChannelHandler()
    {
        return channelHandler;
    }

    public void setChannelHandler(TextWebSocketFrameHandler channelHandler)
    {
        this.channelHandler = channelHandler;
    }

}
