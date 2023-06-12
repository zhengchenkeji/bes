package com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zc.connect.nettyServer.ChildChannelHandler.heartbeat.HeartbeatCmdHandler;
import com.zc.connect.nettyServer.ChildChannelHandler.heartbeat.HeartbeatMsgParser;
import com.zc.connect.nettyServer.ChildChannelHandler.heartbeat.HeartbeatResultSender;
import com.zc.connect.nettyServer.ChildChannelHandler.login.LoginCmdHandler;
import com.zc.connect.nettyServer.ChildChannelHandler.login.LoginMsgParser;
import com.zc.connect.nettyServer.ChildChannelHandler.login.LoginResultSender;
import com.zc.connect.nettyServer.constant.Method;
import com.zc.connect.nettyServer.constant.RequestObject;
import com.zc.connect.nettyServer.constant.ResponseObject;


/**
 * @author xiepufeng
 */
public class ParseDiscern
{

    /*
     * 消息通道处理器
     */
    private ServerHandler_Energy serverHandler_energy;

    public ParseDiscern(ServerHandler_Energy serverHandler_energy) {
        this.serverHandler_energy = serverHandler_energy;
    }

    private  void parseAndProcessByMethod(JsonObject jsonObject, String method)
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

    public void parseAndProcessByJsonType(JsonObject jsonObject, String rpcType)
    {

        if (null == rpcType || null == jsonObject)
        {
            return;
        }

        switch (rpcType)
        {
            case RequestObject.METHOD:
                JsonElement jsonElement = jsonObject.getAsJsonPrimitive(rpcType);
                if (null == jsonElement)
                {
                    break;
                }

                String method = jsonElement.getAsString();
                parseAndProcessByMethod(jsonObject, method);
                break;

            case ResponseObject.RESULT:
                break;

            case ResponseObject.ERROR:
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
        HeartbeatMsgParser heartbeatMsgParser = new HeartbeatMsgParser();

        HeartbeatCmdHandler heartbeatCmdHandler = new HeartbeatCmdHandler();
        heartbeatCmdHandler.setServerHandler_energy(serverHandler_energy);

        HeartbeatResultSender heartbeatResultSender = new HeartbeatResultSender();
        heartbeatResultSender.setServerHandler_energy(serverHandler_energy);

        RpcCmdProcessor rpcCmd = new RpcCmdProcessor();

        rpcCmd.setJsonMsgParser(heartbeatMsgParser);
        rpcCmd.setCmdHandler(heartbeatCmdHandler);
        rpcCmd.setResultSendable(heartbeatResultSender);

        rpcCmd.parseAndExecute(jsonObject);
    }


    /**
     * 登录 Socket 绑定 Socket
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
        LoginMsgParser loginMsgParser = new LoginMsgParser();

        LoginCmdHandler loginCmdHandler = new LoginCmdHandler();
        loginCmdHandler.setServerHandler_energy(serverHandler_energy);

        LoginResultSender loginResultSender = new LoginResultSender();
        loginResultSender.setServerHandler_energy(serverHandler_energy);

        RpcCmdProcessor rpcCmd = new RpcCmdProcessor();

        rpcCmd.setJsonMsgParser(loginMsgParser);
        rpcCmd.setCmdHandler(loginCmdHandler);
        rpcCmd.setResultSendable(loginResultSender);

        rpcCmd.parseAndExecute(jsonObject);

    }

    public ServerHandler_Energy getServerHandler_energy() {
        return serverHandler_energy;
    }

    public void setServerHandler_energy(ServerHandler_Energy serverHandler_energy) {
        this.serverHandler_energy = serverHandler_energy;
    }
}
