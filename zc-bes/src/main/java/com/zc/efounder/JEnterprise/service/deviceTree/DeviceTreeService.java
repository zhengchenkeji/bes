package com.zc.efounder.JEnterprise.service.deviceTree;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.deviceTree.RealTimeDataParam;
import com.zc.efounder.JEnterprise.domain.deviceTreeNode.AthenaDeviceNode;

import java.util.List;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:07 2022/9/8
 * @Modified By:
 */
public interface DeviceTreeService {
    //查询设备树列表
    public List<DeviceTree> selectDeviceTreeList(DeviceTree deviceTree);

    /**
     *
     * @Description: 获取所有的设备树节点
     *
     * @auther: wanghongjie
     * @date: 16:25 2022/10/24
     * @param: [DeviceTree]
     * @return: java.util.List<com.ruoyi.deviceManagement.deviceTree.domain.DeviceTree>
     *
     */
    AjaxResult selectDeviceTreeAllList();

    //根据节点类型查询相应的按钮
    List<AthenaDeviceNode> getButtonByTreeType(AthenaDeviceNode deviceNode);


    /**
     *
     * @Description: 获取当前点击节点的详细信息
     *
     * @auther: wanghongjie
     * @date: 11:20 2022/9/16
     * @param: [DeviceTree]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult getTreeNodeManage(DeviceTree deviceTree);

    /**
     *
     * @Description: 删除树节点操作
     *
     * @auther: wanghongjie
     * @date: 16:53 2022/9/16
     * @param: [DeviceTree]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult deleteTreeNode(DeviceTree deviceTree);

    /**
     *
     * @Description: 根据点位id获取点位实时值
     *
     * @auther: wanghongjie
     * @date: 15:25 2022/11/2
     * @param: [id]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */

    AjaxResult getRealTimeData(Long id, String pointType, Long equipmentId);

    /**
     *
     * @Description: 刷新缓存信息
     *
     * @auther: wanghongjie
     * @date: 16:29 2022/11/18
     * @param:
     * @return:
     *
     */
    void addDeviceTreeCache();

    AjaxResult getAllRealTimeData(List<RealTimeDataParam> realTimeDataParamList);
}
