package com.zc.efounder.JEnterprise.service.safetyWarning;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNotifier;

/**
 * 告警接收组Service接口
 *
 * @author sunshangeng
 * @date 2022-09-15
 */
public interface AlarmNotifierService
{
    /**
     * 查询告警接收组
     *
     * @param id 告警接收组主键
     * @return 告警接收组
     */
    public AlarmNotifier selectAlarmNotifierById(Long id);

    /**
     * 查询告警接收组列表
     *
     * @param alarmNotifier 告警接收组
     * @return 告警接收组集合
     */
    List<AlarmNotifier> selectAlarmNotifierList(AlarmNotifier alarmNotifier);

    /**
     * 新增告警接收组
     *
     * @param alarmNotifier 告警接收组
     * @return 结果
     */
    AjaxResult insertAlarmNotifier(AlarmNotifier alarmNotifier);

    /**
     * 修改告警接收组
     *
     * @param alarmNotifier 告警接收组
     * @return 结果
     */
    AjaxResult updateAlarmNotifier(AlarmNotifier alarmNotifier);

    /**
     * 批量删除告警接收组
     *
     * @param ids 需要删除的告警接收组主键集合
     * @return 结果
     */
    int deleteAlarmNotifierByIds(Long[] ids);

    /**
     * 删除告警接收组信息
     *
     * @param id 告警接收组主键
     * @return 结果
     */
    int deleteAlarmNotifierById(Long id);

    /**
     * @Description 获取部门下的用户
     *
     * @author liuwenge
     * @date 2023/2/28 16:59
     * @param deptId
     * @return java.util.List<com.ruoyi.common.core.domain.entity.SysUser>
     */
    List<SysUser> getUserList(String deptId);
}
