package com.zc.efounder.JEnterprise.mapper.safetyWarning;

import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNotifier;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmTacticsAlarmNotifierLink;

import java.util.List;

/**
 * 告警策略告警接收组关联Mapper接口
 *
 * @author sunshangeng
 * @date 2022-09-20
 */
public interface AlarmTacticsAlarmNotifierLinkMapper
{
    /**
     * 新增告警策略告警接收组关联
     *
     * @param alarmTacticsAlarmNotifierLink 告警策略告警接收组关联
     * @return 结果
     */
    int insertAlarmTacticsAlarmNotifierLink(AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLink);

    /**
     * 修改告警策略告警接收组关联
     *
     * @param alarmTacticsAlarmNotifierLink 告警策略告警接收组关联
     * @return 结果
     */
    int updateAlarmTacticsAlarmNotifierLink(AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLink);

    /**
     * 删除告警策略告警接收组关联
     *
     * @param id 告警策略告警接收组关联主键
     * @return 结果
     */
    int deleteAlarmTacticsAlarmNotifierLinkById(Long id);
    /**
     * 批量删除告警策略告警接收组关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAlarmTacticsAlarmNotifierLinkByIds(Long[] ids);


    /**
     * @description:根据接收组id删除告警策略和告警接收组关联关系
     * @author: sunshangeng
     * @date: 2022/9/22 13:57
     * @param: [notifierId]
     * @return: java.lang.Boolean
     **/
    Boolean deleteByAlarmNotifierIdsBoolean(Long[] notifierIds);

    /**
     * @description:根据告警策略id删除告警策略和告警接收组关联关系
     * @author: sunshangeng
     * @date: 2022/9/22 14:00
     * @param: [tacticsId]
     * @return: java.lang.Boolean
     **/
    Boolean deleteByAlarmtacticsIdsBoolean(Long[] tacticsId);

    List<AlarmTacticsAlarmNotifierLink> selectOtherList(AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLink);

    AlarmNotifier selectUserIdByAlarmNotifierId(Long id);

    List<AlarmTacticsAlarmNotifierLink> selectCheckList(AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLink);
}
