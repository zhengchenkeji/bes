package com.zc.efounder.JEnterprise.service.systemSetting;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSeason;

/**
 * 电价-季节范围Service接口
 * 
 * @author liuwenge
 * @date 2023-02-20
 */
public interface ElectricityPriceSeasonService
{
    /**
     * 查询电价-季节范围
     *
     * @author liuwenge
     * @param id 电价-季节范围主键
     * @return 电价-季节范围
     */
    public ElectricityPriceSeason selectElectricityPriceSeasonById(Long id);

    /**
     * 查询电价-季节范围列表
     *
     * @author liuwenge
     * @param electricityPriceSeason 电价-季节范围
     * @return 电价-季节范围集合
     */
    List<ElectricityPriceSeason> selectElectricityPriceSeasonList(ElectricityPriceSeason electricityPriceSeason);

    /**
     * 新增电价-季节范围
     *
     * @author liuwenge
     * @param electricityPriceSeason 电价-季节范围
     * @return 结果
     */
    AjaxResult insertElectricityPriceSeason(ElectricityPriceSeason electricityPriceSeason);

    /**
     * 修改电价-季节范围
     *
     * @author liuwenge
     * @param electricityPriceSeason 电价-季节范围
     * @return 结果
     */
    AjaxResult updateElectricityPriceSeason(ElectricityPriceSeason electricityPriceSeason);

    /**
     * 批量删除电价-季节范围
     *
     * @author liuwenge
     * @param ids 需要删除的电价-季节范围主键集合
     * @return 结果
     */
    AjaxResult deleteElectricityPriceSeasonByIds(Long[] ids);

    /**
     * 删除电价-季节范围信息
     *
     * @author liuwenge
     * @param id 电价-季节范围主键
     * @return 结果
     */
    int deleteElectricityPriceSeasonById(Long id);
}
