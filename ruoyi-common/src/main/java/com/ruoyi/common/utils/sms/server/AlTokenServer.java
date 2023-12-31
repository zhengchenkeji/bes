package com.ruoyi.common.utils.sms.server;

import com.alibaba.nls.client.AccessToken;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.zc.common.constant.RedisKeyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * description:获取阿里巴巴的token
 * author: sunshangeng
 * date:2022/11/21 10:03
 */
@Component
public class AlTokenServer {
    private static final Logger log = LoggerFactory.getLogger(AlTokenServer.class);


    /**
     * 阿里云的  AccessKey ID
     */
    @Value("${alibaba.accessKeyId}")
    private String accessKeyId;
    /**
     * 阿里云的 AccessKey Secret
     */
    @Value("${alibaba.accessKeySecret}")
    private String accessKeySecret;

    @Resource
    private RedisCache redisCache;

    private String token;

    @PostConstruct
    public String getAlibabaToken() {
        /**token：ccd8b574469f4a078280f481583bc62c
         expireTime：1669126382*/
        if(true){return "";}
        String token = redisCache.getCacheObject(RedisKeyConstants.BES_BasicData_Albaba_Token);
        if (StringUtils.isNotEmpty(token)) {

            return token;
        }

        AccessToken accessToken = new AccessToken(accessKeyId, accessKeySecret);
        try {
            accessToken.apply();
        } catch (IOException e) {
            log.error("获取阿里巴巴token时报错：", e);
            e.printStackTrace();
            return null;
        }
        token = accessToken.getToken();
//       System.out.println("token："+token);
        long expireTime = accessToken.getExpireTime();
//       System.out.println("expireTime："+expireTime);
        /*将获取的时间戳转换为毫秒时间戳*/
        Long time = expireTime * 1000;
        long nowtime = System.currentTimeMillis();
        /*计算出当前需要存储的时间戳减去两个小时 获得需要存储的时间*/
        time = ((time - nowtime) / 1000) - (60 * 60 * 2);
        redisCache.setCacheObject(RedisKeyConstants.BES_BasicData_Albaba_Token, token, time.intValue(), TimeUnit.SECONDS);
        return token;
    }
}
