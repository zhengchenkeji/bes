package com.zc.efounder.JEnterprise.service.safetyWarning;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmHistoricalData;

/**
 * 告警历史数据Service接口
 *
 * @author qindehua
 * @date 2022-11-17
 */
public interface IAlarmHistoricalDataService
{
    /**
     * 查询告警历史数据
     *
     * @param id 告警历史数据主键
     * @return 告警历史数据
     */
    public AlarmHistoricalData selectAlarmHistoricalDataById(Long id);

    /**
     * 查询告警历史数据列表
     *
     * @param alarmHistoricalData 告警历史数据
     * @return 告警历史数据集合
     */
    List<AlarmHistoricalData> selectAlarmHistoricalDataList(AlarmHistoricalData alarmHistoricalData);

    /**
     * 新增告警历史数据
     *
     * @param alarmHistoricalData 告警历史数据
     * @return 结果
     */
    int insertAlarmHistoricalData(AlarmHistoricalData alarmHistoricalData);

    /**
     * 修改告警历史数据
     *
     * @param alarmHistoricalData 告警历史数据
     * @return 结果
     */
    int updateAlarmHistoricalData(AlarmHistoricalData alarmHistoricalData);

    /**
     * 批量删除告警历史数据
     *
     * @param ids 需要删除的告警历史数据主键集合
     * @return 结果
     */
    AjaxResult deleteAlarmHistoricalDataByIds(Long[] ids);

    /**
     * 删除告警历史数据信息
     *
     * @param id 告警历史数据主键
     * @return 结果
     */
    int deleteAlarmHistoricalDataById(Long id);
}
