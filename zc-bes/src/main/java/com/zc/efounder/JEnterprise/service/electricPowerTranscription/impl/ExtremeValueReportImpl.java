package com.zc.efounder.JEnterprise.service.electricPowerTranscription.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.ExtremeValueReport;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.ExtremeValueReportParam;
import com.zc.efounder.JEnterprise.mapper.electricPowerTranscription.ExtremeValueReportMapper;
import com.zc.efounder.JEnterprise.service.electricPowerTranscription.ExtremeValueReportService;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaojikun
 * @Description 用能概况Service业务层处理
 */
@Service
public class ExtremeValueReportImpl implements ExtremeValueReportService {

    @Resource
    private ExtremeValueReportMapper extremeValueReportMapper;
    @Resource
    RedisCache redisCache;

    /**
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 查询极值数据
     * @author gaojikun
     */
    @Override
    public List<Map<String, Object>> queryDayChartsDataList(ExtremeValueReport extremeValueReport) {
        List<ExtremeValueReportParam> paramsList = extremeValueReport.getParamsIdStr();
        List<Map<String, Object>> returnList = new ArrayList<>();
        List<Map<String, Object>> combine = new ArrayList<>();

        //遍历查询采集参数
        for (ExtremeValueReportParam map : paramsList) {
            String paramsId = map.getId();
            String type = map.getType();
            ElectricParams electricParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, Long.parseLong(paramsId));
            if (electricParams == null && paramsId.equals(PointPowerParam.Point_Meter_Code)) {
                extremeValueReport.setParamsId(paramsId);
            } else if ("1".equals(type)) {
                extremeValueReport.setParamsId(electricParams.getCode());
            } else {
                extremeValueReport.setParamsId(paramsId);
            }

            //根据支路ID,参数Code查询电表
            List<Map<String, Object>> meterMapList = new ArrayList<>();
            if(!"3".equals(type)){
                meterMapList = extremeValueReportMapper.queryMeterList(extremeValueReport);
            }
            //根据支路ID,数据项ID查询设备
            List<Map<String, Object>> meterMapListOther = new ArrayList<>();
            if("3".equals(type)) {
                meterMapListOther = extremeValueReportMapper.queryEquipmentList(extremeValueReport);
                Iterator<Map<String, Object>> iterator = meterMapListOther.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> item = iterator.next();
                    //参数不包含该参数则删除
                    if (item.get("params") == null || "".equals(item.get("params").toString())) {
                        iterator.remove();
                    }
                    String[] stringArr = item.get("params").toString().split(",");
                    List containsList = Arrays.asList(stringArr);
                    if(!containsList.contains(extremeValueReport.getParamsId())){
                        iterator.remove();
                    }
                }
            }
            //查询全部电表数据
            List<Map<String, Object>> dataList = new ArrayList<>();
            if (meterMapList.size() == 0 && meterMapListOther.size() == 0) {
                continue;
            }
            List<String> meterList = new ArrayList<>();
            if(meterMapList.size() > 0){
                meterMapList.forEach(iteam -> {
                    meterList.add(iteam.get("code").toString());
                });
                extremeValueReport.setMeterList(meterList);
                List<Map<String, Object>> meterDataList = extremeValueReportMapper.queryDayChartsData(extremeValueReport);
                dataList.addAll(meterDataList);
            }
            if(meterMapListOther.size() > 0){
                meterMapListOther.forEach(iteam -> {
                    meterList.add(iteam.get("code").toString());
                });
                extremeValueReport.setMeterList(meterList);
                List<Map<String, Object>> equipmentDataList = extremeValueReportMapper.queryDayChartsDataOther(extremeValueReport);
                dataList.addAll(equipmentDataList);
            }

//            if (dataList.size() == 0) {
//                continue;
//            }
            //电表没有数据则补充--
            if (dataList.size() < (meterMapList.size() + meterMapListOther.size())) {
                meterMapList.forEach(meterMap -> {
                    boolean iscontainsValue = false;
                    a:
                    for (Map containsMap : dataList) {
                        iscontainsValue = containsMap.containsValue(meterMap.get("code").toString());
                        if (iscontainsValue) {
                            break a;
                        }
                    }
                    if (!iscontainsValue) {
                        dataList.add(new HashMap<String, Object>() {{
                            put("code", meterMap.get("code").toString());
                            put("name", meterMap.get("name").toString());
                            put("maxInfo", "--");
                            put("maxTime", "--");
                            put("minInfo", "--");
                            put("minTime", "--");
                            put("avgInfo", "--");
                        }});
                    }
                });
                meterMapListOther.forEach(meterMap -> {
                    boolean iscontainsValue = false;
                    a:
                    for (Map containsMap : dataList) {
                        iscontainsValue = containsMap.containsValue(meterMap.get("code").toString());
                        if (iscontainsValue) {
                            break a;
                        }
                    }
                    if (!iscontainsValue) {
                        dataList.add(new HashMap<String, Object>() {{
                            put("code", meterMap.get("code").toString());
                            put("name", meterMap.get("name").toString());
                            put("maxInfo", "--");
                            put("maxTime", "--");
                            put("minInfo", "--");
                            put("minTime", "--");
                            put("avgInfo", "--");
                        }});
                    }
                });
            }

            //设备key值 组装列表
            for (Map<String, Object> dataMap : dataList) {
                Map<String, Object> addMap = new HashMap();
                for (String key : dataMap.keySet()) {
                    //判断是否有key
                    String info = dataMap.get(key).toString();
                    String putKey = "";
                    if ("maxInfo".equals(key) || "maxTime".equals(key) || "minInfo".equals(key) || "minTime".equals(key) || "avgInfo".equals(key)) {
                        putKey = key + paramsId + type;
                    } else {
                        putKey = key;
                    }
                    addMap.put(putKey, info);
                }
                returnList.add(addMap);
            }
        }

        //list流组装数据
        if (returnList.size() > 0) {
            combine = returnList.stream()
                    .collect(Collectors.groupingBy(group -> group.get("code").toString())) // 根据map中id的value值进行分组, 这一步的返回结果Map<String,List<Map<String, Object>>>，目的是将相同id下的value归类到一个value下
                    .entrySet() // 得到Set<Map.Entry<String, List<Map<String, Object>>>
                    .stream() // 使用Java8的流
                    .map(m -> { // 进入映射环境
                        // m.getValue()的结果是 List<Map<String, Object>>
                        Map<String, Object> collect = m.getValue().stream()
                                // 核心重点！o.entrySet() 的结果是 Set<Map.Entry<String, Object>>，通过flatMap将value合并成一个stream
                                .flatMap(o -> o.entrySet().stream())
                                // (m1, m2) -> m2 的意思是如果 m1 == m2 则使用m2
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (m1, m2) -> m2));
                        return collect;
                    }).collect(Collectors.toList());
            return combine;
        } else {
            return combine;
        }
    }

    //查询极值报表表格
    @Override
    public AjaxResult queryDayChartsData(ExtremeValueReport extremeValueReport) {
        List<Map<String, Object>> combine = queryDayChartsDataList(extremeValueReport);
        return AjaxResult.success(combine);
    }

}