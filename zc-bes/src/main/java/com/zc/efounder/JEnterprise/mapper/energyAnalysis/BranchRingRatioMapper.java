package com.zc.efounder.JEnterprise.mapper.energyAnalysis;

import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 支路环比Mapper接口
 *
 * @author liuwenge
 * @date 2022/11/9 10:49
 */
public interface BranchRingRatioMapper {

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();


    List<Map<String,Object>> queryDayData(@Param("branchId") String branchId,@Param("thisTime") String thisTime,@Param("lastTime") String lastTime);

    List<Map<String,Object>> queryMonthData(@Param("branchId") String branchId,@Param("thisTime") String thisTime,@Param("lastTime") String lastTime);
}