package com.ruoyi.common.utils.sms.model;

/**
 * description:发送消息响应工具类
 * author: sunshangeng
 * date:2022/11/21 21:13
 */
public class SmsResult {
    /**是否成功*/
    private boolean success;
    /**响应消息*/
    private String message;

    public SmsResult(boolean success) {
        this.success = success;
    }

    public SmsResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static SmsResult success(){
        return new SmsResult(true);
    }
    public static SmsResult success(String message){
        return new SmsResult(true,message);
    }

    public static SmsResult fail(String message){
        return new SmsResult(false,message);
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
