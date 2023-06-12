package com.zc.efounder.JEnterprise.service.deviceTree;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.deviceTree.dto.AthenaElectricMeterDto;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;

/**
 * 电表信息Service接口
 *
 * @author 孙山耕
 * @date 2022-09-14
 */
public interface AthenaElectricMeterService
{
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
     * @param energyCode 能源代码
     * @param parkCode   园区代码
     * @return {@code List<AthenaElectricMeterDto> }
     * @Author qindehua
     * @Date 2023/02/24
     **/

    List<AthenaElectricMeterDto> selectAthenaElectricMeterListInfo(String energyCode,String parkCode);

    /**
     * 新增电表信息
     *
     * @param athenaElectricMeter 电表信息
     * @return 结果
     */
    AjaxResult insertAthenaElectricMeter(AthenaElectricMeter athenaElectricMeter);

    /**
     * 修改电表信息
     *
     * @param athenaElectricMeter 电表信息
     * @return 结果
     */
    AjaxResult updateAthenaElectricMeter(AthenaElectricMeter athenaElectricMeter);

    /**
     * 批量删除电表信息
     *
     * @param meterIds 需要删除的电表信息主键集合
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
     * 查询电表字典
     *
     * @return 电表信息字典集合
     */
    List<AthenaElectricMeter> selectAthenaElectricMeterDicData();




    /**
     * @Description 同步电表
     *
     * @author liuwenge
     * @date 2022/9/23 8:51
     * @param athenaElectricMeter 电表信息
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult syncMeter(AthenaElectricMeter athenaElectricMeter);

    /**
     * @Description 电表数据对比
     *
     * @author liuwenge
     * @date 2022/9/30 16:16
     * @param deviceTreeId 电表设备树id
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult getMeterInfoParam(String deviceTreeId);

    /**
     * @Description 获取电表电能参数
     *
     * @author liuwenge
     * @date 2022/10/8 9:41
     * @param deviceTreeId
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult getElectricParams(String deviceTreeId);

    /**
     * @Description 获取电表实时数据
     *
     * @author liuwenge
     * @date 2022/10/8 11:58
     * @param deviceTreeId
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult getMeterRealTimeData(String deviceTreeId);

    /**
     * @Description 获取历史能耗数据
     *
     * @author liuwenge
     * @date 2022/10/8 14:49
     * @param deviceTreeId 设备树id
     * @param selectDay 查询日期
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult getMeterHistoryData(String deviceTreeId,Integer selectDay);


    void testData(Integer meterId,Integer collectCount,String electricData);

    /**
     *
     * @Description: 刷新电表缓存
     *
     * @auther: wanghongjie
     * @date: 16:41 2022/11/18
     * @param:
     * @return:
     *
     */
    void addMeterCache();

}
