package com.zc.efounder.JEnterprise.mapper.electricPowerTranscription;

import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 电力报表Mapper接口
 *
 * @author liuwenge
 * @date 2022/11/9 10:49
 */
public interface ElectricityStatementMapper {

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();


    List<Map<String,Object>> queryData(@Param("meterList") List<String> meterList,@Param("dateTime") String dateTime);

    List<Map<String,Object>> queryDataOther(@Param("equipmentList") List<String> equipmentList,@Param("dateTime") String dateTime);

    List<String> queryCollMethodCode(@Param("meterIdList") List<String> meterIdList);

    ElectricParams queryElectricParams(@Param("electricCode") String electricCode);
}