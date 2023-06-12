package com.zc.websocket.handler.heartbeat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zc.websocket.core.TextWebSocketFrameHandler;
import com.zc.websocket.dto.param.HeartbeatError;
import com.zc.websocket.dto.result.*;
import com.zc.websocket.enums.ErrorCodeEnum;
import com.zc.websocket.handler.AbstractSender;
import com.zc.websocket.handler.ResultSendable;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.lang.reflect.Type;
import java.util.logging.Logger;

/**
 * 心跳消息发送器
 * @author Athena-xiepufeng
 */
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

            TextWebSocketFrameHandler textWebSocketFrameHandler = getChannelHandler();
            if (textWebSocketFrameHandler == null || textWebSocketFrameHandler.getCtx() == null)
            {
                log.warning("心跳失败, 因为 channel 可能关闭。");
                return;
            }

            textWebSocketFrameHandler.getCtx().channel().writeAndFlush(new TextWebSocketFrame(msg));
            return;
        }

        HeartbeatResult heartbeatResult = new HeartbeatResult();
        heartbeatResult.setMessage(result.getMessage());
        heartbeatResult.setCode(result.getCode());

        ResultMsg<HeartbeatResult> resultMsg = new ResultMsg<>();
        resultMsg.setResult(heartbeatResult);

        Gson gson = new Gson();
        Type type = new TypeToken<ResultMsg<HeartbeatResult>>()
        {
        }.getType();

        String msg = gson.toJson(resultMsg, type);
        TextWebSocketFrameHandler textWebSocketFrameHandler = getChannelHandler();
        if (textWebSocketFrameHandler == null || textWebSocketFrameHandler.getCtx() == null)
        {
            log.warning("心跳失败，因为 channel 可能关闭。" );
            return;
        }

        textWebSocketFrameHandler.getCtx().channel().writeAndFlush(new TextWebSocketFrame(msg));
    }

}
