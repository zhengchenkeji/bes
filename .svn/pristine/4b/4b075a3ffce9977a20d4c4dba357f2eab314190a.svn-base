package com.ruoyi;

import com.zc.datareported.service.DataReportActuator;
import com.zc.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.zc.efounder.JEnterprise.commhandler.SyncTimeBatchHandler;
import com.zc.common.core.ThreadPool.ThreadPool;
import com.zc.common.constant.RedisChannelConstants;
import com.zc.common.core.redis.pubsub.RedisPubSub;
import com.zc.connect.config.NettyServerConfigAttr;
import com.zc.connect.config.NettyServerConfig;
import com.zc.connect.nettyServer.ChildChannelHandler.heartbeat.HeartbeatDetector;
import com.zc.connect.nettyServer.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Timer;

/**
 * @auther Athena-YangChao
 * @date 2021/11/17/017
 * @apiNote 启动类
 */
@Component
public class RuoYiApplicationRunner  implements ApplicationRunner {

    @Autowired
    private NettyServerConfig nettyConfig;

    @Autowired
    private RedisPubSub redisPubSub;

    @Autowired
    private DataReportActuator dataReportActuator;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 消息订阅（设置属性，获取属性，服务调用）
        redisPubSub.subscribe(RedisChannelConstants.IOT_SET_ATTRIBUTE);
        redisPubSub.subscribe(RedisChannelConstants.IOT_GET_ATTRIBUTE);
        redisPubSub.subscribe(RedisChannelConstants.IOT_SERVE_INVOKE);
        redisPubSub.subscribe(RedisChannelConstants.IOT_SET_ATTRIBUTE_CHILD_DEVICE);
        redisPubSub.subscribe(RedisChannelConstants.IOT_GET_ATTRIBUTE_CHILD_DEVICE);
        redisPubSub.subscribe(RedisChannelConstants.IOT_SERVE_INVOKE_CHILD_DEVICE);
        redisPubSub.subscribe(RedisChannelConstants.TEST_SUB_DEMO);


        for (NettyServerConfigAttr configAttr : nettyConfig.getConfig()){

            if(configAttr.getEnabled()){
                String handler_name = configAttr.getHandler_name();
                Object handlerClass = Class.forName(handler_name).newInstance();
                // 线程开启
                ThreadPool.executor.execute(() -> {
                    try {


                        /*Start 启动心跳检测器*/
                        Timer timer = new Timer("SocketServer-server-timer", true);

                        HeartbeatDetector heartbeatDetector = new HeartbeatDetector(
                                configAttr.getHeartbeatInterval(),
                                configAttr.getHeartbeatTimeoutIntervals());

                        int interval = heartbeatDetector.getInterval() * 1000;

                        timer.schedule(heartbeatDetector, interval, interval);

                        // 启动netty服务
                        new NettyServer(configAttr.getPort(),handlerClass).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        Timer timer = new Timer(true);

        // 一小时执行一次清理订阅消息
        MsgSubPubHandler msgSubPubHandler = new MsgSubPubHandler();
        timer.schedule(msgSubPubHandler, MsgSubPubHandler.INTERVAL, MsgSubPubHandler.INTERVAL);

        // 一天同步一次所有控制器时间
        SyncTimeBatchHandler syncTimeBatchHandler = new SyncTimeBatchHandler();
        timer.schedule(syncTimeBatchHandler, SyncTimeBatchHandler.INTERVAL, SyncTimeBatchHandler.INTERVAL);


        // 数据上报执行器启动
        dataReportActuator.start();
    }
}
