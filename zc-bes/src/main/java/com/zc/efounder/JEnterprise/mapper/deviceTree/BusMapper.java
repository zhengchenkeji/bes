package com.zc.efounder.JEnterprise.mapper.deviceTree;

import java.util.List;
import java.util.Map;

import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.deviceTree.Bus;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import org.apache.ibatis.annotations.Param;

/**
 * 总线Mapper接口
 * 
 * @author ruoyi
 * @date 2022-09-15
 */
public interface BusMapper 
{
    /**
     * 查询总线
     * 
     * @param deviceTreeId 总线主键
     * @return 总线
     */
    public Bus selectBusByDeviceTreeId(String deviceTreeId);

    /**
     * 查询总线列表
     * 
     * @param bus 总线
     * @return 总线集合
     */
    List<Bus> selectBusList(Bus bus);

    /**
     * 新增设备树
     *
     * @param bus 总线
     * @return 结果
     */
    boolean insertDeviceTree(Bus bus);

    /**
     * 新增总线
     * 
     * @param bus 总线
     * @return 结果
     */
    boolean insertBus(Bus bus);

    /**
     * 修改总线
     * 
     * @param bus 总线
     * @return 结果
     */
    boolean updateBus(Bus bus);

    /**
     * 删除总线
     * 
     * @param deviceTreeId 总线主键
     * @return 结果
     */
    boolean deleteBusByDeviceTreeId(String deviceTreeId);

    /**
     * 批量删除总线
     * 
     * @param deviceTreeIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteBusByDeviceTreeIds(String[] deviceTreeIds);

    /**
     * 查询父节点下的端口是否占用
     *
     * @param bus 总线
     * @return 结果
     */
    String queryRepeatPort(Bus bus);

    /**
     * 根据父节点id查询名称
     *
     * @param fatherId 父节点id
     * @return 结果
     */
    Map<String,Object> queryFatherSysName(String fatherId);

    /**
     * 根据父节点id查出该节点下 最大的总线名称
     *
     * @param fatherId 父节点id
     * @return 结果
     */
    String queryMaxBusName(String fatherId);

    /**
     * 查询总线下有无电表配置
     *
     * @param fatherId 父节点id
     * @return 结果
     */
    List<DeviceTree> queryMeterList(String fatherId);

    /**
     * 删除设备树节点
     *
     * @param deviceTreeId 设备树id
     * @return 结果
     */
    boolean deleteDeviceTree(String deviceTreeId);

    /**
     *
     * @author liuwenge
     * @param deviceTreeId 设备树id
     * @return com.ruoyi.deviceManagement.deviceTree.domain.DeviceTree
     */
    DeviceTree selectDeviceTreeByDeviceTreeId(String deviceTreeId);

    /**
     * @Description 查询总线下的所有电表
     *
     * @author liuwenge
     * @date 2022/9/26 14:29
     * @param deviceTreeId
     * @return java.util.List<com.ruoyi.deviceManagement.deviceTree.domain.AthenaElectricMeter>
     */
    List<AthenaElectricMeter> selectMeterByBus(String deviceTreeId);

    /**
     * 修改总线下的电表端口
     *
     * @author liuwenge
     * @date 2022/9/23 16:49
     * @param meterList 电表列表
     * @param port 端口
     * @return boolean
     */
    boolean updateMeterPort(@Param("meterList") List<AthenaElectricMeter> meterList, @Param("port") String port);


    Bus selectBusInfoByDeviceTreeId(String deviceTreeId);

}
