package com.zc.efounder.JEnterprise.service.energyAnalysis.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.Cache.BranchConfigCache;
import com.zc.efounder.JEnterprise.mapper.energyAnalysis.SurveyMapper;
import com.zc.efounder.JEnterprise.service.energyAnalysis.SurveyService;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description 用能概况Service业务层处理
 *
 * @author liuwenge
 * @date 2022/11/9 10:48
 */
@Service
public class SurveyServiceImpl implements SurveyService {

    @Resource
    private SurveyMapper surveyMapper;
    @Resource
    RedisCache redisCache;
    @Resource
    BranchConfigCache branchConfigCache;

    /**
     * 查询所有园区
     */
    @Override
    public List<Park> getAllPark(){
        return surveyMapper.getAllPark();
    }

    /**
     * 查询所有能源类型
     */
    @Override
    public List<EnergyType> getEnergyType(){
        ArrayList<EnergyType> energyTypeList = new ArrayList<>();

        Map<String, EnergyType> energyTypeCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
        if (energyTypeCache == null || energyTypeCache.size() == 0) {
            return energyTypeList;
        }

        energyTypeList.addAll(energyTypeCache.values());

        //按照code重新排序
        energyTypeList.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getCode())));
        return energyTypeList;
    }

    /**
     * @Description 查询支路环比
     *
     * @author liuwenge
     * @date 2022/11/10 14:11
     * @param branchId 支路id
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult queryRingRatioData(String branchId){

        if (!StringUtils.hasText(branchId)){
            return AjaxResult.error("参数错误");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //今日用能 时间范围
        Date date = new Date();
        String todayEnd = dateFormat.format(date);
        String todayStart = todayEnd.substring(0,10) + " 00:00:00";

        //昨日同期 时间范围
        //到小时,而不是比较昨天一整天的数据
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY,-24);
        String yesterdayEnd = dateFormat.format(calendar.getTime());
        String yesterdayStart = yesterdayEnd.substring(0,10) + " 00:00:00";

        //当月用能 时间范围
        String monthStart = dateFormat.format(date).substring(0,7) + "-01 00:00:00";

        //上月同期 时间范围
        //到日,而不是上月一整月的数据
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        String lastMonthEnd = dateFormat.format(calendar.getTime());
        String lastMonthStart = dateFormat.format(calendar.getTime()).substring(0,7) + "-01 00:00:00";

        //今年用能 时间范围
        String yearStart = dateFormat.format(date).substring(0,4) + "-01-01 00:00:00";

        //去年同期 时间范围
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -1);
        String lastYearEnd = dateFormat.format(calendar.getTime());
        String lastYearStart = dateFormat.format(calendar.getTime()).substring(0,4) + "-01-01 00:00:00";

        Map<String,Object> result = new HashMap<>();
        BigDecimal thisTimeData; //本次数据
        BigDecimal lastTimeData; //同期数据
        BigDecimal diff; //差值


        //查询今日 昨日同期数据
        Map<String, Object> dayData = surveyMapper.queryEnergyData(branchId,todayStart,todayEnd,yesterdayStart,yesterdayEnd,"0");
        if (dayData != null){
            thisTimeData = new BigDecimal(dayData.get("thisTimeData").toString());
            lastTimeData = new BigDecimal(dayData.get("lastTimeData").toString());
            diff = new BigDecimal(dayData.get("diff").toString());

            result.put("today",thisTimeData);
            result.put("yesterday",lastTimeData);
            result.put("dayDifference",diff);

            if (lastTimeData.compareTo(BigDecimal.ZERO)==0){
                result.put("dayPercentage","--");
            } else {
                BigDecimal dayPercentage = diff.divide(lastTimeData,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                dayPercentage = dayPercentage.setScale(2, BigDecimal.ROUND_HALF_UP);
                result.put("dayPercentage",dayPercentage.toString() + "%");
            }
        } else {
            result.put("today",0);
            result.put("yesterday",0);
            result.put("dayDifference",0);
            result.put("dayPercentage","--");
        }

        //查询本月 上月同期数据
        Map<String, Object> monthData = surveyMapper.queryEnergyData(branchId,monthStart,todayEnd,lastMonthStart,lastMonthEnd,"1");
        if (monthData != null){
            thisTimeData = new BigDecimal(monthData.get("thisTimeData").toString());
            lastTimeData = new BigDecimal(monthData.get("lastTimeData").toString());
            diff = new BigDecimal(monthData.get("diff").toString());

            result.put("month",thisTimeData);
            result.put("lastMonth",lastTimeData);
            result.put("monthDifference",diff);

            if (lastTimeData.compareTo(BigDecimal.ZERO)==0){
                result.put("monthPercentage","--");
            } else {
                BigDecimal monthPercentage = diff.divide(lastTimeData,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                monthPercentage = monthPercentage.setScale(2, BigDecimal.ROUND_HALF_UP);
                result.put("monthPercentage",monthPercentage.toString() + "%");
            }
        } else {
            result.put("month",0);
            result.put("lastMonth",0);
            result.put("monthDifference",0);
            result.put("monthPercentage","--");
        }

        //查询今年 去年同期数据
        Map<String, Object> yearData = surveyMapper.queryEnergyData(branchId,yearStart,todayEnd,lastYearStart,lastYearEnd,"2");
        if (yearData != null){
            thisTimeData = new BigDecimal(yearData.get("thisTimeData").toString());
            lastTimeData = new BigDecimal(yearData.get("lastTimeData").toString());
            diff = new BigDecimal(yearData.get("diff").toString());

            result.put("year",thisTimeData);
            result.put("lastYear",lastTimeData);
            result.put("yearDifference",diff);

            if (lastTimeData.compareTo(BigDecimal.ZERO)==0){
                result.put("yearPercentage","--");
            } else {
                BigDecimal yearPercentage = diff.divide(lastTimeData,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                yearPercentage = yearPercentage.setScale(2, BigDecimal.ROUND_HALF_UP);
                result.put("yearPercentage",yearPercentage.toString() + "%");
            }
        } else {
            result.put("year",0);
            result.put("lastYear",0);
            result.put("yearDifference",0);
            result.put("yearPercentage","--");
        }


        return AjaxResult.success(result);

    }

    /**
     * @Description 查询支路能耗趋势
     *
     * @author liuwenge
     * @date 2022/11/10 15:37
     * @param branchId 支路id
    * @param trendType 类型: 0天 1月 2年
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult queryTrendData(String branchId,String trendType){
        if (!StringUtils.hasText(branchId) || !StringUtils.hasText(trendType)){
            return AjaxResult.error("参数错误");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String thisTime = dateFormat.format(new Date());
        Calendar calendar=Calendar.getInstance();
        if (trendType.equals("0")){

        } else if (trendType.equals("1")){
            thisTime = thisTime.substring(0,7);
        } else if (trendType.equals("2")){
            thisTime = thisTime.substring(0,4);
        }

        List<Map<String,Object>> thisDataList = surveyMapper.queryTrendData(branchId,trendType,thisTime);

        Map<String,Object> thisTimeData = new HashMap<>();
        String key = "";
        if (trendType.equals("0")){
            for (Map<String, Object> thisDatum : thisDataList) {
                key = thisDatum.get("F_CJSJ").toString().substring(11,13);
                thisTimeData.put(key,thisDatum.get("F_DATA"));
            }
        } else if (trendType.equals("1")){
            for (Map<String, Object> thisDatum : thisDataList) {
                key = thisDatum.get("F_CJSJ").toString().substring(8,10);
                thisTimeData.put(key,thisDatum.get("F_DATA"));

            }
        } else if (trendType.equals("2")){
            for (Map<String, Object> thisDatum : thisDataList) {
                key = thisDatum.get("F_CJSJ").toString().substring(5,7);
                thisTimeData.put(key,thisDatum.get("F_DATA"));
            }
        }

        List<Map<String,Object>> lastDataList = surveyMapper.queryTrendData(branchId,trendType,thisTime);

        Map<String,Object> lastTimeData = new HashMap<>();

        if (trendType.equals("0")){
            for (Map<String, Object> lastDatum : lastDataList) {
                key = lastDatum.get("F_CJSJ").toString().substring(11,13);
                lastTimeData.put(key,lastDatum.get("F_DATA"));
            }
        } else if (trendType.equals("1")){
            for (Map<String, Object> lastDatum : lastDataList) {
                key = lastDatum.get("F_CJSJ").toString().substring(8,10);
                lastTimeData.put(key,lastDatum.get("F_DATA"));
            }
        } else if (trendType.equals("2")){
            for (Map<String, Object> lastDatum : lastDataList) {
                key = lastDatum.get("F_CJSJ").toString().substring(5,7);
                lastTimeData.put(key,lastDatum.get("F_DATA"));
            }
        }

        Map<String,Object> result = new HashMap<>();
        result.put("thisTimeData",thisTimeData);
        result.put("lastTimeData",lastTimeData);

        return AjaxResult.success(result);
    }

    /**
     * @Description 查询能耗排行
     *
     * @author liuwenge
     * @date 2022/11/14 16:00
     * @param branchId 支路id
     * @param rankType 类型 0日 1月 2年
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult queryRankData(String branchId,String rankType){
        if (!StringUtils.hasText(branchId) || !StringUtils.hasText(rankType)){
            return AjaxResult.error("参数错误");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = dateFormat.format(new Date());
        Calendar calendar=Calendar.getInstance();
        if (rankType.equals("0")){

        } else if (rankType.equals("1")){
            dateTime = dateTime.substring(0,7);
        } else if (rankType.equals("2")){
            dateTime = dateTime.substring(0,4);
        }

        List<Map<String,Object>> result = surveyMapper.queryRankData(branchId,rankType,dateTime);

        return AjaxResult.success(result);
    }
}