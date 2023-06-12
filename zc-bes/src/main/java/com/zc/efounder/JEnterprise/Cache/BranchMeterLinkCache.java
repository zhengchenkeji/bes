package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liuwenge
 * @Description 支路与电表关系缓存方法
 * @date 2022/10/20 9:26
 */
@Component
public class BranchMeterLinkCache {
    @Resource
    private RedisCache redisCache;


    /**
     * @param meterId
     * @return java.util.List<com.ruoyi.energyInfo.branchConfig.domain.AthenaBranchMeterLink>
     * @Description 根据电表id查询缓存
     * @author liuwenge
     * @date 2022/10/20 9:31
     */
    public List<AthenaBranchMeterLink> getCacheByMeterId(String meterId, String electricId) {

        if (meterId == null) {
            return null;
        }

        Map<String, AthenaBranchMeterLink> athenaBranchMeterLinkList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
        if (athenaBranchMeterLinkList == null || athenaBranchMeterLinkList.size() == 0) {
            return null;
        }


        List<AthenaBranchMeterLink> athenaBranchMeterLinks = new ArrayList<>();

        for (AthenaBranchMeterLink athenaBranchMeterLink : athenaBranchMeterLinkList.values()) {

            if (electricId != null) {
                if (meterId.equals(athenaBranchMeterLink.getMeterId().toString()) && electricId.equals(athenaBranchMeterLink.getElectricParam())) {
                    athenaBranchMeterLinks.add(athenaBranchMeterLink);
                }
            } else {
                if (meterId.equals(athenaBranchMeterLink.getMeterId().toString())) {
                    athenaBranchMeterLinks.add(athenaBranchMeterLink);
                }
            }
        }

        // 排序根据id
        athenaBranchMeterLinks.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getId().toString())));

        return athenaBranchMeterLinks;
    }

    /**
     * @Description 根据支路id查询下面的所有电表
     *
     * @author liuwenge
     * @date 2022/12/2 16:32
     * @param branchId
     * @return
     */
    public Map<String,List<String>> getMeterByBranchId(Long branchId){
        if (branchId == null) {
            return null;
        }
        //取出支路电表缓存
        Map<String, AthenaBranchMeterLink> athenaBranchMeterLinkList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
        if (athenaBranchMeterLinkList == null || athenaBranchMeterLinkList.size() == 0) {
            return null;
        }

        List<String> sysNameList = new ArrayList<>();
        List<String> meterIdList = new ArrayList<>();
        List<String> equipmentList = new ArrayList<>();
        List<String> itemDataList = new ArrayList<>();
        //遍历缓存添加电表/设备
        for (AthenaBranchMeterLink athenaBranchMeterLink : athenaBranchMeterLinkList.values()) {
            if (branchId.equals(athenaBranchMeterLink.getBranchId())){
                if("0".equals(athenaBranchMeterLink.getDeviceType())){
                    //电表系统名称
                    sysNameList.add(athenaBranchMeterLink.getSysName());
                    //电表ID
                    meterIdList.add(athenaBranchMeterLink.getMeterId().toString());
                }else{
                    //设备ID
                    equipmentList.add(athenaBranchMeterLink.getMeterId().toString());
                    String electricParam = athenaBranchMeterLink.getElectricParam();
                    itemDataList = Arrays.asList(electricParam.split(","));
                }
            }
        }

        Map<String,List<String>> result = new HashMap<>();
        result.put("sysNameList",sysNameList);
        result.put("meterIdList",meterIdList);
        result.put("equipmentList",equipmentList);
        result.put("itemDataList",itemDataList);
        return result;
    }
}
