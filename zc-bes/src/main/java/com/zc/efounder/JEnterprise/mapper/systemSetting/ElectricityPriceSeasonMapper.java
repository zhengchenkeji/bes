package com.zc.efounder.JEnterprise.mapper.systemSetting;

import java.util.List;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSeason;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceTime;

/**
 * 电价-季节范围Mapper接口
 * 
 * @author liuwenge
 * @date 2023-02-20
 */
public interface ElectricityPriceSeasonMapper
{
    /**
     * @Description 查询全部季节
     *
     * @author liuwenge
     * @date 2023/2/23 10:49
     * @param
     * @return java.util.List<com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSeason>
     */
    List<ElectricityPriceSeason> selectAllSeason();

    /**
     * @Description 查询全部时间
     *
     * @author liuwenge
     * @date 2023/2/23 10:49
     * @param
     * @return java.util.List<com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceTime>
     */
    List<ElectricityPriceTime> selectAllTime();

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
    int insertElectricityPriceSeason(ElectricityPriceSeason electricityPriceSeason);

    /**
     * 修改电价-季节范围
     *
     * @author liuwenge
     * @param electricityPriceSeason 电价-季节范围
     * @return 结果
     */
    int updateElectricityPriceSeason(ElectricityPriceSeason electricityPriceSeason);

    /**
     * 删除电价-季节范围
     *
     * @author liuwenge
     * @param id 电价-季节范围主键
     * @return 结果
     */
    int deleteElectricityPriceSeasonById(Long id);

    /**
     * 批量删除电价-季节范围
     *
     * @author liuwenge
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteElectricityPriceSeasonByIds(Long[] ids);

    /**
     * @Description 删除季节下面的分时电价
     *
     * @author liuwenge
     * @date 2023/2/21 14:44
     * @param ids
     * @return int
     */
    int deleteTimeOfUsePrice(Long[] ids);
}
