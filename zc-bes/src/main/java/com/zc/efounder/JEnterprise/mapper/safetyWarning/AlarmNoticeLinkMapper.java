package com.zc.efounder.JEnterprise.mapper.safetyWarning;

import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNoticeLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 告警配置和通知关系Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-08
 */
public interface AlarmNoticeLinkMapper
{
    /**
     * 查询告警配置和通知关系
     *
     * @param id 告警配置和通知关系主键
     * @return 告警配置和通知关系
     */
     AlarmNoticeLink selectAlarmNoticeLinkById(Long id);

    /**
     * @description:根据type和策略id查看绑定的通知配置
     * @author: sunshangeng
     * @date: 2023/3/8 18:02
     * @param: [alarmTacticsid, noticeType]
     * @return: com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNoticeLink
     **/
     AlarmNoticeLink selectAlarmNoticeLinkByType(@Param("alarmTacticsid") Long alarmTacticsid,@Param("noticeType") Integer noticeType);

    /**
     * 查询告警配置和通知关系列表
     *
     * @param AlarmNoticeLink 告警配置和通知关系
     * @return 告警配置和通知关系集合
     */
    List<AlarmNoticeLink> selectAlarmNoticeLinkList(AlarmNoticeLink AlarmNoticeLink);

    /**
     * 新增告警配置和通知关系
     *
     * @param AlarmNoticeLink 告警配置和通知关系
     * @return 结果
     */
    Boolean insertAlarmNoticeLink(AlarmNoticeLink AlarmNoticeLink);

    /**
     * 修改告警配置和通知关系
     *
     * @param AlarmNoticeLink 告警配置和通知关系
     * @return 结果
     */
    int updateAlarmNoticeLink(AlarmNoticeLink AlarmNoticeLink);


    Boolean delNoticeLinkBytype(@Param("alarmTacticsid") Long alarmTacticsid,@Param("noticeType") Integer noticeType);
    /**
     * 删除告警配置和通知关系
     *
     * @param id 告警配置和通知关系主键
     * @return 结果
     */
    int deleteAlarmNoticeLinkById(Long id);

    /**
     * 批量删除告警配置和通知关系
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAlarmNoticeLinkByIds(Long[] ids);
}
