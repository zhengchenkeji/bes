package com.zc.connect.nettyServer.ChildChannelHandler.login;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler.AbstractSender;
import com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler.ResultSendable;
import com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler.ServerHandler_Energy;
import com.zc.connect.nettyServer.dto.param.LoginError;
import com.zc.connect.nettyServer.dto.param.LoginParam;
import com.zc.connect.nettyServer.dto.result.*;
import com.zc.connect.nettyServer.enums.ErrorCodeEnum;

import java.lang.reflect.Type;
import java.util.logging.Logger;

public class LoginResultSender extends AbstractSender implements ResultSendable
{

    private static final Logger log = Logger.getLogger(LoginResultSender.class.getName());

    @Override
    public void send(CmdExeResult result)
    {

        @SuppressWarnings("unchecked")
        CmdMsg<LoginParam> loginCmd = (CmdMsg<LoginParam>) result.getJsonMsg();
        if (result.getCode() != ErrorCodeEnum.ERR_OK.getInternalCode())
        {
            LoginError loginError = new LoginError();
            loginError.setCode(result.getCode());
            loginError.setMessage(result.getMessage());

            ErrorMsg<LoginError> errorMsg = new ErrorMsg<>();
            errorMsg.setError(loginError);

            Gson gson = new Gson();
            Type type = new TypeToken<ErrorMsg<LoginError>>()
            {
            }.getType();
            String msg = gson.toJson(errorMsg, type);

            ServerHandler_Energy serverHandler_energy = getServerHandler_energy();
            if (serverHandler_energy == null)
            {
                return;
            }

            serverHandler_energy.getCtx().channel().writeAndFlush(msg);
            return;
        }

        LoginResult loginResult = new LoginResult();
        loginResult.setMessage(result.getMessage());

        ResultMsg<LoginResult> resultMsg = new ResultMsg<>();
        resultMsg.setResult(loginResult);

        Gson gson = new Gson();
        Type type = new TypeToken<ResultMsg<LoginResult>>()
        {
        }.getType();

        String msg = gson.toJson(resultMsg, type);
        ServerHandler_Energy serverHandler_energy = getServerHandler_energy();
        if (serverHandler_energy == null || serverHandler_energy.getCtx() == null)
        {
            log.warning("Login fail, because the channel maybe closed. login user tokenSN:" + loginCmd.getParams().getTokenSN());
            return;
        }

        serverHandler_energy.getCtx().channel().writeAndFlush(msg);
    }
}
