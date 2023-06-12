package com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler;


import com.zc.connect.nettyServer.dto.result.CmdExeResult;

public interface ResultSendable
{
    void send(CmdExeResult result);
}
