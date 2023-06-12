package com.zc.efounder.JEnterprise.service.systemSetting;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSeason;
import com.zc.efounder.JEnterprise.domain.systemSetting.TimeOfUsePrice;

import java.util.List;
import java.util.Map;

/**
 * 电价-季节范围Service接口
 * 
 * @author liuwenge
 * @date 2023-02-20
 */
public interface TimeOfUsePriceService
{
    /**
     * 查询分时电价列表
     *
     * @author liuwenge
     * @return 分时电价列表
     */
    AjaxResult selectList();

    /**
     * @Description 修改分时电价
     *
     * @author liuwenge
     * @date 2023/2/21 15:49
     * @param timeOfUsePrice
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult update(TimeOfUsePrice timeOfUsePrice);

    /**
     * @Description 导出分时电价
     *
     * @author liuwenge
     * @date 2023/2/21 15:49
     * @param
     * @return java.util.Map<java.lang.String,java.util.List<java.util.Map<java.lang.String,java.lang.Object>>>
     */
    Map<String,List<Map<String,Object>>> exportTable();


}
