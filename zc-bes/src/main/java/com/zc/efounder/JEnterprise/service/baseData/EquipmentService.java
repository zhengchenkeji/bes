package com.zc.efounder.JEnterprise.service.baseData;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.baseData.WarnItemData;
import com.zc.efounder.JEnterprise.domain.energyCollection.MeterType;

import java.util.List;
import java.util.Map;

/**
 * 物联设备Service接口
 *
 * @author gaojikun
 * @date 2023-03-08
 */
public interface EquipmentService
{
    /**
     * 查询物联设备
     *
     * @param id 物联设备主键
     * @return AjaxResult
     */
    public AjaxResult selectAthenaBesEquipmentById(Long id);

    /**
     * 查询物联设备详情
     *
     * @param id 物联设备主键
     * @return AjaxResult
     */
    public AjaxResult selectAthenaBesEquipmentInfoById(Long id);

    /**
     * 查询物联设备实时数据
     *
     * @param id 物联设备主键
     * @return AjaxResult
     */
    public AjaxResult selectAthenaBesEquipmentActualTimeById(Long id);

    /**
     * 获取物联设备是保存的数据项列表
     *
     * @param id 物联设备主键
     * @return AjaxResult
     */
    public AjaxResult getActualTimePreserve(Long id);


    /**
     * 查询物联设备实时数据
     *
     * @param e 物联设备
     * @return AjaxResult
     */
    public List<Map<String,Object>> selectItemDataHistoryListById(Equipment e);

    /**
     * 查询物联设备报警历史数据
     *
     * @param e 物联设备
     * @return AjaxResult
     */
    public List<WarnItemData> selectItemDataWarnDataListById(Equipment e);


    /**
     * 查询物联设备列表
     *
     * @param athenaBesEquipment 物联设备
     * @return 物联设备集合
     */
    List<Equipment> selectAthenaBesEquipmentList(Equipment athenaBesEquipment);

    /**
     * 新增物联设备
     *
     * @param athenaBesEquipment 物联设备
     * @return AjaxResult
     */
    AjaxResult insertAthenaBesEquipment(Equipment athenaBesEquipment);


    AjaxResult updateAthenaBesEquipmentOfflineAlarm(Equipment athenaBesEquipment);
    /**
     * 修改物联设备
     *
     * @param athenaBesEquipment 物联设备
     * @return AjaxResult
     */
    AjaxResult updateAthenaBesEquipment(Equipment athenaBesEquipment);

    /**
     * 批量删除物联设备
     *
     * @param ids 需要删除的物联设备主键集合
     * @return AjaxResult
     */
    AjaxResult deleteAthenaBesEquipmentByIds(Long[] ids);

    /**
     * 导入物联设备
     *
     * @param equipmentList 物联设备列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    public String importEquipment(List<Equipment> equipmentList, Boolean isUpdateSupport, String operName);

    /**
     *
     * @Description: 物联设备功能结构树
     *
     * @auther: wanghongjie
     * @date: 15:52 2023/3/30
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult equipmentFunctionTree();

    /**
     * @description:用于定时任务的第三方功能设备集合
     * @author: sunshangeng
     * @date: 2023/4/10 16:33
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult equipmentFunctionTreeByJob();

   /**
    * @description:用于查询关联支路时的设备
    * @author: sunshangeng
    * @date: 2023/4/17 11:26
    * @param: []
    * @return: com.ruoyi.common.core.domain.AjaxResult
    **/
    AjaxResult getEquipmentListByBranch(String parkCode,String code);

    /**
     *
     * @Description: 物联设备结构树
     *
     * @auther: gaojikun
     * @date: 15:52 2023/3/30
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult equipmentTree();

    /**
     * @description:获取数据项
     * @author: gaojikun
     * @param: [设备id]
     * @return: com.ruoyi.common.core.page.TableDataInfo
     **/
    List<ProductItemData> getElectricParamsDatalistOther(Long id);
    /**
     * @description:场景联动设备树
     * @author: sunshangeng
     * @date: 2023/4/17 11:26
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult getEquipmentListByScene();



}
