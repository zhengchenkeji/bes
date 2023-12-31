package com.zc.efounder.JEnterprise.mapper.baseData;

import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.EquipmentItemData;
import com.zc.efounder.JEnterprise.domain.baseData.HistoryItemData;
import com.zc.efounder.JEnterprise.domain.baseData.WarnItemData;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 物联设备Mapper接口
 *
 * @author gaojikun
 * @date 2023-03-08
 */
public interface EquipmentMapper {
    /**
     * 查询物联设备
     *
     * @param id 物联设备主键
     * @return 物联设备
     */
    public Equipment selectAthenaBesEquipmentById(Long id);

    public Equipment getInfoByCode(String code);

    /**
     * 查询物联设备子设备
     *
     * @param id 物联设备主键
     * @return 物联设备
     */
    List<Equipment> selectAthenaBesEquipmentSonList(Long id);

    /**
     * 查询物联设备详情
     *
     * @param id 物联设备主键
     * @return 物联设备详情
     */
    public Map<String, Object> selectAthenaBesEquipmentInfoById(Long id);

    HistoryItemData selectEquipmentLastData(HistoryItemData historyItemData);

    /**
     * 查询物联设备列表
     *
     * @param athenaBesEquipment 物联设备
     * @return 物联设备集合
     */
    List<Equipment> selectAthenaBesEquipmentList(Equipment athenaBesEquipment);

    /**
     * 查重物联设备
     *
     * @param athenaBesEquipment 物联设备
     * @return 物联设备集合
     */
    List<Equipment> selectAthenaBesEquipmentListCheck(Equipment athenaBesEquipment);

    /**
     * 新增物联设备
     *
     * @param athenaBesEquipment 物联设备
     * @return boolean
     */
    boolean insertAthenaBesEquipment(Equipment athenaBesEquipment);

    /**
     * 修改物联设备
     *
     * @param athenaBesEquipment 物联设备
     * @return boolean
     */
    boolean updateAthenaBesEquipment(Equipment athenaBesEquipment);

    /**
     * 删除物联设备
     *
     * @param id 物联设备主键
     * @return boolean
     */
    boolean deleteAthenaBesEquipmentById(Long id);

    /**
     * 删除所有物联子设备
     *
     * @param id 物联设备主键
     * @return boolean
     */
    boolean deleteSonAthenaBesEquipmentByIds(Long id);


    /**
     * 批量删除物联设备
     *
     * @param ids 需要删除的数据主键集合
     * @return boolean
     */
    boolean deleteAthenaBesEquipmentByIds(Long[] ids);

    /**
     * 查询物联设备历史数据
     *
     * @param ids 数据项id集合
     * @return boolean
     */
    List<HistoryItemData> selectItemDataHistoryListById(@Param("ids") List<Long> ids, @Param("startDate") Date startDate,
                                                        @Param("endDate") Date endDate, @Param("equipmentId") Long equipmentId);

    List<HistoryItemData> selectMemterHistoryListById(@Param("ids") List<Long> ids, @Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate, @Param("equipmentCode") String equipmentCode);

    List<WarnItemData> selectItemDataWarnDataListById(@Param("equipmentId") Long equipmentId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 新增历史数据
     *
     * @param historyItemData 历史数据
     * @return boolean
     */
    boolean insertHistoryData(HistoryItemData historyItemData);

    boolean insertHistoryDataList(@Param("lstDto") List<HistoryItemData> lstDto);

    /**
     * 新增报警数据
     *
     * @param warnItemData 报警数据
     * @return boolean
     */
    boolean insertWarnItemData(WarnItemData warnItemData);

    //有重命名的数据项获取重命名
    Map<String, String> selectNameByEquipment(@Param("equipmentId") Long equipmentId, @Param("itemDataId") Long itemDataId);

    /**
     *
     * @Description: 获取数据库中设备对应的数据项别名
     *
     * @auther: wanghongjie
     * @date: 14:41 2023/4/24
     * @param: [equipmentId]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.baseData.EquipmentItemData>
     *
     */
    List<EquipmentItemData> selectEquipmentItemDataList(Long equipmentId);

    /**
     *
     * @Description: 配置设备给数据项重命名
     * 
     * @auther: wanghongjie
     * @date: 15:09 2023/4/24 
     * @param: [equipmentItemDataList]
     * @return: void
     *
     */
    void insertAthenaBesEquipmentItemData(@Param("equipmentItemDataList") List<EquipmentItemData> equipmentItemDataList);

    /**
     *
     * @Description: 根据设备id删除数据项重命名数据
     *
     * @auther: wanghongjie
     * @date: 15:20 2023/4/24
     * @param: [id]
     * @return: java.lang.Boolean
     *
     */
    Boolean deleteAthenaBesEquipmentItemData(Long id);

    /**
     *
     * @Description: 获取所有的自定义数据项别名
     *
     * @auther: wanghongjie
     * @date: 15:03 2023/5/30
     * @param: []
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.baseData.EquipmentItemData>
     *
     */
    List<EquipmentItemData> selectEquipmentItemData();
}
