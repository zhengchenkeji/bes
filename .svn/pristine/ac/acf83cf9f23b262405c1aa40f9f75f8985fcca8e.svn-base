package com.zc.efounder.JEnterprise.service.energyAnalysis.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.mapper.energyAnalysis.HouseholdRingRatioMapper;
import com.zc.efounder.JEnterprise.service.energyAnalysis.HouseholdRingRatioService;
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
 * @Description 分户环比Service业务层处理
 *
 * @author liuwenge
 * @date 2022/11/9 10:48
 */
@Service
public class HouseholdRingRatioServiceImpl implements HouseholdRingRatioService {

    @Resource
    private HouseholdRingRatioMapper householdRingRatioMapper;
    @Resource
    RedisCache redisCache;

    /**
     * 查询所有园区
     */
    @Override
    public List<Park> getAllPark(){
        return householdRingRatioMapper.getAllPark();
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
     * 查询分户环比数据
     */
    @Override
    public AjaxResult queryData(String householdId,String dateType,String thisTime){
        if (!StringUtils.hasText(householdId) || !StringUtils.hasText(dateType) || !StringUtils.hasText(thisTime)){
            return AjaxResult.error("参数错误");
        }
        String lastTime;
        List<Map<String,Object>> dataList;
        if (dateType.equals("1")){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY,-24);
            lastTime = dateFormat.format(calendar.getTime());
            dataList = householdRingRatioMapper.queryDayData(householdId,thisTime,lastTime);

        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.MONTH,-1);
            lastTime = dateFormat.format(calendar.getTime());
            dataList = householdRingRatioMapper.queryMonthData(householdId,thisTime,lastTime);

        }


        if (dataList.size() > 0){
            BigDecimal thisData; //本次数据
            BigDecimal lastData; //同期数据
            BigDecimal diff; //差值
            for (Map<String, Object> dataMap : dataList) {
                thisData = new BigDecimal(dataMap.get("thisData").toString());
                lastData = new BigDecimal(dataMap.get("lastData").toString());

                if (lastData.compareTo(BigDecimal.ZERO)==0){
                    dataMap.put("ratio","--");
                } else {
                    diff = thisData.subtract(lastData);
                    BigDecimal monthPercentage = diff.divide(lastData,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    monthPercentage = monthPercentage.setScale(2, BigDecimal.ROUND_HALF_UP);
                    dataMap.put("ratio",monthPercentage.toString());
                }
            }
        }

        return AjaxResult.success(dataList);
    }



}