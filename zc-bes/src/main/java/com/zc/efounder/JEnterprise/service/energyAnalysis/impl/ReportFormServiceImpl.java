package com.zc.efounder.JEnterprise.service.energyAnalysis.impl;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.energyAnalysis.vo.ReportFormResult;
import com.zc.efounder.JEnterprise.service.energyAnalysis.reportFormService;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBranchMeterLinkMapper;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * description:
 * author: sunshangeng
 * date:2022/11/25 17:20
 */
@Service
public class ReportFormServiceImpl implements reportFormService {
    @Resource
    private RedisCache redisCache;
    @Resource
    private AthenaBranchMeterLinkMapper branchMeterLinkMapper;

    /**
     * @description:查询集抄数据
     * @author: sunshangeng
     * @param: [branchIds 支路id, time 统计时间数组  ]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult service(@RequestBody Long[] branchIds,@RequestBody Date[] time) {
        List<ReportFormResult> resultList=new ArrayList<>();

        /**判断是否传入支路id*/
        if(branchIds==null||branchIds.length==0){

            return AjaxResult.success(resultList);
        }
        for (int i = 0; i < branchIds.length; i++) {
            /**根据支路查询关联电表*/
            List<AthenaBranchMeterLink> branchMeterLinks = branchMeterLinkMapper.selectAthenaBranchMeterLinkListInfo(branchIds[i]);
            ReportFormResult result = new ReportFormResult();
            for (AthenaBranchMeterLink branchMeterLink : branchMeterLinks) {
                branchMeterLink = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, branchMeterLink.getId());
                /**根据电表名称查询电表数据*/
                List<Map> mapList = branchMeterLinkMapper.selectBranchReportForm(branchMeterLink.getSysName(),  DateUtil.format(time[0],"yyyy-MM-dd HH:mm:ss"),  DateUtil.format(time[1],"yyyy-MM-dd HH:mm:ss"));
              if(mapList.size()>0)
              {
                  /**组合电表数据*/
                  if(branchMeterLink.getOperator().equals("0")){
                      result.setStartValue(result.getStartValue() - (Double) mapList.get(0).get("mindata"));
                      result.setEndValue(result.getEndValue() - (Double) mapList.get(0).get("maxdata"));
                  }else{
                      result.setStartValue(result.getStartValue() + (Double) mapList.get(0).get("mindata"));
                      result.setEndValue(result.getEndValue() + (Double) mapList.get(0).get("maxdata"));
                  }
              }
            }
            result.setStartTime( DateUtil.format(time[0],"yyyy-MM-dd HH:mm:ss"));
            result.setEndTime(DateUtil.format(time[1],"yyyy-MM-dd HH:mm:ss"));
            AthenaBranchConfig branch = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, branchIds[i]);
            result.setEnergyname(branch.getBranchName());
            /**计算差值*/
            result.setDifference(result.getEndValue()-result.getStartValue());
//            if(result.getEndValue()==0&&result.getStartValue()==0){
//                result.setEndValue(null);
//                result.setStartValue(null);
//            }
            resultList.add(result);
        }
//        System.out.println(resultList);
        return AjaxResult.success(resultList);
    }
}
