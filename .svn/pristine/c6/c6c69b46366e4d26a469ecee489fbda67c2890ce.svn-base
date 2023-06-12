package com.zc.efounder.JEnterprise.mapper.energyAnalysis;


import com.zc.efounder.JEnterprise.domain.energyAnalysis.dto.RankingDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 能耗排名
 *
 * @author qindehua
 * @date 2022/11/28
 */
public interface RankingMapper {


    /**
     * 查询能耗数据 根据支路id
     *
     * @param branchIds 支路ids
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return {@code List<BesBranchData> }
     * @Author qindehua
     * @Date 2022/11/28
     **/
    List<RankingDTO> selectDataByBranchIds(@Param("branchIds") List branchIds,
                                                  @Param("startTime") String startTime,
                                                  @Param("endTime") String endTime);

    /**
     * 查询能耗数据 根据分户id
     *
     * @param householdIds 分户ids
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return {@code List<BesBranchData> }
     * @Author qindehua
     * @Date 2022/11/28
     **/
    List<RankingDTO> selectDataByHouseholdIds(@Param("householdIds") List householdIds,
                                           @Param("startTime") String startTime,
                                           @Param("endTime") String endTime);
}
