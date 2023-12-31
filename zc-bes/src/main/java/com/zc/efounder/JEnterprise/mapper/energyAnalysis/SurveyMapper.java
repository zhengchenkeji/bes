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
public interface SurveyMapper{

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();

    /**
     * @Description 查询支路环比
     * 
     * @author liuwenge
     * @date 2022/11/10 13:53
     * @param branchId 支路id
    * @param thisTimeStart 本次开始时间
    * @param thisTimeEnd 本次接收时间
    * @param lastTimeStart 上次开始时间
    * @param lastTimeEnd 上次结束时间
    * @param dataType 类型(0:时 1:天 2:月 3:年)
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String,Object> queryEnergyData(@Param("branchId") String branchId,
                                       @Param("thisTimeStart") String thisTimeStart,
                                       @Param("thisTimeEnd") String thisTimeEnd,
                                       @Param("lastTimeStart") String lastTimeStart,
                                       @Param("lastTimeEnd") String lastTimeEnd,
                                       @Param("dataType") String dataType);

    /**
     * @Description 差值支路能耗趋势
     *
     * @author liuwenge
     * @date 2022/11/10 15:54
     * @param branchId 支路id
    * @param trendType 类型:0当日 1当月 2当年
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String,Object>> queryTrendData(@Param("branchId") String branchId,
                                            @Param("trendType") String trendType,
                                            @Param("dateTime") String dateTime);

    /**
     * @Description 支路能耗排行
     *
     * @author liuwenge
     * @date 2022/11/10 15:54
     * @param branchId 支路id
    * @param rankType 类型:0当日 1当月 2当年
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String,Object>> queryRankData(@Param("branchId") String branchId,
                                            @Param("rankType") String rankType,
                                            @Param("dateTime") String dateTime);
}