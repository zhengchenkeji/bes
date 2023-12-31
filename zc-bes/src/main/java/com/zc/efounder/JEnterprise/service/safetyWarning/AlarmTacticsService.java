package com.zc.efounder.JEnterprise.service.safetyWarning;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmTactics;

/**
 * 告警策略Service接口
 *
 * @author sunshangeng
 * @date 2022-09-16
 */
public interface AlarmTacticsService
{
    /**
     * 查询告警策略
     *
     * @param id 告警策略主键
     * @return 告警策略
     */
    public AlarmTactics selectAlarmTacticsById(Long id);

    /**
     * 查询告警策略列表
     *
     * @param alarmTactics 告警策略
     * @return 告警策略集合
     */
    List<AlarmTactics> selectAlarmTacticsList(AlarmTactics alarmTactics);

    /**
     * 新增告警策略
     *
     * @param alarmTactics 告警策略
     * @return 结果
     */
    AjaxResult insertAlarmTactics(AlarmTactics alarmTactics);

    /**
     * 修改告警策略
     *
     * @param alarmTactics 告警策略
     * @return 结果
     */
    AjaxResult updateAlarmTactics(AlarmTactics alarmTactics);

    /**
     * 批量删除告警策略
     *
     * @param ids 需要删除的告警策略主键集合
     * @return 结果
     */
    AjaxResult deleteAlarmTacticsByIds(Long[] ids);

    /**
     * 删除告警策略信息
     *
     * @param id 告警策略主键
     * @return 结果
     */
    int deleteAlarmTacticsById(Long id);


    List<DicDataEntity> selectAlarmTacticsDicData();


    AjaxResult selectTree(DeviceTree deviceTree);

    /**
     * 查询报警配置根据告警类型
     *
     * @param alarmTypeId 报警类型id
     * @return {@code List<Map<Long, String>> }
     * @Author qindehua
     * @Date 2022/11/22
     **/
    List<Map<Long,String>> selectAlarmTacticsByAlarmTypeId(Long alarmTypeId);


    AjaxResult getNoticeLinkBytype(Long alarmTacticsid,Integer noticeType);
}
