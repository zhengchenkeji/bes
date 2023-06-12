package com.zc.common.core.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.zc.common.constant.RedisStreamConstants;
import com.zc.common.core.redis.stream.RedisStream;
import com.zc.common.core.websocket.pojo.CmdMsg;
import com.zc.common.core.websocket.pojo.EventParam;
import com.zc.common.core.websocket.pojo.RedisWebSocketMsg;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

/**
 * 给客户端发送消息（发布消息到 redis）
 * @author Athena-xiepufeng
 */
@Service
public class WebSocketService
{

    private static final String EVENT = "event";

    private static final RedisStream redisStream = SpringUtils.getBean(RedisStream.class);

    /**
     * 给指定客户端发送消息
     * @param subEvent 事件名称
     * @param content 消息内容
     * @param <T>
     * @return
     */
    public static<T> boolean postEvent(String subEvent, T content)
    {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();

        if (loginUser == null)
        {
            return false;
        }

        String token = loginUser.getToken();

        if (token == null)
        {
            return false;
        }

        postEvent(token, subEvent, content);

        return true;
    }


    /**
     * 给指定客户端发送消息
     * @param subscriber 客户端标识（token）
     * @param subEvent 事件名称
     * @param content 消息内容
     * @param <T>
     * @return
     */
    public static<T> void postEvent(String subscriber, String subEvent, T content)
    {
        if (subscriber == null || subscriber.isEmpty() || subEvent == null || subEvent.isEmpty())
        {
            return;
        }

        EventParam<T> eventParam = new EventParam<>();
        eventParam.setSubEvent(subEvent);
        eventParam.setContent(content);

        CmdMsg<EventParam<T>> eventCmd = new CmdMsg<>();
        eventCmd.setMethod(EVENT);
        eventCmd.setParams(eventParam);

        RedisWebSocketMsg redisWebSocketMsg = new RedisWebSocketMsg();

        redisWebSocketMsg.setCmdMsg(eventCmd);
        redisWebSocketMsg.setSubscriber(subscriber);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Type type = new TypeToken<RedisWebSocketMsg>()
        {
        }.getType();

        String msg = gson.toJson(redisWebSocketMsg, type);

        redisStream.add(RedisStreamConstants.WebSocketDirectional.KEY, "", msg);
    }

    /**
     * 群发消息（广播）
     * @param subEvent 事件名称
     * @param content 消息内容
     * @param <T>
     * @return
     */
    public static<T> void broadcast(String subEvent, T content)
    {
        if (subEvent == null || subEvent.isEmpty())
        {
            return;
        }

        EventParam<T> eventParam = new EventParam<>();
        eventParam.setSubEvent(subEvent);
        eventParam.setContent(content);

        CmdMsg<EventParam<T>> eventCmd = new CmdMsg<>();
        eventCmd.setMethod(EVENT);
        eventCmd.setParams(eventParam);

        RedisWebSocketMsg redisWebSocketMsg = new RedisWebSocketMsg();

        redisWebSocketMsg.setCmdMsg(eventCmd);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Type type = new TypeToken<RedisWebSocketMsg>()
        {
        }.getType();

        String msg = gson.toJson(redisWebSocketMsg, type);

        redisStream.add(RedisStreamConstants.WebSocketStreamBroadcast.KEY, "", msg);

    }

}
