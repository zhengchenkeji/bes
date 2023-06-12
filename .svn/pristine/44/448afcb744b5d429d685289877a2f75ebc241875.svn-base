package com.zc.connect.nettyServer.ChildChannelHandler.heartbeat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler.AbstractSender;
import com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler.ResultSendable;
import com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler.ServerHandler_Energy;
import com.zc.connect.nettyServer.dto.param.HeartbeatError;
import com.zc.connect.nettyServer.dto.result.CmdExeResult;
import com.zc.connect.nettyServer.dto.result.ErrorMsg;
import com.zc.connect.nettyServer.dto.result.HeartbeatResult;
import com.zc.connect.nettyServer.dto.result.ResultMsg;
import com.zc.connect.nettyServer.enums.ErrorCodeEnum;

import java.lang.reflect.Type;
import java.util.logging.Logger;

public class HeartbeatResultSender extends AbstractSender implements ResultSendable
{

    private static final Logger log = Logger.getLogger(HeartbeatResultSender.class.getName());

    @Override
    public void send(CmdExeResult result)
    {
        if (result.getCode() != ErrorCodeEnum.ERR_OK.getInternalCode())
        {
            HeartbeatError heartbeatError = new HeartbeatError();
            heartbeatError.setCode(result.getCode());
            heartbeatError.setMessage(result.getMessage());

            ErrorMsg<HeartbeatError> errorMsg = new ErrorMsg<>();
            errorMsg.setError(heartbeatError);

            Gson gson = new Gson();
            Type type = new TypeToken<ErrorMsg<HeartbeatError>>()
            {
            }.getType();
            String msg = gson.toJson(errorMsg, type);

            ServerHandler_Energy serverHandler_energy = getServerHandler_energy();
            if (serverHandler_energy == null || serverHandler_energy.getCtx() == null)
            {
                log.warning("heartbeat fail, because the channel maybe closed.");
                return;
            }

            serverHandler_energy.getCtx().channel().writeAndFlush(msg);
            return;
        }

        HeartbeatResult heartbeatResult = new HeartbeatResult();
        heartbeatResult.setMessage(result.getMessage());

        ResultMsg<HeartbeatResult> resultMsg = new ResultMsg<>();
        resultMsg.setResult(heartbeatResult);

        Gson gson = new Gson();
        Type type = new TypeToken<ResultMsg<HeartbeatResult>>()
        {
        }.getType();

        String msg = gson.toJson(resultMsg, type);
        ServerHandler_Energy serverHandler_energy = getServerHandler_energy();
        if (serverHandler_energy == null || serverHandler_energy.getCtx() == null)
        {
            log.warning("Heartbeat fail, because the channel maybe closed.");
            return;
        }

        serverHandler_energy.getCtx().channel().writeAndFlush(msg);
    }

}
