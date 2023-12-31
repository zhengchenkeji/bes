package com.zc.efounder.JEnterprise.mapper.deviceTree;

import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 点位Mapper接口
 *
 * @author gaojikun
 * @date 2022-09-15
 */
public interface PointMapper {
    /**
     * 查询点位
     *
     * @param guid 点位主键
     * @return 点位
     */
    public Point selectPointByGuid(Long guid);

    /**
     * 查询点位
     *
     * @param treeId 点位树ID
     * @return 点位
     */
    public Point selectPointByTreeId(Long treeId);

    /**
     * 查询点位列表
     *
     * @param point 点位
     * @return 点位集合
     */
    List<Point> selectPointList(Point point);

    /**
     * 查询虚点、总线、线路列表
     *
     * @return 点位集合
     */
    List<BuildNode> selectBuildNodeList();

    /**
     * 查询虚点、总线、线路信息
     *
     * @param deviceTreeId 设备树id
     * @return {@code BuildNode }
     * @Author qindehua
     * @Date 2022/12/29
     **/
    BuildNode selectBuildNodeByDeviceTreeId(@Param("deviceTreeId") Long deviceTreeId);

    /**
     * 根据总线id查询线路列表
     *
     * @return 点位集合
     */
    List<DeviceTree> selectBuildNodeListByBusId(BuildNode buildNode);

    /**
     * 根据端口查询线路
     *
     * @return 点位集合
     */
    BuildNode selectBuildNodeByPort(BuildNode buildNode);

    /**
     * 根据treeId查询
     *
     * @return 点位集合
     */
    BuildNode selectBuildNodeByTreeId(BuildNode buildNode);

    /**
     * 新增点位
     *
     * @param point 点位
     * @return 结果
     */
    boolean insertPoint(Point point);

    /**
     * 修改点位
     *
     * @param point 点位
     * @return 结果
     */
    boolean updatePoint(Point point);

    /**
     * 根据树ID 修改故障点
     *
     * @param treeId 树id
     * @param value  值
     * @return boolean
     * @Author qindehua
     * @Date 2022/10/28
     **/
    boolean updateFaultState(@Param("treeId") Long treeId,@Param("value") Integer value);

    /**
     * 修改点位-重置
     *
     * @param point 点位
     * @return 结果
     */
    boolean updatePointRest(Point point);

    /**
     * 删除点位
     *
     * @param guid 点位主键
     * @return 结果
     */
    int deletePointByGuid(Long guid);

    /**
     * 批量删除点位
     *
     * @param guids 需要删除的数据主键集合
     * @return 结果
     */
    int deletePointByGuids(Long[] guids);

    /**
     * @Description: 添加树节点
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:int
     */
    boolean insertDeviceTreee(DeviceTree deviceTree);

    /**
     * @Description: 查询点位表最大设备ID
     * @auther: gaojikun
     * @param:conId
     * @param:nodeType
     * @return:Map<String, Object>
     */
    Map<String, Object> selectPointMaxId(@Param("controllerId") int controllerId);

    /**
     * @Description: 添加楼控虚点、总线、线路
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:int
     */
    boolean insertBuildNode(BuildNode buildNode);

    /**
     * @Description: 修改楼控虚点、总线、线路
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:int
     */
    boolean updateBuildNode(BuildNode buildNode);


    /**
     * @Description: 修改照明干线 支线等问题
     * @auther: sunshangeng
     * @param:DeviceTree
     * @return:int
     */
    boolean updateBuildNodeByLighting(BuildNode buildNode);

    /**
     * @Description: 模块点系统名称查重
     * @auther: sunshangeng
     * @param:DeviceTree
     * @return:int
     */
    List<DeviceTree> selectDeviceTreeeCheckName(DeviceTree deviceTree);

    /**
     * @Description: 修改树节点
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:int
     */
    boolean updateDeviceTreee(DeviceTree deviceTree);

    /**
     * @Description: 重置树节点
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:int
     */
    boolean updateDeviceTreeeRest(DeviceTree deviceTree);

    /**
     * @Description: 根据树节点查询树节点
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:DeviceTree
     */
    DeviceTree selectDeviceTreeeByDeviceTreee(DeviceTree deviceTree);

    /**
     * @Description: 根据树节点查询树节点
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:DeviceTree
     */
    DeviceTree selectDeviceTreeeButton(DeviceTree deviceTree);

    /**
     * @Description: 删除点位
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:DeviceTree
     */
    boolean deletePointByTreeId(Integer treeId);

    /**
     * @Description: 删除设备树
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:DeviceTree
     */
    boolean deleteDeviceTreee(Integer treeId);

    /**
     * @Description: 删除点位
     * @auther: gaojikun
     * @param:DeviceTree
     * @return:DeviceTree
     */
    boolean deleteBuildNodeByTreeId(Integer treeId);

    /**
     * @Description: 根据父节点Id查询子节点数量和父节点类型
     * @auther: gaojikun
     * @param:String
     * @return:map
     */
    Map<String, Object> selectDeviceTreeeByFatherId(@Param("fatherId") int fatherId, @Param("nodeId") int nodeId);

    /**
     * @Description: 查询虚点类型
     * @auther: gaojikun
     */
    List<Map<String, Object>> listVpoint();

    /**
     * @Description: 修改同步状态
     * @auther: gaojikun
     * @param:point
     * @return:boolean
     */
    boolean updatePointSyncState(Point point);

    /**
     * @Description: 查询点值配置
     * @auther: gaojikun
     * @param:nodeConfigSet
     * @return:List<NodeConfigSet>
     */
    List<NodeConfigSet> selectNodeConfigSet(NodeConfigSet nodeConfigSet);

    /**
     * @Description: 查询所有点值配置
     * @auther: gaojikun
     * @param:nodeConfigSet
     * @return:List<NodeConfigSet>
     */
    List<NodeConfigSet> selectNodeConfigSetList(NodeConfigSet nodeConfigSet);

    /**
     * @Description: 查重点值配置
     * @auther: gaojikun
     * @param:nodeConfigSet
     * @return:List<NodeConfigSet>
     */
    List<NodeConfigSet> selectNodeConfigSetCheck(NodeConfigSet nodeConfigSet);

    /**
     * @Description: 新增点值配置
     * @auther: gaojikun
     * @param:nodeConfigSet
     * @return:boolean
     */
    boolean insertNodeConfigSet(NodeConfigSet nodeConfigSet);

    /**
     * @Description: 修改点值配置
     * @auther: gaojikun
     * @param:nodeConfigSet
     * @return:boolean
     */
    boolean updateNodeConfigSet(NodeConfigSet nodeConfigSet);

    /**
     * @Description: 删除点值配置
     * @auther: gaojikun
     * @param:nodeConfigSet
     * @return:boolean
     */
    int deletePointSettingByName(NodeConfigSet nodeConfigSet);

    /**
     * @Description: 新增模块错误日志
     * @auther: gaojikun
     * @param:moduleErrorLog
     * @return:boolean
     */
    boolean addModuleErrorLog(ModuleErrorLog moduleErrorLog);

    /**
     * @Description: 新增调试点位操作日志
     * @auther: gaojikun
     * @param:pointDebugLog
     * @return:boolean
     */
    boolean addDebugLog(PointDebugLog pointDebugLog);

}
