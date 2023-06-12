package com.zc.common.core.websocket.pojo;

public class RedisWebSocketMsg {
    private String subscriber;
    private CmdMsg cmdMsg;

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    public CmdMsg getCmdMsg() {
        return cmdMsg;
    }

    public void setCmdMsg(CmdMsg cmdMsg) {
        this.cmdMsg = cmdMsg;
    }

}
