package com.zc.websocket.handler;

import com.zc.websocket.core.WebSocketServer;
import com.zc.websocket.handler.heartbeat.HeartbeatDetector;

import java.util.Timer;

/**
 * WebSocket 启动器
 * @author Athena-xiepufeng
 */
public class WebSocketStarter
{

    private int port = 7789;
    private String path = "/ws";
    private String password = "zc&ws123";
    private int interval = 60;
    private int timeoutIntervals = 5;

    public WebSocketStarter(int port,
                            String path,
                            String password,
                            int interval,
                            int timeoutIntervals) {
        this.port = port;
        this.path = path;
        this.password = password;
        this.interval = interval;
        this.timeoutIntervals = timeoutIntervals;
    }

    public void run()
    {
        try
        {

            /* start 心跳检查*/
            Timer timer = new Timer();

            HeartbeatDetector heartbeatDetector = new HeartbeatDetector(
                    interval,
                    timeoutIntervals);

            int interval = heartbeatDetector.getInterval() * 1000;

            timer.schedule(heartbeatDetector, interval, interval);

            /* end 心跳检查*/

            /* start WebSocket 服务启动 */
            WebSocketServer webSocketServer = new WebSocketServer(
                    port,
                    path,
                    password);

            webSocketServer.run();

            /* end WebSocket 服务启动 */
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
