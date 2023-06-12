package com.zc.connect.nettyServer.ChildChannelHandler.login;

import com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler.CmdHandler;
import com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler.ServerHandler_Energy;
import com.zc.connect.nettyServer.bo.ChannelProperty;
import com.zc.connect.nettyServer.bo.ChannelPropertyCommon;
import com.zc.connect.nettyServer.constant.AttributeKeyConst;
import com.zc.connect.nettyServer.dto.param.LoginParam;
import com.zc.connect.nettyServer.dto.result.CmdExeResult;
import com.zc.connect.nettyServer.dto.result.CmdMsg;
import com.zc.connect.nettyServer.dto.result.JsonMsg;
import com.zc.connect.nettyServer.enums.ChannelStatus;
import com.zc.connect.nettyServer.enums.ErrorCodeEnum;
import com.zc.connect.util.TokenUtil;
import io.netty.channel.Channel;

import java.util.logging.Logger;

public class LoginCmdHandler implements CmdHandler
{

    private static final Logger log = Logger.getLogger(LoginCmdHandler.class.getName());

    /*
     * 消息通道处理器
     */
    private ServerHandler_Energy serverHandler_energy;

    @Override
    public CmdExeResult execute(JsonMsg jsonMsg)
    {

        CmdExeResult result = new CmdExeResult();

        result.setJsonMsg(jsonMsg);

        Channel channel = serverHandler_energy.getCtx().channel();

        ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();

        // 非验证登录
        if (channelProperty.getStatus().equals(ChannelStatus.TOKENSN_BOUND))
        {
            result.setCode(ErrorCodeEnum.ERR_OK.getInternalCode());
            result.setMessage(ErrorCodeEnum.ERR_OK.getInternalMessage());

            log.info("user address: " + channel.remoteAddress() + " Socket ok");

            return result;
        }

        ChannelPropertyCommon cpc = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_COMMON_KEY).get();

        @SuppressWarnings("unchecked")
        CmdMsg<LoginParam> loginCmdMsg = (CmdMsg<LoginParam>) jsonMsg;

        // 登录密码校验
        if (!loginCmdMsg.getParams().getPassword().equals(cpc.getPassword()))
        {
            result.setCode(ErrorCodeEnum.ERR_PWD.getInternalCode());
            result.setMessage(ErrorCodeEnum.ERR_PWD.getInternalMessage());
            log.warning("user tokenSN:" + loginCmdMsg.getParams().getTokenSN() + " login failed. Password error");
            return result;
        }

        // 判断 tokenSN 是否存在
        String tokenSN = loginCmdMsg.getParams().getTokenSN();
        if (!TokenUtil.isTokenExist(tokenSN))
        {
            result.setCode(ErrorCodeEnum.ERR_TOKENSN.getInternalCode());
            result.setMessage(ErrorCodeEnum.ERR_TOKENSN.getInternalMessage());
            log.warning("Error code:" + ErrorCodeEnum.ERR_TOKENSN.getInternalCode() + " tokenSN:"
                    + tokenSN + " " + ErrorCodeEnum.ERR_TOKENSN.getInternalMessage());
            return result;

        }

        // 绑定tokenSN和channel的关系
        updateChannelProperty(loginCmdMsg, channel);

        result.setCode(ErrorCodeEnum.ERR_OK.getInternalCode());
        result.setMessage(ErrorCodeEnum.ERR_OK.getInternalMessage());

        log.info("user tokenSN:" + loginCmdMsg.getParams().getTokenSN() + " Login Socket ok");

        return result;

    }

    /**
     * 更新 channel 属性及状态。
     *
     * @param loginCmdMsg
     * @param channel
     */
    public void updateChannelProperty(CmdMsg<LoginParam> loginCmdMsg, Channel channel)
    {
        ChannelProperty channelProperty = channel.attr(AttributeKeyConst.CHANNEL_PROPERTY_KEY).get();
        if (channelProperty == null)
        {
            log.warning("Error: can not get the ChannelProperty when Update Channel Property");
            return;
        }

        channelProperty.setTokenSN(loginCmdMsg.getParams().getTokenSN());
        channelProperty.setStatus(ChannelStatus.TOKENSN_BOUND);
        channelProperty.setStatusCount(0);

        log.info("Bind propertis to channel. token:" + loginCmdMsg.getParams().getTokenSN());
    }

    public ServerHandler_Energy getServerHandler_energy() {
        return serverHandler_energy;
    }

    public void setServerHandler_energy(ServerHandler_Energy serverHandler_energy) {
        this.serverHandler_energy = serverHandler_energy;
    }
}
