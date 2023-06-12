package com.zc.efounder.JEnterprise.mapper.safetyWarning;

import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmRealtimeData;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 告警实时数据Mapper接口
 *
 * @author qindehua
 * @date 2022-11-04
 */
public interface AlarmRealtimeDataMapper {
    /**
     * 查询告警实时数据
     *
     * @param id 告警实时数据主键
     * @return 告警实时数据
     */
    public AlarmRealtimeData selectAlarmRealtimeDataById(Long id);


    /**
     * 查询告警实时数据总条数
     *
     * @return 告警实时数据
     */
    public int selectAlarmRealtimeDataCount();

    /**
     * 查询告警实时数据
     *
     * @param alarmRealtimeData 告警实时数
     * @return 告警实时数据
     */
    AlarmRealtimeData selectAlarmRealtimeData(AlarmRealtimeData alarmRealtimeData);

    /**
     * 查询告警实时数据根据报警配置id
     *
     * @return 告警实时数据
     */
    AlarmRealtimeData selectAlarmRealtimeDataByAlarmTacticsId(@Param("alarmTacticsId") Long alarmTacticsId);

    /**
     * 查询告警实时数据列表
     *
     * @param alarmRealtimeData 告警实时数据
     * @return 告警实时数据集合
     */
    List<AlarmRealtimeData> selectAlarmRealtimeDataList(AlarmRealtimeData alarmRealtimeData);

    /**
     * 查询告警实时数据列表(有策略ID)
     */
    List<AlarmRealtimeData> selectAlarmRealtimeDataStateList(AlarmRealtimeData alarmRealtimeData);

    /**
     * 查询告警实时数据列表(无策略ID)
     *
     * @param alarmRealtimeData 告警实时数据
     * @return 告警实时数据集合
     */
    List<AlarmRealtimeData> selectAlarmRealtimeDataNoIdList(AlarmRealtimeData alarmRealtimeData);

    /**
     * 查询告警实时数据列表
     *
     * @param level 告警优先级
     * @return 告警实时数据集合
     */
    List<AlarmRealtimeData> selectAlarmRealtimeDataInfo(@Param("level") String level);

    /**
     * 新增告警实时数据
     *
     * @param alarmRealtimeData 告警实时数据
     * @return 结果
     */
    int insertAlarmRealtimeData(AlarmRealtimeData alarmRealtimeData);

    /**
     * 修改告警实时数据
     *
     * @param alarmRealtimeData 告警实时数据
     * @return 结果
     */
    int updateAlarmRealtimeData(AlarmRealtimeData alarmRealtimeData);

    /**
     * 删除告警实时数据
     *
     * @param id 告警实时数据主键
     * @return 结果
     */
    int deleteAlarmRealtimeDataById(Long id);

    /**
     * 批量删除告警实时数据
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAlarmRealtimeDataByIds(Long[] ids);

    /**
     * 更新报警实时数据根据报警策略id
     *
     * @param alarmRealtimeData 报警实时数据
     * @return int
     * @Author qindehua
     * @Date 2022/11/23
     **/
    int updateAlarmRealtimeDataByAlarmTacticsId(AlarmRealtimeData alarmRealtimeData);

    int updateAlarmRealtimeDataById(AlarmRealtimeData alarmRealtimeData);

    /**
     * 查询所有关联的告警策略
     * @return List
     * @Author gaojikun
     **/
    List<Long> selectUserNotifier(@Param("userId") String userId);
}
