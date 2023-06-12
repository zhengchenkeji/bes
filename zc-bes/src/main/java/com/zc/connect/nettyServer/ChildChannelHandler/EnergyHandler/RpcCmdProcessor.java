package com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler;

import com.google.gson.JsonObject;
import com.zc.connect.nettyServer.dto.result.CmdExeResult;
import com.zc.connect.nettyServer.dto.result.JsonMsg;


public class RpcCmdProcessor
{

    /*
     * 消息解析器
     */
    private JsonMsgParser jsonMsgParser;

    /*
     * 消息的处理器
     */
    private CmdHandler cmdHandler;

    /*
     * 消息发送器
     */
    private ResultSendable resultSendable;

    public void parseAndExecute(JsonObject jsonObject)
    {
        // 1.json消息对象解析成命令对象
        JsonMsg jsonMsg = (JsonMsg) jsonMsgParser.parse(jsonObject);

        // 2.执行命令并获取执行结果
        CmdExeResult result = cmdHandler.execute(jsonMsg);

        // 3.返回执行结果
        resultSendable.send(result);

    }

    public CmdHandler getCmdHandler()
    {
        return cmdHandler;
    }

    public void setCmdHandler(CmdHandler cmdHandler)
    {
        this.cmdHandler = cmdHandler;
    }

    public JsonMsgParser getJsonMsgParser()
    {
        return jsonMsgParser;
    }

    public void setJsonMsgParser(JsonMsgParser jsonMsgParser)
    {
        this.jsonMsgParser = jsonMsgParser;
    }

    public ResultSendable getResultSendable()
    {
        return resultSendable;
    }

    public void setResultSendable(ResultSendable resultSendable)
    {
        this.resultSendable = resultSendable;
    }

}
