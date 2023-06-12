package com.zc.websocket;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zc.websocket")
public class WebSocketConfigParam {

    private int port;

    private String path;

    private String password;

    private int interval;

    private int timeoutIntervals;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getTimeoutIntervals() {
        return timeoutIntervals;
    }

    public void setTimeoutIntervals(int timeoutIntervals) {
        this.timeoutIntervals = timeoutIntervals;
    }
}
