package com.zc.connect.MQTTClient;

import org.eclipse.paho.client.mqttv3.*;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 11:44 2023/2/24
 * @Modified By:
 */

/**
 * 发布消息的回调类
 *
 * 必须实现MqttCallback的接口并实现对应的相关接口方法CallBack 类将实现 MqttCallBack。
 * 每个客户机标识都需要一个回调实例。在此示例中，构造函数传递客户机标识以另存为实例数据。
 * 在回调中，将它用来标识已经启动了该回调的哪个实例。
 * 必须在回调类中实现三个方法：
 *
 *  public void messageArrived(MqttTopic topic, MqttMessage message)接收已经预订的发布。
 *
 *  public void connectionLost(Throwable cause)在断开连接时调用。
 *
 *  public void deliveryComplete(MqttDeliveryToken token))
 *  接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用。
 *  由 MqttClient.connect 激活此回调。
 *
 */
public class PushCallback implements MqttCallbackExtended {
    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        System.out.println("连接断开，可以做重连");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // subscribe后得到的消息会执行到这里面
        System.out.println("接收消息主题 : " + topic);
        System.out.println("接收消息Qos : " + message.getQos());
        System.out.println("接收消息内容 : " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("deliveryComplete---------" + token.isComplete());
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println("reconnect");
        System.out.println("serverURI");

        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                publishMessage(serverURI,"MQTT","hello java",0);

            }
        };
        timer.schedule(timerTask,2 * 1000,10 * 1000);
//        try{
//            //如果监测到有,号，说明要订阅多个主题
//            if(mqttTopic.contains(",")){
//                //多主题
//                String[] mqttTopics = mqttTopic.split(",");
//                mqttClient.subscribe(mqttTopics);
//            }else{
//                //单主题
//                mqttClient.subscribe(mqttTopic);
//            }
//            log.info("----TAG", "connectComplete: 订阅主题成功");
//        }catch(Exception e){
//            e.printStackTrace();
//            log.info("----TAG", "error: 订阅主题失败");
//        }
    }


    /**
     * 发布消息
     * @param publishTopic 发布消息的主题名称
     * @param message 消息内容
     * @param qos 消息质量
     */
    public void publishMessage(String serverURI, String publishTopic, String message, int qos){

        MqttClient mqttClient = MqttClientThread.mqttClients.get(serverURI);
        // 判断是否连接
        if (mqttClient != null && mqttClient.isConnected()){
            System.out.println("发布消息.......");
            System.out.println("发布消息人的clientId："+mqttClient.getClientId());
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(qos);
            mqttMessage.setPayload(message.getBytes());
            //是否保持持久化
            mqttMessage.setRetained(true);
            MqttTopic mqttTopic = mqttClient.getTopic(publishTopic);
            if (mqttTopic != null){
                try {
                    MqttDeliveryToken publish = mqttTopic.publish(mqttMessage);
                    if (!publish.isComplete()){
                        System.out.println("消息发布成功！");
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("mqttTopic == null");
            }
        }else {
            System.out.println("mqttClient == null || mqttClient.isConnected() == false");
        }
    }
}
