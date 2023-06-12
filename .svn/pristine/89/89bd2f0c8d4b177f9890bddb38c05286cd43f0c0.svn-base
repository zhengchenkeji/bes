package com.zc.efounder.JEnterprise.mapper.systemSetting;

import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceLink;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSeason;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceTime;

import java.util.List;
import java.util.Map;

/**
 * 分时电价Mapper接口
 * 
 * @author liuwenge
 * @date 2023-02-20
 */
public interface TimeOfUsePriceMapper
{

    /**
     * @Description 查询季节列表
     *
     * @author liuwenge
     * @date 2023/2/21 15:50
     * @param
     * @return java.util.List<com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSeason>
     */
    List<ElectricityPriceSeason> querySeasonList();

    /**
     * @Description 查询时间列表
     *
     * @author liuwenge
     * @date 2023/2/21 15:50
     * @param
     * @return java.util.List<com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceTime>
     */
    List<ElectricityPriceTime> queryTimeList();

    /**
     * @Description 查询分时电价配置
     *
     * @author liuwenge
     * @date 2023/2/21 15:50
     * @param
     * @return java.util.List<com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceLink>
     */
    List<ElectricityPriceLink> queryElectricityPriceLink();

    /**
     * @Description 删除分时电价配置
     *
     * @author liuwenge
     * @date 2023/2/21 15:50
     * @param electricityPriceLinkOld
     * @return java.lang.Boolean
     */
    Boolean delete(List<ElectricityPriceLink> electricityPriceLinkOld);

    /**
     * @Description 批量插入分时电价配置
     *
     * @author liuwenge
     * @date 2023/2/21 15:51
     * @param electricityPriceLinkList
     * @return java.lang.Boolean
     */
    Boolean insertByList(List<ElectricityPriceLink> electricityPriceLinkList);

    /**
     * @Description 导出时的查询分时电价配置
     *
     * @author liuwenge
     * @date 2023/2/21 15:51
     * @param
     * @return java.util.List<com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceLink>
     */
    List<ElectricityPriceLink> queryExportData();
}
