package com.zc.connect.business.handler;

import com.zc.connect.business.dto.ReceiveMsg;
import com.zc.efounder.JEnterprise.commhandler.PointDataResponse;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;

import java.util.List;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 11:16 2023/2/22
 * @Modified By:
 */
public interface ModbusMsgReceive {

    /**
     *
     * @param ip 控制器 ip 地址
     * @param state 在线状态 true 在线， false 离线
     */
    void controllerState(String ip, int post, Boolean state);

    /**
     *
     * @Description: 设备模块在线离线
     *
     * @auther: wanghongjie
     * @date: 15:33 2023/3/16
     * @param: [s]
     * @return: void
     *
     */
    void deviceState(String ip, int post, Long id, Boolean state);

    void sendRegistrationMessage(String s) throws Exception;

    /**
     *
     * @Description: 设备实时数据
     *
     * @auther: wanghongjie
     * @date: 16:01 2023/3/17
     * @param: [productItemData]
     * @return: void
     *
     */
    void deviceRealTimeData(List<ProductItemData> productItemData);

    /**
     *
     * @Description: 指令下发成功后推到前端
     *
     * @auther: wanghongjie
     * @date: 14:12 2023/3/20
     * @param:
     * @return:
     *
     * @param b
     */
    void sendMessageBoolen(boolean b);

    void getMassgaeState( String event,  ReceiveMsg<List<PointDataResponse>> msg);
}
