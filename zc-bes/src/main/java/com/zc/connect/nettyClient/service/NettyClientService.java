package com.zc.connect.nettyClient.service;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 14:24 2023/3/14
 * @Modified By:
 */
public interface NettyClientService {

    public boolean sendMsg(String text, String dataId, String serviceId);

    public String sendSyncMsg(String text, String dataId, String serviceId);

    public void ackSyncMsg(String msg);
}
