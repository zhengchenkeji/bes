package com.zc.efounder.JEnterprise.service.energyAnalysis.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.Cache.BranchConfigCache;
import com.zc.efounder.JEnterprise.domain.energyAnalysis.EnergyConsumptionTrend;
import com.zc.efounder.JEnterprise.mapper.energyAnalysis.EnergyConsumptionTrendMapper;
import com.zc.efounder.JEnterprise.service.energyAnalysis.EnergyConsumptionTrendService;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gaojikun
 * @Description 用能概况Service业务层处理
 */
@Service
public class EnergyConsumptionTrendServiceImpl implements EnergyConsumptionTrendService {

    @Resource
    private EnergyConsumptionTrendMapper energyConsumptionTrendMapper;
    @Resource
    RedisCache redisCache;
    @Resource
    BranchConfigCache branchConfigCache;

    /**
     * 查询所有园区
     */
    @Override
    public List<Park> getAllPark() {
        return energyConsumptionTrendMapper.getAllPark();
    }

    /**
     * 查询所有能源类型
     */
    @Override
    public List<EnergyType> getEnergyType(EnergyConfig energyConfig) {
        ArrayList<EnergyType> energyTypeList = new ArrayList<>();
        Map<String, EnergyType> energyTypeCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
        Map<String, EnergyConfig> energyConfigCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyConfig);
        if (energyConfigCache == null || energyConfigCache.size() == 0) {
            return energyTypeList;
        }
        energyConfigCache.values().forEach(item -> {
            if(energyConfig.getParkCode().equals(item.getParkCode())){
                energyTypeCache.values().forEach(itemtype -> {
                    if(itemtype.getCode().equals(item.getEnergyCode())){
                        energyTypeList.add(itemtype);
                    }
                });
            }
        });
        //按照code重新排序
        energyTypeList.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getCode())));
        return energyTypeList;
    }

    /**
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 查询支路能耗趋势
     * @author gaojikun
     */
    @Override
    public AjaxResult queryAccessRdData(EnergyConsumptionTrend energyConsumptionTrend) {
        if (!StringUtils.hasText(energyConsumptionTrend.getZLID())
                || !StringUtils.hasText(energyConsumptionTrend.getDNBH())
                || !StringUtils.hasText(energyConsumptionTrend.getCJSJ())) {
            return AjaxResult.error("参数错误");
        }
        if (energyConsumptionTrend.getType() == null) {
            energyConsumptionTrend.setType("0");
        }

        String beforeCJSJ = dataType(energyConsumptionTrend.getType(),energyConsumptionTrend.getCJSJ());
        if(beforeCJSJ == null){
            return AjaxResult.error("转换时间发生异常");
        }
        energyConsumptionTrend.setBeforeCJSJ(beforeCJSJ);

        List<Map<String, Object>> dataList = energyConsumptionTrendMapper.queryTrendData(energyConsumptionTrend);

        return AjaxResult.success(dataList);
    }

    /**
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 查询分户能耗趋势
     * @author gaojikun
     */
    @Override
    public AjaxResult queryIndividualAccountData(EnergyConsumptionTrend energyConsumptionTrend) {
        if (!StringUtils.hasText(energyConsumptionTrend.getZLID())
                || !StringUtils.hasText(energyConsumptionTrend.getDNBH())
                || !StringUtils.hasText(energyConsumptionTrend.getCJSJ())) {
            return AjaxResult.error("参数错误");
        }
        if (energyConsumptionTrend.getType() == null) {
            energyConsumptionTrend.setType("0");
        }

        String beforeCJSJ = dataType(energyConsumptionTrend.getType(),energyConsumptionTrend.getCJSJ());
        if(beforeCJSJ == null){
            return AjaxResult.error("转换时间发生异常");
        }
        energyConsumptionTrend.setBeforeCJSJ(beforeCJSJ);

        List<Map<String, Object>> dataList = energyConsumptionTrendMapper.queryRankData(energyConsumptionTrend);

        return AjaxResult.success(dataList);
    }

    //获取昨日上月去年时间
    public String dataType(String type,String CJSJ){

        if("0".equals(type)){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                //使用SimpleDateFormat的parse()方法生成Date
                Date date = sf.parse(CJSJ);
                Calendar calendar = Calendar.getInstance(); //创建Calendar 的实例
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, -1); //当前时间减去一天
                return sf.format(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if("1".equals(type)){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
            try {
                //使用SimpleDateFormat的parse()方法生成Date
                Date date = sf.parse(CJSJ);
                Calendar calendar = Calendar.getInstance(); //创建Calendar 的实例
                calendar.setTime(date);
                calendar.add(Calendar.MONTH, -1); //当前时间减去一天
                return sf.format(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            int year = Integer.parseInt(CJSJ) - 1;
            return String.valueOf(year);
        }
        return null;
    }

}
