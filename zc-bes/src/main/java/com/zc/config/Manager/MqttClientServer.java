package com.zc.config.Manager;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.ThreadPool.ThreadPool;
import com.zc.connect.MQTTClient.config.MQTTClientConfig;
import com.zc.connect.MQTTClient.config.MQTTClientConfigAttr;
import com.zc.efounder.JEnterprise.commhandler.MqttClientCallBack;
import com.zc.efounder.JEnterprise.domain.baseData.Product;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description: mqtt消费者客户端
 * author: sunshangeng
 * date:2023/3/14 17:14
 */
@Slf4j
@Component
@EnableScheduling
public class MqttClientServer implements ApplicationRunner, DisposableBean {

//    @Value("${mqtt.username}")
//    private String username;
//
//    @Value("${mqtt.password}")
//    private String password;
//
//    @Value("${mqtt.url}")
//    private String hostUrl;
//
//    @Value("${mqtt.client.consumerId}")
//    private String clientId;
//
//    @Value("${mqtt.default.topic}")
//    private String defaultTopic;

    //是否启用mqtt
    @Value("${mqtt.enable}")
    private Boolean enable;

    @Resource
    private MQTTClientConfig mqttClientConfig;

    @Resource
    private RedisCache redisCache;
    /**
     * 客户端对象
     */
    public static List<MqttClient> clientList = new ArrayList<>();

    /**
     * 在bean初始化后连接到服务器
     */
//    @PostConstruct
    public void init() {
        if (enable) {
            ThreadPool.executor.execute(() -> {
                connect();
                initSubTopic();
            });
        }

    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (enable) {
            ThreadPool.executor.execute(() -> {
                connect();
                initSubTopic();
            });
        }

    }

    /**
     * 关闭MQTT连接
     */
    @PreDestroy
    public void setEmpty() throws MqttException {
        log.info("释放设备连接!");
        //清空连接缓存
        if (clientList.size() == 0) {
            return;
        }
        for (MqttClient mqttClient : clientList) {
            mqttClient.disconnect();
            mqttClient.close();
        }
        log.info("释放设备连接");
    }

    /**
     * @description:初始化订阅设备主题
     * @author: sunshangeng
     * @date: 2023/3/16 11:15
     * @param: []
     * @return: void
     **/
    public void initSubTopic() {

        /**根据数据项处理*/
        Map<String, ProductItemData> productItemDataMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);
        productItemDataMap.values().stream()
                .filter(item -> StringUtils.isNotBlank(item.getSubscribeAddress()))
                .forEach(item -> {
                    Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, item.getProductId());
                    /**只处理启用的产品和mqtt协议的产品   */
                    if (product.getState().equals("1") && product.getCommunicationProtocol().equals("1")) {
                        try {
                            sub("1", item.getSubscribeAddress());
                        } catch (MqttException e) {
                            log.error("--------数据项id：" + item.getId() + "中topic:" + item.getSubscribeAddress() + "初始化订阅时失败", e);
                            e.printStackTrace();
                        }
                    }
                });
        log.error("--------mqtt初始化订阅成功--------------");


//        Map<String, Product> productCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product);
//        productCache.values().stream()
//                .filter(item -> item.getState().equals("1"))//产品启用
//                .filter(item -> item.getCommunicationProtocol().equals("1"))//消息协议为mqtt
//                .filter(item -> item.getMessageProtocol() != null)//绑定的设备协议不为空
//                .forEach(item -> {
//                    /**获得未停用设备 mqtt订阅地址*/
//
//                    Agreement agreement = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Agreement, item.getMessageProtocol());
//                    if (agreement != null && StringUtils.isNotBlank(agreement.getMqttAddress())) {
//                        String[] mqttArray = agreement.getMqttAddress().split(",");
//                        /**循环订阅mqtt*/
//                        for (String Topic : mqttArray) {
//                            try {
//                                sub(toString());
//                            } catch (MqttException e) {
//                                log.error("--------协议id：" + agreement.getId() + "中topic:" + Topic + "初始化订阅时失败", e);
//                                e.printStackTrace();
//                            }
//                        }
//                    } else {
//                        log.error("-------产品id:" + item.getId() + "绑定的mqtt协议不正确或为空！");
//                    }
//                });
    }


    /**
     * 客户端连接服务端
     */
    public void connect() {
        try {
            //获取所有的mqtt配置项
            List<MQTTClientConfigAttr> mqttClientConfigAttrList = mqttClientConfig.getList();

            for (int i = 0; i < mqttClientConfigAttrList.size(); i++) {


                String clientId = "zckj_" + i;

                //是否已经连接
                Boolean connect = false;
                for (MqttClient mqttClients : clientList) {
                    if (clientId.equalsIgnoreCase(mqttClients.getClientId())) {
                        connect = true;
                    }
                }
                if (connect) {
                    continue;
                }

                MqttClient mqttClient = new MqttClient(mqttClientConfigAttrList.get(i).getUrl(), clientId, new MemoryPersistence());

                //连接设置
                MqttConnectOptions options = new MqttConnectOptions();
                //是否清空session，设置为false表示服务器会保留客户端的连接记录，客户端重连之后能获取到服务器在客户端断开连接期间推送的消息
                //设置为true表示每次连接到服务端都是以新的身份
                options.setCleanSession(true);
                //设置连接用户名
                options.setUserName(mqttClientConfigAttrList.get(i).getUsername());
                //设置连接密码
                options.setPassword(mqttClientConfigAttrList.get(i).getPassword().toCharArray());
                //设置超时时间，单位为秒
                options.setConnectionTimeout(10);
                //设置心跳时间 单位为秒，表示服务器每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
                options.setKeepAliveInterval(20);
                //设置遗嘱消息的话题，若客户端和服务器之间的连接意外断开，服务器将发布客户端的遗嘱信息
                options.setWill("willTopic", (clientId + "与服务器断开连接").getBytes(), 0, false);

                /*默认断线重连机制，不会重新订阅*/
//            options.setAutomaticReconnect(true);


                //设置回调
                mqttClient.setCallback(new MqttClientCallBack(clientId));
                mqttClient.connect(options);
                //订阅主题
                //消息等级，和主题数组一一对应，服务端将按照指定等级给订阅了主题的客户端推送消息
                int[] qos = {1};
                //主题
                String[] topics = {mqttClientConfigAttrList.get(i).getTopic()[0]};
                //订阅主题
                mqttClient.subscribe(topics, qos);

                clientList.add(mqttClient);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向某个主题发布消息 默认qos：1
     *
     * @param mqttClient:指定服务器
     * @param topic:发布的主题
     * @param msg：发布的消息
     */
    public void pub(String mqttClient, String topic, String msg) throws Exception {

        if (StringUtils.isBlank(topic)) {
            throw new ServiceException("传入的订阅主题为空");
        }
        if (StringUtils.isBlank(msg)) {
            throw new ServiceException("传入的消息为空");
        }

        if (clientList.size() == 0) {
            return;
        }
        for (MqttClient mqttClients : clientList) {

            if (mqttClient.equalsIgnoreCase(mqttClients.getClientId())) {
                if (!mqttClients.isConnected()) {
                    throw new ServiceException("mqtt链接不成功");
                }

                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(msg.getBytes());
                MqttTopic mqttTopic = mqttClients.getTopic(topic);
                MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
                token.waitForCompletion();
            }
        }

    }

    /**
     * 向某个主题发布消息
     *
     * @param mqttClient:指定服务器
     * @param topic:           发布的主题
     * @param msg:             发布的消息
     * @param qos:             消息质量    Qos：0、1、2
     */
    public void pub(String mqttClient, String topic, String msg, int qos) throws Exception {


        if (StringUtils.isBlank(topic)) {
            throw new ServiceException("传入的订阅主题为空");
        }
        if (StringUtils.isBlank(msg)) {
            throw new ServiceException("传入的消息为空");
        }

        if (clientList.size() == 0) {
            return;
        }
        for (MqttClient mqttClients : clientList) {

            if (mqttClient.equalsIgnoreCase(mqttClients.getClientId())) {
                if (!mqttClients.isConnected()) {
                    throw new ServiceException("mqtt链接不成功");
                }

                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(qos);
                mqttMessage.setPayload(msg.getBytes());
                MqttTopic mqttTopic = mqttClients.getTopic(topic);
                MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
                token.waitForCompletion();
            }
        }


    }

    /**
     * 订阅某一个主题 ，此方法默认的的Qos等级为：1
     *
     * @param mqttClient:指定服务器
     * @param topic            主题
     */
    public void sub(String mqttClient, String topic) throws MqttException {
        if (StringUtils.isBlank(topic)) {
            throw new ServiceException("传入的订阅主题为空");
        }

        if (clientList.size() == 0) {
            return;
        }

        for (MqttClient mqttClients : clientList) {

            if (mqttClient.equalsIgnoreCase(mqttClients.getClientId())) {
                if (!mqttClients.isConnected()) {
                    throw new ServiceException("当前mqtt服务未正常链接,mqtt链接不成功");
                }

                mqttClients.subscribe(topic);
            }
        }

    }

    /**
     * 订阅某一个主题，可携带Qos
     *
     * @param mqttClient:指定服务器
     * @param topic            所要订阅的主题
     * @param qos              消息质量：0、1、2
     */
    public void sub(String mqttClient, String topic, int qos) throws MqttException {
        if (StringUtils.isBlank(topic)) {
            throw new ServiceException("传入的订阅主题为空");
        }

        if (clientList.size() == 0) {
            return;
        }

        for (MqttClient mqttClients : clientList) {

            if (mqttClient.equalsIgnoreCase(mqttClients.getClientId())) {
                if (!mqttClients.isConnected()) {
                    throw new ServiceException("mqtt链接不成功");
                }

                mqttClients.subscribe(topic, qos);
            }
        }

    }


    /**
     * 取消订阅主题
     *
     * @param mqttClient:指定服务器
     * @param topic            主题名称
     */
    public void cleanTopic(String mqttClient, String topic) throws Exception {
        if (StringUtils.isBlank(topic)) {
            throw new ServiceException("传入的订阅主题为空");
        }
        if (clientList.size() == 0) {
            return;
        }

        for (MqttClient mqttClients : clientList) {

            if (mqttClient.equalsIgnoreCase(mqttClients.getClientId())) {
                if (!mqttClients.isConnected()) {
                    throw new ServiceException("mqtt链接不成功");
                }

                mqttClients.unsubscribe(topic);
            }
        }

    }


    /**
     * @description:mqtt 轮训任务，每次间隔二分钟重新连接
     * @author: sunshangeng
     * @date: 2023/3/20 15:36
     * @param: []
     * @return: void
     **/
    @Scheduled(cron = "0 */1 * * * ?")
    public void reConnect() {
//        log.info("检测是否在线！！！！");
        init();

    }


}
