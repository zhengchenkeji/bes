package com.zc.common.core.rabbitMQ;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 14:57 2023/2/8
 * @Modified By:
 */
public class demo {


    private static MsgProducer msgProducer;

    public static void main(String[] args) {
        msgProducer.sendMsg("222");
    }
}
