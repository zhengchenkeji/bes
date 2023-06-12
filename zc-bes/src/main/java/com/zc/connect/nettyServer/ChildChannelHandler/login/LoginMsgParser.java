package com.zc.connect.nettyServer.ChildChannelHandler.login;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zc.connect.nettyServer.ChildChannelHandler.EnergyHandler.JsonMsgParser;
import com.zc.connect.nettyServer.dto.param.LoginParam;
import com.zc.connect.nettyServer.dto.result.CmdMsg;

import java.lang.reflect.Type;

public class LoginMsgParser implements JsonMsgParser
{

    @Override
    public Object parse(JsonObject jsonObject)
    {
        Type type = new TypeToken<CmdMsg<LoginParam>>()
        {
        }.getType();

        Gson gson = new Gson();

        return gson.fromJson(jsonObject, type);

    }

}
