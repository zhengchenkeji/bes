package com.zc.efounder.JEnterprise.commhandler;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.ApplicationContextProvider;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.ThreadPool.ThreadPool;
import com.zc.config.Manager.MqttClientServer;
import com.zc.connect.business.handler.ModbusMsgReceive;
import com.zc.connect.config.SyncFuture;
import com.zc.connect.nettyClient.ClientHandler;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.Product;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

/**
 * description: 消费者消息回调
 * author: sunshangeng
 * date:2023/3/14 17:19
 */
@Slf4j
public class MqttClientCallBack implements MqttCallbackExtended {

    private static RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);

    /**
     * 系统的mqtt客户端id
     */
    private String mqttClientId;

    public MqttClientCallBack(String clientId) {
        this.mqttClientId = clientId;
    }

    /**
     * MQTT 断开连接会执行此方法
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.info("断开了MQTT连接 ：{}", throwable.getMessage());
        System.out.println(mqttClientId);
        for (MqttClient mqttClients : MqttClientServer.clientList) {
            if (mqttClientId.equalsIgnoreCase(mqttClients.getClientId())) {
                MqttClientServer.clientList.remove(mqttClients);
            }
        }

        log.error(throwable.getMessage(), throwable);
    }

    /**
     * publish发布成功后会执行到这里
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("发布消息成功");
    }

    /**
     * subscribe订阅后得到的消息会执行到这里
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        log.info("收到来自 " + topic + " 的消息：{}", new String(message.getPayload()));
        //线程池执行
        ThreadPool.executor.execute(() -> {
            try {
                MqttHandler.executeMqttMessage(topic,new String(message.getPayload()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    /**
     *连接成功后的回调 可以在这个方法执行 订阅主题  生成Bean的 MqttConfiguration方法中订阅主题 出现bug
     *重新连接后  主题也需要再次订阅  将重新订阅主题放在连接成功后的回调 比较合理
     * @param reconnect
     * @param serverURI
     */
    @Override
    public void connectComplete(boolean reconnect,String serverURI) {
        //ip
        String host = serverURI.substring(serverURI.indexOf("//") + 2,serverURI.lastIndexOf(":"));
        //端口
        String port = serverURI.substring(host.length() + 7,serverURI.length());

        Map<String,Product> productMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product);

        if (productMap == null || productMap.size() == 0) {
            return;
        }

        for (Product product : productMap.values()) {
            //获取启动状态
            String state = product.getState();//启用状态;0-停用;1-启用
            if ("0".equalsIgnoreCase(state)) {
                continue;
            }

            //获取物联类型
            String iot_type = product.getIotType();//物联类型 0 直连设备  1 网关设备  2 网关子设备
            if (!"0".equalsIgnoreCase(iot_type) && !"1".equalsIgnoreCase(iot_type)) {
                continue;
            }
            //获取通讯协议
            String communication_protocol = product.getCommunicationProtocol();//通讯协议  1:mqtt
            if (!"1".equalsIgnoreCase(communication_protocol)) {
                continue;
            }

            //获取所有的设备
            Map<String, Equipment> equipmentMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);


            if (equipmentMap == null || equipmentMap.size() == 0) {
                continue;
            }

            for (Equipment equipment : equipmentMap.values()) {
                if (equipment.getProductId().equals(product.getId())) {//产品id相同
                    //获取设备ip以及端口
                    String equipmentIp = equipment.getIpAddress();
                    String equipmentPort = equipment.getPortNum();

                    if (!host.equalsIgnoreCase(equipmentIp) || !port.equalsIgnoreCase(equipmentPort)) {
                        continue;
                    }

                    ModbusMsgReceive clientMsgReceive =  ClientHandler.modbusMsgReceive;
                    clientMsgReceive.controllerState(host, Integer.parseInt(port),true);
                }
            }
        }
    }

}
