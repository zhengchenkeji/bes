package com.zc.efounder.JEnterprise.service.safetyWarning;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmTacticsAlarmNotifierLink;

/**
 * 告警策略告警接收组关联Service接口
 *
 * @author sunshangeng
 * @date 2022-09-20
 */
public interface AlarmTacticsAlarmNotifierLinkService
{

    /**
     * 新增告警策略告警接收组关联
     *
     * @param alarmTacticsAlarmNotifierLink 告警策略告警接收组关联
     * @return 结果
     */
    AjaxResult insertAlarmTacticsAlarmNotifierLink(AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLink);



    /**
     * 批量删除告警策略告警接收组关联
     *
     * @param alarmTacticsAlarmNotifierLink 需要删除的告警策略告警接收组关联主键集合
     * @return 结果
     */
    int deleteAlarmTacticsAlarmNotifierLinkByIds(AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLink);

    /**
     * 删除告警策略告警接收组关联信息
     *
     * @param id 告警策略告警接收组关联主键
     * @return 结果
     */
    int deleteAlarmTacticsAlarmNotifierLinkById(Long id);


}
