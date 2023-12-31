package com.zc.efounder.JEnterprise.mapper.safetyWarning;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNotifier;

/**
 * 告警接收组Mapper接口
 *
 * @author sunshangeng
 * @date 2022-09-15
 */
public interface AlarmNotifierMapper
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
    * 根据关联关系查询 接收组信息
    * */
    List<AlarmNotifier> selectTacticsNotifierLinkList(AlarmNotifier alarmNotifier);
    /**
     * 根据关联关系查询 未关联的用户
     * */
    List<AlarmNotifier> selectTacticsNotifierNotLinkList(AlarmNotifier alarmNotifier);

    /**
     * 新增告警接收组
     *
     * @param alarmNotifier 告警接收组
     * @return 结果
     */
    int insertAlarmNotifier(AlarmNotifier alarmNotifier);

    /**
     * 修改告警接收组
     *
     * @param alarmNotifier 告警接收组
     * @return 结果
     */
    int updateAlarmNotifier(AlarmNotifier alarmNotifier);

    /**
     * 删除告警接收组
     *
     * @param id 告警接收组主键
     * @return 结果
     */
    int deleteAlarmNotifierById(Long id);

    /**
     * 批量删除告警接收组
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAlarmNotifierByIds(Long[] ids);


    /**
     * 查询是否有重复的告警策略组姓名
     *
     * @return 结果
     */

    int getNotifierByName(AlarmNotifier alarmNotifier);

    /**
     * 查询是否有重复的告警策略组姓名
     *
     * @return 结果
     */

    int getNotifierByPhone(AlarmNotifier alarmNotifier);

    /**
     * 查询是否有重复的告警策略组姓名
     *
     * @return 结果
     */

    int getNotifierByEmail(AlarmNotifier alarmNotifier);

    /**
     * @Description 获取部门下的用户
     *
     * @author liuwenge
     * @date 2023/2/28 17:09
     * @param deptId
     * @return java.util.List<com.ruoyi.common.core.domain.entity.SysUser>
     */
    List<SysUser> getUserList(String deptId);
}
