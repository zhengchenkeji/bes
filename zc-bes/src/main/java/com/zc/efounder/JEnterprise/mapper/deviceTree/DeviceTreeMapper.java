package com.zc.efounder.JEnterprise.mapper.deviceTree;

import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.deviceTreeNode.AthenaDeviceNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:08 2022/9/8
 * @Modified By:
 */
public interface DeviceTreeMapper {

    //查询设备树列表
    List<DeviceTree> selectDeviceTreeList(DeviceTree deviceTree);

    List<DeviceTree> selectDeviceTreeByRedis();

    /**
     * 根据树id 查询缓存
     *
     * @param deviceTreeId 设备树id
     * @return {@code DeviceTree }
     * @Author qindehua
     * @Date 2022/12/19
     **/
    DeviceTree selectDeviceTreeRedisById(Integer deviceTreeId);

    //根据节点类型查询相应的按钮
    List<AthenaDeviceNode> getButtonByTreeType(AthenaDeviceNode deviceNode);

    //添加设备树节点
    Boolean insertDeviceTreeNode(DeviceTree deviceTreeMsg);

    //根据系统名称获取创建的设备树节点
    DeviceTree selectDeviceTreeBySYSName(String sysName);


    //根据树 查询数据
    DeviceTree selectDeviceTreeByData(DeviceTree  deviceTree);



    /**
     * @Description:获取当前点击节点的详细信息
     * @auther: wanghongjie
     * @date: 11:49 2022/9/16
     * @param: [DeviceTree]
     * @return: java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     */
    Map<String, Object> getTreeNodeManage(DeviceTree deviceTree);

    //根据线路树Id查询模块树Id//根据总线查询线路
    List<DeviceTree> selectModelueTree(int treeId);

    //根据DDC id查询总线、虚点
    List<DeviceTree> selectBusVpointTree(int treeId);

    //删除树节点
    boolean deleteLineTree(int treeId);

    //删除控制器表
    boolean deleteControllerTree(int treeId);

    /**
     * 查询电表关联的支路
     * @param meterIdList 电表的设备树id
     *
     * @return 结果
     */
    int selectBranchMeterLink(@Param("meterIdList") List<String> meterIdList);

    /**
     * @param deviceTreeId 设备树id
     * @return java.lang.String
     * @Description 查询所属采集器ip
     * @author liuwenge
     * @date 2022/9/22 12:03
     */
    String selectControllerIP(String deviceTreeId);

    /**
     * 删除设备树节点
     *
     * @param deviceTreeId 设备树id
     * @return 结果
     */
    boolean deleteDeviceTree(String deviceTreeId);

    /**
     * 删除电表信息
     *
     * @param deviceTreeId 设备树id
     * @return 结果
     */
    boolean deleteAthenaElectricMeterByMeterId(String deviceTreeId);

    /**
     * 查询电表关联的报警策略
     *
     * @param deviceTreeId 设备树id
     * @return 结果
     */
    String[] selectAlarmTactics(String deviceTreeId);

    /**
     * 删除告警抑制
     *
     * @param alarmTacticsIds 告警策略ids
     * @return 结果
     */
    boolean deleteAlarmRestrainControl(String[] alarmTacticsIds);

    /**
     * 删除告警实时数据
     *
     * @param alarmTacticsIds 告警策略ids
     * @return 结果
     */
    boolean deleteAlarmRealtimeData(String[] alarmTacticsIds);

    /**
     * 删除告警历史数据
     *
     * @param alarmTacticsIds 告警策略ids
     * @return 结果
     */
    boolean deleteAlarmHistoricalData(String[] alarmTacticsIds);

    /**
     * 删除告警接受组关联
     *
     * @param alarmTacticsIds 告警策略ids
     * @return 结果
     */
    boolean deleteAlarmNotifierLink(String[] alarmTacticsIds);

    /**
     * 删除告警通知记录
     *
     * @param alarmTacticsIds 告警策略ids
     * @return 结果
     */
    boolean deleteAlarmNotificationRecord(String[] alarmTacticsIds);

    /**
     * 删除支路与电表关联关系
     *
     * @param meterId 电表id
     * @return 结果
     */
    boolean deleteBranchMeterLink(String meterId);

    /**
     * 删除能耗总线
     * @author liuwenge
     * @date 2022/9/28 14:14
     * @param deviceTreeId 总线的设备树id
     * @return boolean
     */
    boolean deleteBusById(String deviceTreeId);

    /**
     * 查询总线下所有的电表id
     * @author liuwenge
     * @date 2022/9/28 17:06
     * @param deviceTreeId 总线id
     * @return java.util.List<java.lang.String>
     */
    List<String> selectMeterList(String deviceTreeId);
    /**
     * 按id列表删除设备树
     * @author liuwenge
     * @date 2022/9/28 14:35
     * @param deviceTreeIdList 设备树id列表
     * @return boolean
     */
    boolean deleteDeviceTreeByList(@Param("deviceTreeIdList") List<String> deviceTreeIdList);

    /**
     * 按id列表删除电表
     * @author liuwenge
     * @date 2022/9/28 14:40
     * @param deviceTreeIdList 电表的设备树id列表
     * @return boolean
     */
    boolean deleteMeterByList(@Param("deviceTreeIdList") List<String> deviceTreeIdList);

    /**
     * 按id列表查询电表关联的报警策略
     * @param deviceTreeIdList 电表的设备树id列表
     * @return 结果
     */
    String[] selectAlarmTacticsByList(@Param("deviceTreeIdList") List<String> deviceTreeIdList);
    /**
     * @description:根据树id查询
     * @author: sunshangeng
     * @date: 2022/9/27 14:41
     * @param: string deviceid
     * @return:
     **/
    DeviceTree  getdeviceTreeByid(String devceid);


    /**
     * @description:查询照明下的子节点
     * @author: sunshangeng
     * @date: 2022/10/8 18:10
     * @param: [ControllerId]
     * @return: java.util.List<com.ruoyi.deviceManagement.deviceTree.domain.DeviceTree>
     **/
    List<DeviceTree> getLightingSubNode(String ControllerId);


    /**
     * @description:查询所有子节点
     * @author: sunshangeng
     * @date: 2022/10/8 18:10
     * @param: [ControllerId]
     * @return: java.util.List<com.ruoyi.deviceManagement.deviceTree.domain.DeviceTree>
     **/
    List<String> getSubNode(String deviceTreeId);


    /***
     * @description:根据任务设备树id删除数据
     * @author: sunshangeng
     * @param:
     * @return:
     **/
    Boolean deleteBySyncTreeIdBoolean(String treeid);

}
