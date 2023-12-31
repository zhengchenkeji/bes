package com.zc.efounder.JEnterprise.service.deviceTree;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.deviceTree.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 虚点。点位
 *
 * @author gaojikun
 * @date 2022-09-14
 */
public interface PointService {
    /**
     * @Description: 添加树节点
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:DeviceTree
     */
    AjaxResult insertDeviceTreee(BuildNode buildNode);

    /**
     * @Description: 修改树节点
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:deviceTree
     */
    AjaxResult updateDeviceTreee(BuildNode buildNode);

    /**
     * @Description: 查询虚点类型
     * @auther: gaojikun
     */
    AjaxResult listVpoint();

    /**
     * @Description: 添加虚点
     * @auther: gaojikun
     * @param:point
     * @return:AjaxResult
     */
    AjaxResult insertPoint(Point point);

    /**
     * @Description: 修改虚点
     * @auther: gaojikun
     * @param:point
     * @return:AjaxResult
     */
    AjaxResult updatePoint(Point point);

    /**
     * @description:添加照明子节点
     * @author: sunshangeng
     * @date: 2022/9/26 17:47
     * @param:
     * @return:
     **/
    AjaxResult insertLightingTree(BuildNode buildNode);



    /**
     * @description:修改照明子节点
     * @author: sunshangeng
     * @date: 2022/9/26 17:47
     * @param:
     * @return:
     **/
    AjaxResult updateLightingTree(BuildNode buildNode);

    /**
     * 调试一个逻辑点的初始值值
     * @param pointControlCommand
     * @return AjaxResult
     * @author: gaojikun
     */
    AjaxResult debugPointInfo(PointControlCommand pointControlCommand);


    /**
     * 点值配置
     * @param nodeConfigSets
     * @return AjaxResult
     * @author: gaojikun
     */
    AjaxResult debuggerEditPointValue(NodeConfigSet nodeConfigSets);

    /**
     * 查询点值配置
     * @param nodeConfigSets
     * @return AjaxResult
     * @author: gaojikun
     */
    AjaxResult selectEditPointValue(NodeConfigSet nodeConfigSets);


    /**
     *
     * @Description: 批量下发点位值
     *
     * @auther: wanghongjie
     * @date: 17:28 2022/11/3
     * @param: [list]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     * @param list
     */
    AjaxResult debugPointListInfo(List<PointControlCommand> list) throws NoSuchAlgorithmException;

    /**
     *
     * @Description: 刷新点位缓存
     *
     * @auther: wanghongjie
     * @date: 16:37 2022/11/18
     * @param:
     * @return:
     *
     */
    void initPointCache();

    /**
     *
     * @Description: 第三方协议下发获取实时值
     *
     * @auther: wanghongjie
     * @date: 17:33 2023/4/10
     * @param: [pointControlCommand]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult debugPointRealTimeInfo(PointControlCommand pointControlCommand) throws NoSuchAlgorithmException;

    /**
     *
     * @Description: 设计器批量获取实时值
     *
     * @auther: wanghongjie
     * @date: 14:30 2023/5/29
     * @param: [list]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult debugPointListRealTimeInfo(List<RealTimeDataParam> list);
}
