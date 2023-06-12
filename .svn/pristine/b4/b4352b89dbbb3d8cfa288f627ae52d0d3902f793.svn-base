package com.zc.websocket;

import com.google.auto.service.AutoService;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.zc.websocket.handler.WebsocketEvent;


/**
 * Websocket 事件处理
 * @author Athena-xiepufeng
 */
@AutoService(WebsocketEvent.class)
public class WebsocketEventHandle implements WebsocketEvent
{
    private final TokenService tokenService = SpringUtils.getBean(TokenService.class);

    @Override
    public String decryptToken(String tokenSN)
    {
        return tokenService.getTokenUuid(tokenSN);
    }

    /**
     * 判断 tokenSN 是否存在
     * @param tokenSN
     * @return
     */
    @Override
    public boolean isTokenSNExist(String tokenSN)
    {
        LoginUser user = tokenService.getLoginUser(tokenSN);

        return user != null;
    }

    /**
     * 心跳事件回调
     * 1.根据 tokenSN 获取用户信息
     * 2.刷新用户令牌有效期
     * @param tokenSN
     */
    @Override
    public void heartbeatEvent(String tokenSN)
    {
        LoginUser user = tokenService.getLoginUser(tokenSN);
        if (user == null) return;
        tokenService.refreshToken(user);
    }

    /**
     * 连接关闭回调
     * @param tokenSN
     */
    @Override
    public void channelRemovedEvent(String tokenSN)
    {

    }
}
