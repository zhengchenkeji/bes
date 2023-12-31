package com.zc.efounder.JEnterprise.service.safetyWarning.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.stereotype.Service;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmNotificationRecordMapper;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNotificationRecord;
import com.zc.efounder.JEnterprise.service.safetyWarning.AlarmNotificationRecordService;

import javax.annotation.Resource;

/**
 * 告警通知记录Service业务层处理
 *
 * @author ruoyi
 * @date 2022-09-19
 */
@Service
public class AlarmNotificationRecordServiceImpl implements AlarmNotificationRecordService
{
    @Resource
    private AlarmNotificationRecordMapper alarmNotificationRecordMapper;

    /**
     * 查询告警通知记录
     * sunshangeng
     * @param id 告警通知记录主键
     * @return 告警通知记录
     */
    @Override
    public AlarmNotificationRecord selectAlarmNotificationRecordById(Long id)
    {

        return alarmNotificationRecordMapper.selectAlarmNotificationRecordById(id);
    }

    /**
     * 查询告警通知记录列表
     *sunshangeng
     * @param alarmNotificationRecord 告警通知记录
     * @return 告警通知记录
     */
    @Override
    public List<AlarmNotificationRecord> selectAlarmNotificationRecordList(AlarmNotificationRecord alarmNotificationRecord)
    {
        return alarmNotificationRecordMapper.selectAlarmNotificationRecordList(alarmNotificationRecord);
    }

    /**
     * 新增告警通知记录
     *sunshangeng
     * @param alarmNotificationRecord 告警通知记录
     * @return 结果
     */
    @Override
    public int insertAlarmNotificationRecord(AlarmNotificationRecord alarmNotificationRecord)
    {
        alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
        return alarmNotificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
    }

    /**
     * 修改告警通知记录
     *sunshangeng
     * @param alarmNotificationRecord 告警通知记录
     * @return 结果
     */
    @Override
    public int updateAlarmNotificationRecord(AlarmNotificationRecord alarmNotificationRecord)
    {
        return alarmNotificationRecordMapper.updateAlarmNotificationRecord(alarmNotificationRecord);
    }

    /**
     * 批量删除告警通知记录
     *sunshangeng
     * @param ids 需要删除的告警通知记录主键
     * @return 结果
     */
    @Override
    public int deleteAlarmNotificationRecordByIds(Long[] ids)
    {
        return alarmNotificationRecordMapper.deleteAlarmNotificationRecordByIds(ids);
    }

    /**
     * 删除告警通知记录信息
     *sunshangeng
     * @param id 告警通知记录主键
     * @return 结果
     */
    @Override
    public int deleteAlarmNotificationRecordById(Long id)
    {
        return alarmNotificationRecordMapper.deleteAlarmNotificationRecordById(id);
    }

    /**
     * @description:导出告警通知记录的查询方法
     *  因需要多表关联 导出时 访问该方法
     * @author: sunshangeng
     * @date: 2022/9/22 16:31
     * @param: [alarmNotificationRecord]
     * @return: java.util.List<com.ruoyi.safetyWarning.alarmTactics.domain.AlarmNotificationRecord>
     **/
    @Override
    public List<AlarmNotificationRecord> exportAlarmNotificationRecordList(AlarmNotificationRecord alarmNotificationRecord) {
        return alarmNotificationRecordMapper.exportAlarmNotificationRecordList(alarmNotificationRecord);
    }
}
