package com.zc.websocket.handler.login;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zc.websocket.core.TextWebSocketFrameHandler;
import com.zc.websocket.dto.param.LoginError;
import com.zc.websocket.dto.result.*;
import com.zc.websocket.enums.ErrorCodeEnum;
import com.zc.websocket.handler.AbstractSender;
import com.zc.websocket.handler.ResultSendable;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.lang.reflect.Type;
import java.util.logging.Logger;

/**
 * 登录消息发送器
 * @author Athena-xiepufeng
 */
public class LoginResultSender extends AbstractSender implements ResultSendable
{

    private static final Logger log = Logger.getLogger(LoginResultSender.class.getName());

    @Override
    public void send(CmdExeResult result)
    {
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

            TextWebSocketFrameHandler textWebSocketFrameHandler = getChannelHandler();
            if (textWebSocketFrameHandler == null)
            {
                return;
            }

            textWebSocketFrameHandler.getCtx().channel().writeAndFlush(new TextWebSocketFrame(msg));
            return;
        }

        LoginResult loginResult = new LoginResult();
        loginResult.setMessage(result.getMessage());
        loginResult.setCode(result.getCode());

        ResultMsg<LoginResult> resultMsg = new ResultMsg<>();
        resultMsg.setResult(loginResult);

        Gson gson = new Gson();
        Type type = new TypeToken<ResultMsg<LoginResult>>()
        {
        }.getType();

        String msg = gson.toJson(resultMsg, type);
        TextWebSocketFrameHandler textWebSocketFrameHandler = getChannelHandler();
        if (textWebSocketFrameHandler == null || textWebSocketFrameHandler.getCtx() == null)
        {
            log.warning("登录失败, channel 可能被关闭" );
            return;
        }

        // 登录相应结果到客户端
        textWebSocketFrameHandler.getCtx().channel().writeAndFlush(new TextWebSocketFrame(msg));
    }

}
