package com.zc.efounder.JEnterprise.mapper.deviceTree;

import java.util.List;
import java.util.Map;

import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.deviceTree.dto.AthenaElectricMeterDto;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import org.apache.ibatis.annotations.Param;

/**
 * 电表信息Mapper接口
 *
 * @author 孙山耕
 * @date 2022-09-14
 */
public interface AthenaElectricMeterMapper {
    /**
     * 查询电表信息
     *
     * @param meterId 电表信息主键
     * @return 电表信息
     */
    public AthenaElectricMeter selectAthenaElectricMeterByMeterId(Long meterId);

    /**
     * 查询电表信息列表
     *
     * @param athenaElectricMeter 电表信息
     * @return 电表信息集合
     */
    List<AthenaElectricMeter> selectAthenaElectricMeterList(AthenaElectricMeter athenaElectricMeter);

    /**
     * 查询电表信息列表 不分页
     *
     * @return 电表信息集合
     */
    List<AthenaElectricMeterDto> selectAthenaElectricMeterListInfo(@Param("energyCode") String energyCode);

    /**
     * 新增电表信息
     *
     * @param athenaElectricMeter 电表信息
     * @return 结果
     */
    Boolean insertAthenaElectricMeter(AthenaElectricMeter athenaElectricMeter);

    /**
     * 修改电表信息
     *
     * @param athenaElectricMeter 电表信息
     * @return 结果
     */
    boolean updateAthenaElectricMeter(AthenaElectricMeter athenaElectricMeter);

    /**
     * 删除电表信息
     *
     * @param deviceTreeId 设备树id
     * @return 结果
     */
    boolean deleteAthenaElectricMeterByMeterId(String deviceTreeId);

    /**
     * 批量删除电表信息
     *
     * @param meterIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAthenaElectricMeterByMeterIds(Long[] meterIds);

    /**
     * 获取采集方案列表
     *
     * @return 结果
     */
    List<CollMethod> getCollectionMethodList(String parkId);

    /**
     * 查询系统名称是否重复
     *
     * @return 结果
     */
    List<AthenaElectricMeter> queryAthenaElectricMeterSysName(AthenaElectricMeter athenaElectricMeter);

    /**
     * 查询父级所属园区
     *
     * @return java.lang.String
     */
    String selectPark(String fatherId);

    /**
     * 把电表插入设备树表
     *
     * @return 结果
     */
    boolean insertDeviceTree(AthenaElectricMeter athenaElectricMeter);

    /**
     * 删除设备树节点
     *
     * @param deviceTreeId 设备树id
     * @return 结果
     */
    boolean deleteDeviceTree(String deviceTreeId);


    /**
     * 查询电表字典
     *
     * @return 电表信息字典集合
     */
    List<AthenaElectricMeter> selectAthenaElectricMeterDicData();

    /**
     * 查询电表关联的支路
     *
     * @param deviceTreeId 设备树id
     * @return 结果
     */
    int selectBranchMeterLink(String deviceTreeId);

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
     * @param deviceTreeId 设备树id
     * @return java.lang.String
     * @Description 查询所属采集器ip
     * @author liuwenge
     * @date 2022/9/22 12:03
     */
    String selectControllerIP(String deviceTreeId);

    /**
     * @param deviceTreeId 设备树id
     * @return com.ruoyi.deviceManagement.deviceTree.domain.DeviceTree
     * @Description 查询设备树信息
     * @author liuwenge
     * @date 2022/9/23 11:42
     */
    DeviceTree selectDeviceTreeByDeviceTreeId(String deviceTreeId);

    /**
     * @param deviceTreeId
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Description 获取电表电能参数
     * @author liuwenge
     * @date 2022/10/8 11:23
     */
    List<Map<String, Object>> getElectricParams(String deviceTreeId);

    List<ElectricParams> selectElectricParams(String collId);

    AthenaElectricMeter selectAthenaElectricMeterByDeviceTreeId(String deviceTreeId);

}
