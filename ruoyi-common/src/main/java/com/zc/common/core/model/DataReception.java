package com.zc.common.core.model;

/**
 * 数据接收
 *
 * @author qindehua
 * @date 2022/09/27
 */
public class DataReception {

    //状态
    private Boolean state;
    //消息
    private String msg;
    //数据
    private Object data;
    //标识
    private String code;

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataReception(Boolean state, String msg, String code) {
        this.state = state;
        this.msg = msg;
        this.code = code;
    }

    public DataReception(Boolean state, String msg, Object data) {
        this.state = state;
        this.msg = msg;
        this.data = data;
    }

    public DataReception(Boolean state, String msg) {
        this.state = state;
        this.msg = msg;
    }

    public DataReception(Boolean state, Object data) {
        this.state = state;
        this.data = data;
    }

    public DataReception(Boolean state) {
        this.state = state;
    }

    public DataReception() {
        super();
    }
}
