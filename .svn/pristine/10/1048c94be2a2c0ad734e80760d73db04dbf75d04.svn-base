package com.zc.connect.MQTTClient;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 11:42 2023/2/24
 * @Modified By:
 */
@Component
public class MqttClientThread extends Thread{

    private static final Logger log = LoggerFactory.getLogger(MqttClientThread.class);

    private String serverURL;
    private String mqttTopic;
    private String clientId ;
    //MQTT的client
    private MqttClient mqttClient;
    //产品id
    private String productId;

    private MqttConnectOptions options;
    private String mqttUsername;
    private String mqttPassWord;

    public static Map<String,MqttClient> mqttClients = new ConcurrentHashMap();

    public MqttClientThread() {

    }

    //构造函数
    public MqttClientThread(String serverURL,String mqttUsername,String mqttPassWord,String mqttTopic,String clientId,String productId) {
        this.serverURL = serverURL;
        this.mqttUsername = mqttUsername;
        this.mqttPassWord = mqttPassWord;
        this.mqttTopic = mqttTopic;
        this.clientId = clientId;
        this.productId = productId;
    }
    /**
     * 退出后释放连接（防止服务器重启造成的channel脏数据）
     */
    @PreDestroy
    public void setEmpty() throws MqttException {
        // 断开连接
        log.info("释放mqtt设备连接!");
        mqttClient.disconnect();
        // 关闭客户端
        mqttClient.close();
    }

    public void run() {
        try {
            // host为主机名，clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，
            // MemoryPersistence设置clientid的保存形式，默认为以内存保存，就用username
            mqttClient = new MqttClient(serverURL, clientId, new MemoryPersistence());
            // 配置参数信息
            MqttConnectOptions options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
            // 这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            // 设置用户名
            options.setUserName(mqttUsername);
            // 设置密码
            options.setPassword(mqttPassWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
//            options.setKeepAliveInterval(20);
            //设置断开后重新连接
            options.setAutomaticReconnect(true);

            mqttClient.setCallback(new PushCallback());

            MqttTopic topic = mqttClient.getTopic(mqttTopic);
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            options.setWill(topic, "close".getBytes(), 1, true);

            // 连接
            mqttClient.connect(options);

            // 订阅
            //如果监测到有,号，说明要订阅多个主题
            int Qos  = 1;
            if(mqttTopic.contains(",")){
                //多主题
                String[] mqttTopics = mqttTopic.split(",");
                for (int i = 0; i < mqttTopics.length; i++) {
                    subscribeTopic(mqttTopics[i],Qos);
                }

            }else{
                //单主题
                subscribeTopic(mqttTopic,Qos);
            }

            mqttClients.put(serverURL,mqttClient);

        } catch (Exception e) {
            e.printStackTrace();
            //当创建客户端的时候出现 已断开连接，有可能是在另一个环境下启动了该客户端，直接吧这边的客户端关闭，不然另一边会无限重连
            if(e.getMessage().equals("已断开连接") || e.getMessage().equals("客户机未连接")){
                try {
                    mqttClient.close();
                } catch (MqttException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 订阅主题
     * @param topic 主题
     * @param qos 消息质量
     */
    public void subscribeTopic(String topic, int qos){
        if (mqttClient != null && mqttClient.isConnected() && topic != null){
            try {
                mqttClient.subscribe(topic, qos);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("订阅主题失败！mqttClient == null || mqttClient.isConnected() == false");
        }
    }

    /**
     * 取消订阅主题
     * @param topic 主题名称
     */
    public void cleanTopic(String topic){
        if (mqttClient != null && mqttClient.isConnected()){
            try {
                mqttClient.unsubscribe(topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("取消订阅失败！");
        }
    }


//    public static void main(String[] args) throws MqttException {
//
//        String serverURL = "tcp://127.0.0.1:1883";
//        String mqttTopic = "home/garden/fountain";
//        String clientId = "client11";
//        //产品id
//        String productId = "";
//
//        String mqttUsername = "admin";
//        String mqttPassWord = "Wang.285652";
//        MqttClientThread client = new MqttClientThread(serverURL,mqttUsername,mqttPassWord,mqttTopic,clientId,productId);
//        client.run();
//    }
}
