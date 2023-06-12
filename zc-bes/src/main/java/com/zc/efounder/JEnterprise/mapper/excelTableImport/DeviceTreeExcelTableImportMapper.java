package com.zc.efounder.JEnterprise.mapper.excelTableImport;

import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import com.zc.efounder.JEnterprise.domain.modulePoint.ModulePoint;
import com.zc.efounder.JEnterprise.domain.excelTableImport.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 18:49 2022/11/9
 * @Modified By:
 */
@Mapper
public interface DeviceTreeExcelTableImportMapper {
    /**
     * @Description: 添加到设备树节点
     * @auther: wanghongjie
     * @date: 14:21 2022/11/10
     * @param: [treeExcel]
     * @return: boolean
     */
    int add_deviceTree(DeviceTreeExcel treeExcel);

    /**
     * @Description: 添加到控制器节点
     * @auther: wanghongjie
     * @date: 17:58 2022/11/10
     * @param: [controllerExcel]
     * @return: java.lang.Boolean
     */
    Boolean add_controller(ControllerExcel controllerExcel);

    /**
     * @Description: 根据父节点名称获取其自增id
     * @auther: wanghongjie
     * @date: 9:18 2022/11/11
     * @param: [deviceTreeFatherId]
     * @return: com.ruoyi.deviceManagement.deviceTree.domain.DeviceTree
     */
    DeviceTree selectByPSysneme(String PsysName);

    /**
     * @Description: 如果模块的通信地址在当前ddc下有相同的地址, 则在页面提示通信地址重复, 添加失败
     * @auther: wanghongjie
     * @date: 10:21 2022/11/11
     * @param: [deviceTreeFatherId]
     * @return: java.util.List<com.ruoyi.deviceManagement.deviceTree.domain.DeviceTree>
     */
    List<Module> addrListByPName(int deviceTreeFatherId);

    /**
     * @param PsysName
     * @Description: 根据模块的父节点名称获取控制器的信息
     * @auther: wanghongjie
     * @date: 11:31 2022/11/11
     * @param: []
     * @return: com.ruoyi.deviceManagement.deviceTree.domain.Controller
     */
    Controller queryControllerMapByModelPSysName(String PsysName);

    /**
     * @Description: 添加线路/耦合器到athena_bes_build_node表中
     * @auther: wanghongjie
     * @date: 15:37 2022/11/11
     * @param: [buildNode]
     * @return: boolean
     */
    boolean add_buildNode(BuildNode buildNode);

    /**
     * @Description: 添加模块信息
     * @auther: wanghongjie
     * @date: 15:45 2022/11/11
     * @param: [moduleExcel]
     * @return: java.lang.Boolean
     */
    Boolean add_Module(ModuleExcel moduleExcel);

    /**
     * @Description: 根据模块型号获取当前模块的点集合
     * @auther: wanghongjie
     * @date: 16:00 2022/11/11
     * @param: [moduleExcel]
     * @return: java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     */
    List<Map<String, Object>> selectModulePoint(ModuleExcel moduleExcel);

    /**
     * @Description: 获取模块点类型信息
     * @auther: wanghongjie
     * @date: 16:10 2022/11/11
     * @param: []
     * @return: java.util.List<com.ruoyi.deviceManagement.modulePoint.domain.ModulePoint>
     */
    List<ModulePoint> getModulePointTypeInfo();

    /**
     * @param controllerId
     * @Description: 查询当前点位最大设备ID
     * @auther: wanghongjie
     * @date: 16:41 2022/11/11
     * @param: []
     * @return: java.util.List<com.ruoyi.deviceManagement.deviceTree.domain.Point>
     */
    List<Point> getPointList(int controllerId);

    /**
     * @Description: 点位添加到设备树表
     * @auther: wanghongjie
     * @date: 17:26 2022/11/11
     * @param: [nodeList]
     * @return: int
     */
    int batchInsert(@Param("list") List<DeviceTreeExcel> nodeList);

    /**
     * @Description: 获取当前模块下所有的点位
     * @auther: wanghongjie
     * @date: 17:45 2022/11/11
     * @param: [sysName]
     * @return: java.util.List<com.ruoyi.deviceManagement.deviceTree.domain.DeviceTree>
     */
    List<DeviceTree> selectPointByPSysneme(Integer deviceTreeFatherId);

    /**
     * @Description: 点位添加到点位表
     * @auther: wanghongjie
     * @date: 17:58 2022/11/11
     * @param: [nodePointList]
     * @return: int
     */
    int batchInsertPoint(@Param("list") List<PointExcel> nodePointList);

    /**
     * @Description: 根据父节点名称查询数据库中是否有这个名称
     * @auther: wanghongjie
     * @date: 16:52 2022/11/15
     * @param: [deviceTreeFatherId]
     * @return: com.ruoyi.deviceManagement.deviceTree.domain.DeviceTree
     */
    DeviceTree selectSbdyByPsysName(String PsysName);

    /**
     *
     * @Description: 查询该节点的父节点的类型
     *
     * @auther: wanghongjie
     * @date: 16:59 2022/11/15
     * @param: [deviceTreeFatherId]
     * @return: java.lang.String
     *
     */
    String selectPSysNameType(String PsysName);

    /**
     *
     * @Description: 查询当前模块下所有的点位
     *
     * @auther: wanghongjie
     * @date: 17:27 2022/11/15
     * @param: [deviceTreeFatherId]
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     *
     */
    List<DeviceTree> selectModule_pointList(Integer PsysName);


    /**
     *
     * @Description: 根据名称查询当前点位在设备树表的信息
     *
     * @auther: wanghongjie
     * @date: 18:25 2022/11/15
     * @param: [name]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     *
     */
    DeviceTree selectPointBySysName(String name);

    /**
     *
     * @Description: 判断当前点位的通道索引是否符合模块的模块型号,首先查出模块的点集合
     *
     * @auther: wanghongjie
     * @date: 18:31 2022/11/15
     * @param: [deviceTreeFatherId]
     * @return: java.lang.String
     *
     */
    String selectF_POINT_TYPE_CL(int deviceTreeId);

    /**
     *
     * @Description: 修改设备配置的点位信息
     *
     * @auther: wanghongjie
     * @date: 10:09 2022/11/16
     * @param: [idBySbdyStruct, sys_name_old, type]
     * @return: java.lang.Boolean
     *
     */
    Boolean updateStructPoint(@Param("idBySbdyStruct") Integer idBySbdyStruct,@Param("sys_name_old") String sys_name_old,@Param("type") String type);

    /**
     *
     * @Description: 修改点位信息到相应的点位表中
     *
     * @auther: wanghongjie
     * @date: 10:19 2022/11/16
     * @param: [point]
     * @return: java.lang.Boolean
     *
     */
    Boolean updatePointMap(PointExcel point);

    /**
     *
     * @Description: 添加虚点点位信息到相应的点位表中
     *
     * @auther: wanghongjie
     * @date: 11:09 2022/11/18
     * @param: [point]
     * @return: java.lang.Boolean
     *
     */
    Boolean addPointMap(PointExcel point);

    /**
     *
     * @Description: 根据系统名称获取节点信息
     *
     * @auther: wanghongjie
     * @date: 18:51 2022/11/21
     * @param: [psysName]
     * @return: com.ruoyi.deviceManagement.deviceTree.domain.BuildNode
     *
     */
    BuildNode selectBuildNode(String sysName);

    /**
     *
     * @Description: 添加电表信息
     *
     * @auther: wanghongjie
     * @date: 18:55 2022/11/21
     * @param: [ammeterExcel]
     * @return: boolean
     *
     */
    boolean add_structAmmeter(AmmeterExcel ammeterExcel);

    /**
     *
     * @Description: 添加电表总线节点
     *
     * @auther: wanghongjie
     * @date: 20:09 2022/11/21
     * @param: [buildNode]
     * @return: boolean
     *
     */
    boolean add_ammeterBus(BuildNode buildNode);

    /**
     *
     * @Description: 查询当前电表父节点的端口号
     *
     * @auther: wanghongjie
     * @date: 20:16 2022/11/21
     * @param: [psysName]
     * @return: com.ruoyi.deviceManagement.deviceTree.domain.BuildNode
     *
     */
    Bus selectAmmeterBus(String id);
}
