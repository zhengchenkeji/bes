package com.zc.efounder.JEnterprise.service.safetyWarning;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmRealtimeData;

import java.util.List;

/**
 * 告警实时数据Service接口
 *
 * @author qindehua
 * @date 2022-11-04
 */
public interface IAlarmRealtimeDataService {
    /**
     * 查询告警实时数据
     *
     * @param id 告警实时数据主键
     * @return 告警实时数据
     */
    AlarmRealtimeData selectAlarmRealtimeDataById(Long id);

    /**
     * 查询告警实时数据列表
     *
     * @param alarmRealtimeData 告警实时数据
     * @return 告警实时数据集合
     */
    List<AlarmRealtimeData> selectAlarmRealtimeDataList(AlarmRealtimeData alarmRealtimeData);


    /**
     * 查询告警实时数据个数
     *
     * @return {@code Integer }
     * @Author qindehua
     * @Date 2022/11/07
     **/
    Integer selectAlarmCount();

    /**
     * 查询告警实时数据
     *
     * @param level 级别
     * @return {@code List<AlarmRealtimeData> }
     * @Author qindehua
     * @Date 2022/11/07
     **/
    AjaxResult selectAlarmInfo(String level);


    /**
     * 批量删除告警实时数据
     *
     * @param ids 需要删除的告警实时数据主键集合
     * @return 结果
     */
    AjaxResult deleteAlarmRealtimeDataByIds(Long[] ids);

    /**
     * 处理报警实时数据
     *
     * @param alarmRealtimeDatas 报警实时数据
     * @return {@code AjaxResult }
     * @Author qindehua
     **/
    AjaxResult updateAlarmRealtimeData(List<AlarmRealtimeData> alarmRealtimeDatas,boolean isWorkOrderCall);
}
