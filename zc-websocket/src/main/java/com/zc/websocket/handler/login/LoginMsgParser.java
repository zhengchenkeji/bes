package com.zc.websocket.handler.login;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zc.websocket.dto.param.LoginParam;
import com.zc.websocket.dto.result.CmdMsg;
import com.zc.websocket.handler.JsonMsgParser;


import java.lang.reflect.Type;

/**
 * 登录消息解析器
 */
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
