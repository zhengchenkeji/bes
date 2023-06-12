package com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler;


import com.zc.connect.nettyServer.dto.result.CmdExeResult;
import com.zc.connect.nettyServer.dto.result.JsonMsg;

public interface CmdHandler
{
    CmdExeResult execute(JsonMsg jsonMsg);
}
