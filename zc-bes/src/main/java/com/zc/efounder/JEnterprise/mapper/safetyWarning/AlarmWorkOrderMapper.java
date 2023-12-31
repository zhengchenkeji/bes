package com.zc.efounder.JEnterprise.mapper.safetyWarning;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmWorkOrder;
import org.apache.ibatis.annotations.Param;

/**
 * 告警工单Mapper接口
 *
 * @author ruoyi
 * @date 2023-03-06
 */
public interface AlarmWorkOrderMapper {
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

    List<AlarmWorkOrder> selectAlarmWorkOrderStateList(AlarmWorkOrder alarmWorkOrder);

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
    Boolean updateAlarmWorkOrder(AlarmWorkOrder alarmWorkOrder);

    /**
     * 删除告警工单
     *
     * @param id 告警工单主键
     * @return 结果
     */
    int deleteAlarmWorkOrderById(Long id);

    /**
     * 批量删除告警工单
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAlarmWorkOrderByIds(Long[] ids);

    /**
     * 根据告警位置 告警策略 接收人查询是否存在
     */

    AlarmWorkOrder getWorkOrderByTactics(@Param("tacticsId") Long tacticsId, @Param("userIds") String UserIds, @Param("azwz") String azwz);

    /**
     * 根据策略ID查询用户ID
     */
    Map<String, String> selectUserIdByAlarmTacticsId(@Param("id") Long id);

    /**
     * 根据设备ID修改用户ID
     */
    boolean updateWorkOrderUserId(AlarmWorkOrder alarmWorkOrder);

    /**
     * 根据车瑞额ID修改用户ID
     */
    boolean updateWorkOrderUserIdByAlarmTacticsId(AlarmWorkOrder alarmWorkOrder);

    boolean updateAlarmWorkOrderDealBatch(@Param("ids") List<Long> ids, @Param("status") String status, @Param("updateTime") Date updateTime,
                                          @Param("updateCode") String updateCode, @Param("updateName") String updateName,
                                          @Param("remark") String remark);
}
