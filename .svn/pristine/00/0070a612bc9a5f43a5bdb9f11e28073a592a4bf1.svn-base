package com.zc.efounder.JEnterprise.service.systemSetting;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceRule;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSetting;

/**
 * 电价设置Service接口
 *
 * @author gaojikun
 * @date 2022-11-29
 */
public interface ElectricityPriceSettingService
{
    /**
     * @param id
     * @return AjaxResult
     * @Description: 查询电价设置
     * @auther: gaojikun
     */
    public AjaxResult selectElectricityPriceSettingById(Long id);

    /**
     * @param electricityPriceSetting
     * @return List<ElectricityPriceSetting>
     * @Description: 查询电价设置列表
     * @auther: gaojikun
     */
    List<ElectricityPriceSetting> selectElectricityPriceSettingList(ElectricityPriceSetting electricityPriceSetting);

    /**
     * @param electricityPriceSetting
     * @return AjaxResult
     * @Description: 新增电价设置
     * @auther: gaojikun
     */
    AjaxResult insertElectricityPriceSetting(ElectricityPriceSetting electricityPriceSetting);

    /**
     * @param electricityPriceSetting
     * @return AjaxResult
     * @Description: 修改电价设置
     * @auther: gaojikun
     */
    AjaxResult updateElectricityPriceSetting(ElectricityPriceSetting electricityPriceSetting);

    /**
     * @param ids
     * @return AjaxResult
     * @Description: 批量删除电价设置
     * @auther: gaojikun
     */
    AjaxResult deleteElectricityPriceSettingByIds(Long[] ids);

    /**
     * @param id
     * @return AjaxResult
     * @Description: 删除电价设置信息
     * @auther: gaojikun
     */
    AjaxResult deleteElectricityPriceSettingById(Long id);
}
