package com.zc.efounder.JEnterprise.service.safetyWarning;

import java.util.List;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNotificationRecord;

/**
 * 告警通知记录Service接口
 *
 * @author ruoyi
 * @date 2022-09-19
 */
public interface AlarmNotificationRecordService
{
    /**
     * 查询告警通知记录
     *
     * @param id 告警通知记录主键
     * @return 告警通知记录
     */
    public AlarmNotificationRecord selectAlarmNotificationRecordById(Long id);

    /**
     * 查询告警通知记录列表
     *
     * @param alarmNotificationRecord 告警通知记录
     * @return 告警通知记录集合
     */
    List<AlarmNotificationRecord> selectAlarmNotificationRecordList(AlarmNotificationRecord alarmNotificationRecord);

    /**
     * 新增告警通知记录
     *
     * @param alarmNotificationRecord 告警通知记录
     * @return 结果
     */
    int insertAlarmNotificationRecord(AlarmNotificationRecord alarmNotificationRecord);

    /**
     * 修改告警通知记录
     *
     * @param alarmNotificationRecord 告警通知记录
     * @return 结果
     */
    int updateAlarmNotificationRecord(AlarmNotificationRecord alarmNotificationRecord);

    /**
     * 批量删除告警通知记录
     *
     * @param ids 需要删除的告警通知记录主键集合
     * @return 结果
     */
    int deleteAlarmNotificationRecordByIds(Long[] ids);

    /**
     * 删除告警通知记录信息
     *
     * @param id 告警通知记录主键
     * @return 结果
     */
    int deleteAlarmNotificationRecordById(Long id);


    /**
     * @description:导出告警通知记录的查询方法
     *  因需要多表关联 导出时 访问该方法
     * @author: sunshangeng
     * @date: 2022/9/22 16:29
     * @param:
     * @return:
     **/

    List<AlarmNotificationRecord> exportAlarmNotificationRecordList(AlarmNotificationRecord alarmNotificationRecord);

}
