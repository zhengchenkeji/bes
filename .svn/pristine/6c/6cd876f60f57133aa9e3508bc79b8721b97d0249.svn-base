package com.zc.efounder.JEnterprise.mapper.systemSetting;

import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceRule;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 电价设置Mapper接口
 *
 * @author gaojikun
 * @date 2022-11-29
 */
public interface ElectricityPriceSettingMapper {

    /**
     * @param id
     * @return ElectricityPriceSetting
     * @Description: 查询电价设置
     * @auther: gaojikun
     */
    public ElectricityPriceSetting selectElectricityPriceSettingById(Long id);

    /**
     * @param electricityPriceSetting
     * @return List<ElectricityPriceSetting>
     * @Description: 查询电价设置列表
     * @auther: gaojikun
     */
    List<ElectricityPriceSetting> selectElectricityPriceSettingList(ElectricityPriceSetting electricityPriceSetting);

    List<ElectricityPriceSetting> CheckList(ElectricityPriceSetting electricityPriceSetting);

    /**
     * @param electricityPriceSetting
     * @return boolean
     * @Description: 新增电价设置
     * @auther: gaojikun
     */
    boolean insertElectricityPriceSetting(ElectricityPriceSetting electricityPriceSetting);

    /**
     * @param electricityPriceSetting
     * @return boolean
     * @Description: 修改电价设置
     * @auther: gaojikun
     */
    boolean updateElectricityPriceSetting(ElectricityPriceSetting electricityPriceSetting);

    /**
     * @param id
     * @return int
     * @Description: 删除电价设置信息
     * @auther: gaojikun
     */
    int deleteElectricityPriceSettingById(Long id);

    /**
     * @param ids
     * @return int
     * @Description: 删除电价设置信息
     * @auther: gaojikun
     */
    int deleteElectricityPriceSettingByIds(Long[] ids);


    /**
     * @param monthDay
     * @return Map<String, Object>
     * @Description: 根据时间获取季节id
     * @auther: gaojikun
     */
    Map<String, Object> querySeasonId(@Param("monthDay") String monthDay);

    /**
     * @param time
     * @return Map<String, Object>
     * @Description: 根据时间获取时间段
     * @auther: gaojikun
     */
    Map<String, Object> queryTimeId(@Param("time") String time);


    /**
     * @param seasonId
     * @param timeId
     * @return Map<String, Object>
     * @Description: 根据时间段和季节获取尖峰平谷字段
     * @auther: gaojikun
     */
    Map<String, Object> queryFieId(@Param("seasonId") String seasonId, @Param("timeId") String timeId);

    Map<String, Object> queryFieIdBySeasonTime(@Param("monthDay") String monthDay, @Param("time") String time);

    /**
     * @param yearMonth
     * @return Map<String, Object>
     * @Description: 根据尖峰平谷字段获取电价
     * @auther: gaojikun
     */
    Map<String, Object> queryElectricityPrice(@Param("yearMonth") String yearMonth);

    Map<String, Object> queryElectricityPriceLast();
}
