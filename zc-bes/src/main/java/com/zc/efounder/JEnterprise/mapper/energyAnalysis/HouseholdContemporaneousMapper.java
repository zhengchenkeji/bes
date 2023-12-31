package com.zc.efounder.JEnterprise.mapper.energyAnalysis;

import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 用能概况Mapper接口
 *
 * @author liuwenge
 * @date 2022/11/9 10:49
 */
public interface HouseholdContemporaneousMapper {

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();


    List<Map<String,Object>> queryData(@Param("householdId") String householdId,@Param("thisTime") String thisTime,@Param("lastTime") String lastTime);
}