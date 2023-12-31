package com.zc.efounder.JEnterprise.mapper.commhandler;

import com.zc.efounder.JEnterprise.domain.baseData.OtherCalculateData;
import com.zc.efounder.JEnterprise.domain.commhandler.MonitoringErrorLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author liuwenge
 * @Description
 * @date 2022/10/19 14:21
 */
public interface JobManagerMapper {

    /**
     * @param lstCalcDto 能耗数据
     * @return void
     * @Description 插入电表数据表
     * @author liuwenge
     * @date 2022/10/19 14:23
     */
    public void insertCalculateData(@Param("lstCalcDto") List<Map<String, Object>> lstCalcDto);


    /**
     * @param lstCalcDto 能耗数据
     * @return void
     * @Description 插入第三方能源数据表
     * @author sunshangeng
     * @date 2022/10/19 14:23
     */
    public void insertOtherCalculateData(@Param("lstDto") List<OtherCalculateData> lstCalcDto);

    /**
     * @param lstCalcDto 能耗数据
     * @return void
     * @Description 插入电表数据表
     * @author liuwenge
     * @date 2022/10/19 14:23
     */
    public Boolean deleteCalculateData(@Param("id") String id);


    /**
     * @param lstDto
     * @return void
     * @Description 插入电表原始数据表
     * @author liuwenge
     * @date 2022/10/19 14:32
     */
    public void insertEnectricData(@Param("lstDto") List<Map<String, Object>> lstDto);

    /**
     * 将差值数据记录到差值记录表
     *
     * @param lstDto
     */
    public void insertMonitoringData(@Param("lstDto") List<Map<String, Object>> lstDto);

    /**
     * 点位系统名称改变后修改电表数据表
     *
     * @author gaojikun
     */
    public void updateCalculate(@Param("oldName") String oldName, @Param("newName") String newName);

    /**
     * 点位系统名称改变后修改插值记录表
     *
     * @author gaojikun
     */
    public void updateMonitoring(@Param("oldName") String oldName, @Param("newName") String newName);

    /**
     * @Description
     *
     * @author liuwenge
     * @date 2022/11/3 17:41
     * @param
     * @return void
     */
    public boolean insertMonitoringErrorLog(MonitoringErrorLog monitoringErrorLog);

}
