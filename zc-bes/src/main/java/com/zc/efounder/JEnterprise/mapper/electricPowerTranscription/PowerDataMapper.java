package com.zc.efounder.JEnterprise.mapper.electricPowerTranscription;

import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.PowerData;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 用能概况Mapper接口
 *
 * @author gaojikun
 * @date 2022/11/9 10:49
 */
public interface PowerDataMapper {

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();


    /**
     * 查询支路下电表
     */
    List<Map<String,String>> getCheckMeterList(PowerData powerData);
    /**
     * 查询支路下第三方电表
     */
    List<Map<String,String>> getCheckMeterListEquipment(PowerData powerData);

    /**
     * 查询采集参数
     */
    List<Map<String,String>> getCheckMeterParamsList(@Param("ids") String[] ids);

    /**
     * 查询采集参数下分项
     */
    List<Map<String,Object>> getMeterParamsList(PowerData powerData);

    /**
     * 查询电表下采集参数
     */
    List<Map<String,String>> getMeterParams(PowerData powerData);

    /**
     * @Description 日原始数据
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String,Object>> queryDayChartsData(PowerData powerData);
    /**
     * @Description 第三方日原始数据
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String,Object>> queryDayChartsDataOther(PowerData powerData);

    /**
     * @Description 逐日极值数据
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String,Object>> queryMaxChartsData(PowerData powerData);

    /**
     * @Description 第三方逐日极值数据
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String,Object>> queryMaxChartsDataOther(PowerData powerData);
}