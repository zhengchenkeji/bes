package com.zc.websocket.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zc.websocket.constant.Method;
import com.zc.websocket.constant.RequestObject;
import com.zc.websocket.constant.ResponseObject;
import com.zc.websocket.core.TextWebSocketFrameHandler;
import com.zc.websocket.dto.param.HeartbeatParam;
import com.zc.websocket.dto.param.LoginParam;
import com.zc.websocket.handler.heartbeat.HeartbeatCmdHandler;
import com.zc.websocket.handler.heartbeat.HeartbeatMsgParser;
import com.zc.websocket.handler.heartbeat.HeartbeatResultSender;
import com.zc.websocket.handler.login.LoginCmdHandler;
import com.zc.websocket.handler.login.LoginMsgParser;
import com.zc.websocket.handler.login.LoginResultSender;


/**
 * 消息识别器
 * @author Athena-xiepufeng
 */
public class ParseDiscern {

    /*
     * 消息通道处理器
     */
    private TextWebSocketFrameHandler channelHandler;


    public ParseDiscern(TextWebSocketFrameHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    public void parseAndProcessByJsonRpcType(JsonObject jsonObject, String rpcType)
    {

        if (null == rpcType || null == jsonObject)
        {
            return;
        }

        switch (rpcType)
        {
            case RequestObject.METHOD:
                JsonElement jsonElement = jsonObject.getAsJsonPrimitive(rpcType);

                if (null != jsonElement)
                {
                    String method = jsonElement.getAsString();
                    parseAndProcessByMethod(jsonObject, method);
                }

                break;
            case ResponseObject.RESULT:
                break;
            default:
                break;
        }

    }

    private void parseAndProcessByMethod(JsonObject jsonObject, String method)
    {
        if (null == method || null == jsonObject)
        {
            return;
        }

        switch (method)
        {
            case Method.LOGIN:
                parseAndProcessLogin(jsonObject);
                break;
            case Method.EVENT:
                break;
            case Method.HEARTBEAT:
                parseAndProcessHeartbeat(jsonObject);
                break;
            default:
                break;
        }

    }

    /**
     * 业务层心跳处理
     *
     * @param jsonObject
     */
    private void parseAndProcessHeartbeat(JsonObject jsonObject)
    {
        if (null == jsonObject)
        {
            return;
        }

        // 解析并执行具体的命令
        HeartbeatMsgParser jsonMsgParser = new HeartbeatMsgParser();

        // 心跳消息处理器
        HeartbeatCmdHandler heartbeatCmdHandler = new HeartbeatCmdHandler();
        heartbeatCmdHandler.setChannelHandler(channelHandler);

        // 心跳消息发送器
        HeartbeatResultSender heartbeatResultSender = new HeartbeatResultSender();
        heartbeatResultSender.setChannelHandler(channelHandler);

        RpcCmdProcessor rpcCmd = new RpcCmdProcessor();

        // 添加数据解析器
        rpcCmd.setJsonMsgParser(jsonMsgParser);

        // 添加数据处理器
        rpcCmd.setCmdHandler(heartbeatCmdHandler);

        // 添加数据发送器
        rpcCmd.setResultSendable(heartbeatResultSender);

        rpcCmd.parseAndExecute(jsonObject);
    }


    /**
     * 登录 WebSocket 绑定 WebSocket 连接到 http session。
     *
     * @param jsonObject
     */
    private void parseAndProcessLogin(JsonObject jsonObject)
    {
        if (null == jsonObject)
        {
            return;
        }

        // 解析并执行具体的命令
        LoginMsgParser jsonMsgParser = new LoginMsgParser();

        // 登录消息的处理器
        LoginCmdHandler loginCmdHandler = new LoginCmdHandler();
        loginCmdHandler.setChannelHandler(channelHandler);

        // 登录消息发送器
        LoginResultSender loginResultSender = new LoginResultSender();
        loginResultSender.setChannelHandler(channelHandler);

        RpcCmdProcessor rpcCmd = new RpcCmdProcessor();

        // 添加数据解析器
        rpcCmd.setJsonMsgParser(jsonMsgParser);

        // 添加数据处理器
        rpcCmd.setCmdHandler(loginCmdHandler);

        // 添加数据发送器
        rpcCmd.setResultSendable(loginResultSender);

        rpcCmd.parseAndExecute(jsonObject);

    }

    public TextWebSocketFrameHandler getChannelHandler() {
        return channelHandler;
    }

    public void setChannelHandler(TextWebSocketFrameHandler channelHandler) {
        this.channelHandler = channelHandler;
    }
}
