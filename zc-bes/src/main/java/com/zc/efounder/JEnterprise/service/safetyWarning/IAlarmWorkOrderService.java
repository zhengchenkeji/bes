package com.zc.efounder.JEnterprise.service.safetyWarning;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmWorkOrder;

/**
 * 告警工单Service接口
 *
 * @author ruoyi
 * @date 2023-03-06
 */
public interface IAlarmWorkOrderService
{
    /**
     * 查询告警工单
     *
     * @param id 告警工单主键
     * @return 告警工单
     */
    public AlarmWorkOrder selectAlarmWorkOrderById(Long id);

    /**
     * 查询告警工单列表
     *
     * @param alarmWorkOrder 告警工单
     * @return 告警工单集合
     */
    List<AlarmWorkOrder> selectAlarmWorkOrderList(AlarmWorkOrder alarmWorkOrder);

    /**
     * 新增告警工单
     *
     * @param alarmWorkOrder 告警工单
     * @return 结果
     */
    int insertAlarmWorkOrder(AlarmWorkOrder alarmWorkOrder);

    /**
     * 修改告警工单
     *
     * @param alarmWorkOrder 告警工单
     * @return 结果
     */
    AjaxResult updateAlarmWorkOrder(AlarmWorkOrder alarmWorkOrder);

    /**
     * 批量删除告警工单
     *
     * @param ids 需要删除的告警工单主键集合
     * @return 结果
     */
    int deleteAlarmWorkOrderByIds(Long[] ids);

    /**
     * 删除告警工单信息
     *
     * @param id 告警工单主键
     * @return 结果
     */
    int deleteAlarmWorkOrderById(Long id);
}
