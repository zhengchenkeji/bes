package com.zc.efounder.JEnterprise.mapper.safetyWarning;

import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmHistoricalData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 告警历史数据Mapper接口
 *
 * @author qindehua
 * @date 2022-11-17
 */
public interface AlarmHistoricalDataMapper
{
    /**
     * 查询告警历史数据
     *
     * @param id 告警历史数据主键
     * @return 告警历史数据
     */
    public AlarmHistoricalData selectAlarmHistoricalDataById(Long id);

    /**
     * 查询告警历史数据列表(有策略ID)
     *
     * @param alarmHistoricalData 告警历史数据
     * @return 告警历史数据集合
     */
    List<AlarmHistoricalData> selectAlarmHistoricalDataList(AlarmHistoricalData alarmHistoricalData);

    /**
     * 查询告警历史数据列表(无策略ID)
     *
     * @param alarmHistoricalData 告警历史数据
     * @return 告警历史数据集合
     */
    List<AlarmHistoricalData> selectAlarmHistoricalDataNoIdList(AlarmHistoricalData alarmHistoricalData);

    /**
     * 新增告警历史数据
     *
     * @param alarmHistoricalData 告警历史数据
     * @return 结果
     */
    int insertAlarmHistoricalData(AlarmHistoricalData alarmHistoricalData);

    /**
     * 新增告警历史数据批量
     *
     * @param alarmHistoricalDatas 告警历史数据
     * @return 结果
     */
    int insertAlarmHistoricalDataBatch(@Param("list") List<AlarmHistoricalData> alarmHistoricalDatas);

    /**
     * 修改告警历史数据
     *
     * @param alarmHistoricalData 告警历史数据
     * @return 结果
     */
    int updateAlarmHistoricalData(AlarmHistoricalData alarmHistoricalData);

    /**
     * 删除告警历史数据
     *
     * @param id 告警历史数据主键
     * @return 结果
     */
    int deleteAlarmHistoricalDataById(Long id);

    /**
     * 批量删除告警历史数据
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAlarmHistoricalDataByIds(Long[] ids);
}
