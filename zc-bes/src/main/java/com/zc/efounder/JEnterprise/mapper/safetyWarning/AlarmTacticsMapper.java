package com.zc.efounder.JEnterprise.mapper.safetyWarning;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNotifier;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmTactics;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

/**
 * 告警策略Mapper接口
 *
 * @author sunshangeng
 * @date 2022-09-16
 */
public interface AlarmTacticsMapper
{
    /**
     * 查询告警策略
     *
     * @param id 告警策略主键
     * @return 告警策略
     */
    public AlarmTactics selectAlarmTacticsById(Long id);

    /**
     * 根据设备ids查询告警策略
     *
     * @param ids 设备ids
     * @return {@code List<AlarmTactics> }
     * @Author qindehua
     * @Date 2022/12/15
     **/
    public Long[] selectAlarmTacticsByDeviceIds(@Param("ids") String[] ids);

    /**
     * 根据设备ids查询告警策略
     *
     * @param ids 设备ids
     * @return {@code List<AlarmTactics> }
     * @Author qindehua
     * @Date 2022/12/15
     **/
    public Long[] selectAlarmTacticsByDeviceIds(@Param("ids") Long[] ids);

    /**
     * 查询告警策略列表
     *
     * @param alarmTactics 告警策略
     * @return 告警策略集合
     */
    List<AlarmTactics> selectAlarmTacticsList(AlarmTactics alarmTactics);



    /**
     * 查询告警策略列表根据设备ID
     *
     * @param deviceId 设备id
     * @return {@code List<AlarmTactics> }
     * @Author qindehua
     * @Date 2022/11/17
     **/
    List<AlarmTactics> selectAlarmTacticsListByDeviceId(@Param("deviceId") String deviceId);
    /**
     * 新增告警策略
     *
     * @param alarmTactics 告警策略
     * @return 结果
     */
    int insertAlarmTactics(AlarmTactics alarmTactics);

    /**
     * 修改告警策略
     *
     * @param alarmTactics 告警策略
     * @return 结果
     */
    int updateAlarmTactics(AlarmTactics alarmTactics);

    /**
     * 删除告警策略
     *
     * @param id 告警策略主键
     * @return 结果
     */
    int deleteAlarmTacticsById(Long id);

    /**
     * 批量删除告警策略
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAlarmTacticsByIds(Long[] ids);


    List<DicDataEntity> selectAlarmTacticsDicData();
    /**
     * 查询报警配置根据告警类型
     *
     * @param alarmTypeId 报警类型id
     * @return {@code List<Map<Long, String>> }
     * @Author qindehua
     * @Date 2022/11/22
     **/
    @MapKey("id")
    List<Map<Long, String>> selectAlarmTacticsByAlarmTypeId(@Param("alarmTypeId") Long alarmTypeId);

    /**
     * 查询告警接收组根据报警策略id
     *
     * @param alarmTacticsId 报警策略id
     * @return {@code List<AlarmNotifier> }
     * @Author qindehua
     * @Date 2022/11/23
     **/
    List<AlarmNotifier> selectAlarmNotifierByAlarmTacticsId(@Param("alarmTacticsId") Long alarmTacticsId);

    SysUser selectUserInfoById(@Param("userId") String userId);
}
