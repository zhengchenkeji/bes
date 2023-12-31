package com.zc.efounder.JEnterprise.service.electricPowerTranscription.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.zc.ApplicationContextProvider;
import com.zc.efounder.JEnterprise.Cache.BranchConfigCache;
import com.zc.efounder.JEnterprise.Cache.MeterCache;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.PowerData;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.mapper.electricPowerTranscription.PowerDataMapper;
import com.zc.efounder.JEnterprise.service.electricPowerTranscription.PowerDataService;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaojikun
 * @Description 用能概况Service业务层处理
 */
@Service
public class PowerDataServiceImpl implements PowerDataService {

    @Resource
    private PowerDataMapper powerDataMapper;
    @Resource
    RedisCache redisCache;
    @Resource
    BranchConfigCache branchConfigCache;

    //电表缓存
    private MeterCache meterCache = ApplicationContextProvider.getBean(MeterCache.class);

    /**
     * 查询所有园区
     */
    @Override
    public List<Park> getAllPark() {
        return powerDataMapper.getAllPark();
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
            if (energyConfig.getParkCode().equals(item.getParkCode())) {
                energyTypeCache.values().forEach(itemtype -> {
                    if (itemtype.getCode().equals(item.getEnergyCode())) {
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
     * 查询支路下电表/设备
     */
    @Override
    public AjaxResult getCheckMeterList(PowerData powerData) {
        List<Map<String, String>> list = powerDataMapper.getCheckMeterList(powerData);
        List<Map<String, String>> listEquipment = powerDataMapper.getCheckMeterListEquipment(powerData);
        list.addAll(listEquipment);
        if (list.size() > 0) {
            return AjaxResult.success(list);
        } else {
            return AjaxResult.error("该支路无电表数据");
        }

    }

    @Override
    public AjaxResult getAllCheckMeterParamsList(PowerData powerData) {
        //根据支路ID查询支路关联的采集参数配置
        AthenaBranchConfig athenaBranchConfig = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, Long.parseLong(powerData.getBranchId()));;
        if(athenaBranchConfig.getParamsId() == null){
            List<Map<String, String>> list = new ArrayList<>();
            list.add(new HashMap<String, String>() {{
                put("id", PointPowerParam.Point_Meter_Code);
                put("name", PointPowerParam.Point_Meter_Name);
            }});
            return AjaxResult.success(list);
        }
        String[] split = athenaBranchConfig.getParamsId().split(",");
        List<Map<String, String>> list = powerDataMapper.getCheckMeterParamsList(split);
        list.add(new HashMap<String, String>() {{
            put("id", PointPowerParam.Point_Meter_Code);
            put("name", PointPowerParam.Point_Meter_Name);
        }});
        if (list.size() > 0) {
            return AjaxResult.success(list);
        } else {
            return AjaxResult.error("无采集参数");
        }
    }

    /**
     * 查询支路下电表/设备下绑定采集参数/数据项
     */
    @Override
    public AjaxResult getMeterParamsList(PowerData powerData) {
        if (!"3".equals(powerData.getMeterType())) {
            //根据id获取或电表ID
            AthenaBranchMeterLink athenaBranchMeterLink = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, powerData.getMeterId());
            powerData.setMeterId(String.valueOf(athenaBranchMeterLink.getMeterId()));
        }

        List<Map<String, Object>> list = new ArrayList<>();
        if (powerData.getMeterType() != null && "1".equals(powerData.getMeterType())
            /*|| powerData.getParamsId().equals(PointPowerParam.Point_Meter_Code)*/) {
            HashMap<String, Object> map = new HashMap<String, Object>() {{
                put("id", PointPowerParam.Point_Meter_Code);
                put("name", PointPowerParam.Point_Meter_Name);
                put("type", "0");
            }};
            list.add(map);
            return AjaxResult.success(list);
        } else if (powerData.getMeterType() != null && "0".equals(powerData.getMeterType())) {
            int paramsNumber = 0;
            //查询点电表绑定的采集参数
            AthenaElectricMeter meter = meterCache.getMeterByMeterId(Integer.parseInt(powerData.getMeterId()));
            int collId = meter.getCollectionMethodCode().intValue();
            //获取采集方案下的所有采集参数Id
            for (Object value : redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl).values()) {
                ElectricCollRlgl electricCollRlgl = (ElectricCollRlgl) value;
                if (electricCollRlgl.getCollId() == collId) {
                    paramsNumber++;
                    ElectricParams electricParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, (long) electricCollRlgl.getElectricId());
                    list.add(new HashMap<String, Object>() {{
//                        put("id", electricCollRlgl.getElectricCode());
                        put("id", electricCollRlgl.getElectricId());
                        put("name", electricParams.getName());
                        put("type", "1");
                    }});
                }
            }
            if (paramsNumber > 1) {
                list.add(0, new HashMap<String, Object>() {{
                    put("id", "0");
                    put("name", "全部");
                    put("type", "1");
                }});
            }
            //id name
            return AjaxResult.success(list);
        } else {
            //第三方设备
            String electricParam = powerData.getElectricParam();
            String[] electricParamStringArr = electricParam.split(",");
            for (String itemDataId : electricParamStringArr) {
                ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, Long.parseLong(itemDataId));
                if (productItemData != null) {
                    HashMap<String, Object> map = new HashMap<String, Object>() {{
                        put("id", productItemData.getId());
                        put("name", productItemData.getName());
                        put("type", "3");
                    }};
                    list.add(map);
                }
            }
            if (list != null && list.size() > 1) {
                list.add(0, new HashMap<String, Object>() {{
                    put("id", "0");
                    put("name", "全部");
                    put("type", "3");
                }});
            }
            //id name
            return AjaxResult.success(list);
        }
    }

    @Override
    public AjaxResult getMeterParamsConfigList(PowerData powerData) {
        List<Map<String, Object>> list = new ArrayList<>();
        if ((powerData.getMeterType() != null && "1".equals(powerData.getMeterType()))
                || powerData.getParamsId().equals(PointPowerParam.Point_Meter_Code)) {
            HashMap<String, Object> map = new HashMap<String, Object>() {{
                put("id", PointPowerParam.Point_Meter_Code);
                put("name", PointPowerParam.Point_Meter_Name);
                put("type", "0");
            }};
            list.add(map);
            return AjaxResult.success(list);
        } else {
            list = powerDataMapper.getMeterParamsList(powerData);
            if (list.size() > 0) {
                for (Map<String, Object> map : list) {
                    if("0".equals(map.get("type").toString())){
                        ElectricParams electricParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, Long.parseLong(String.valueOf(map.get("id"))));
                        map.put("name", electricParams.getName());
                        map.put("type", "1");
                    }else{
                        ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, Long.parseLong(String.valueOf(map.get("id"))));
                        map.put("name", productItemData.getName());
                        map.put("type", "3");
                    }
                }
                return AjaxResult.success(list);
            } else {
                return AjaxResult.error("无采集参数");
            }
        }
    }


    @Override
    public AjaxResult getMeterParams(PowerData powerData) {
        List<Map<String, String>> list = powerDataMapper.getMeterParams(powerData);
        if (list.size() > 0) {
            return AjaxResult.success(list);
        } else {
            return AjaxResult.error("该电表未绑定采集方案");
        }
    }


    /**
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 日原始数据图表
     * @author gaojikun
     */
    @Override
    public AjaxResult queryDayChartsData(PowerData powerData) {
        //根据id获取或电表ID
        AthenaBranchMeterLink athenaBranchMeterLink = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, powerData.getMeterId());
        powerData.setMeterId(String.valueOf(athenaBranchMeterLink.getMeterId()));

        if (!StringUtils.hasText(powerData.getBranchId()) || !StringUtils.hasText(powerData.getMeterId())) {
            return AjaxResult.error("参数错误");
        }

        //查询数据
        List<Map<String, Object>> queryDataList = queryDayData(powerData);

        List<Map<String, Object>> returnList = new ArrayList<>();
        //组装数据
        String[] paramsArr = powerData.getParamsIdStr().split("-");
        if (queryDataList.size() > 0) {

            for (String paramsId : paramsArr) {
                List<String> timeList = new ArrayList<>();
                Map<String, Object> returnData = new HashMap<>();
                List<String> dataList = new ArrayList<>();
                for (Map<String, Object> map : queryDataList) {
                    //名称
                    if (returnData.get("name") == null) {
                        if(!"3".equals(powerData.getMeterType())){
                            ElectricParams electricParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, Long.parseLong(paramsId));
                            if (electricParams == null && paramsId.equals(PointPowerParam.Point_Meter_Code)) {
                                returnData.put("name", PointPowerParam.Point_Meter_Name);
                            } else {
                                returnData.put("name", electricParams.getName());
                            }
                        }else{
                            ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, Long.parseLong(paramsId));
                            returnData.put("name", productItemData.getName());
                        }
                    }

                    //数据
                    for (String key : map.keySet()) {
                        if (key.contains(paramsId)) {
                            dataList.add(map.get(key).toString());
                        }
                    }
                    //采集时间
                    timeList.add(map.get("CJSJ").toString());
                }
                returnData.put("data", dataList);
                returnData.put("CJSJ", timeList);
                returnList.add(returnData);
            }
        }

        return AjaxResult.success(returnList);
    }

    /**
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 日原始数据表格
     * @author gaojikun
     */
    @Override
    public AjaxResult queryDayTableData(PowerData powerData) {
        if (!StringUtils.hasText(powerData.getBranchId()) || !StringUtils.hasText(powerData.getMeterId())) {
            return AjaxResult.error("参数错误");
        }
        List<Map<String, Object>> returnList = queryDayData(powerData);
        return AjaxResult.success(returnList);
    }

    @Override
    public List<Map<String, Object>> queryDayExport(PowerData powerData) {
        if (powerData.getParamsIdStr() == null) {
            return null;
        }
        String[] paramsSort = powerData.getParamsIdStr().split("-");
        List<Map<String, Object>> dataList = queryDayData(powerData);
        if (dataList == null) {
            return null;
        }
        List<Map<String, Object>> returnList = new ArrayList<>();
        //组装数据
        for (Map<String, Object> map : dataList) {
            Map<String, Object> addMap = new LinkedHashMap<>();
            //先添加采集时间
            addMap.put("CJSJ", map.get("CJSJ").toString());
            //再顺序添加采集数据
            for (String paramsId : paramsSort) {
                for (String key : map.keySet()) {
                    if ("CJSJ".equals(key)) {
                        continue;
                    }
                    //根据顺序添加
                    if (paramsId.equals(key.substring(8,key.length()-1))) {
                        addMap.put(key, map.get(key));
                    }
                }
            }
            returnList.add(addMap);
        }
        //倒序
        returnList.sort((m1, m2) -> {
            return DateUtils.parseDate(String.valueOf(m2.get("CJSJ"))).compareTo(DateUtils.parseDate(String.valueOf(m1.get("CJSJ"))));
        });
        return returnList;
    }

    //查询电表参数数据
    public List<Map<String, Object>> queryDayData(PowerData powerData) {
        if (powerData.getParamsIdStr() == null) {
            return null;
        }
        //电表参数列表
        String[] paramsArr = powerData.getParamsIdStr().split("-");
//        List<String> paramsCodeArr = new ArrayList<>();
        List<Map<String, Object>> returnList = new ArrayList<>();
        List<Map<String, Object>> combine = new ArrayList<>();

        //5分钟一个点位 查询电表原始数据
        List<Map<String, Object>> dataList = new ArrayList<>();

        //遍历查询采集参数
        for (String paramsId : paramsArr) {
            if(!"3".equals(powerData.getMeterType())){
                ElectricParams electricParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, Long.parseLong(paramsId));
                //默认采集参数
                if (electricParams == null && paramsId.equals(PointPowerParam.Point_Meter_Code)) {
                    powerData.setParamsId(paramsId);
//                    paramsCodeArr.add(paramsId);
                } else {
                    //电表绑定采集参数
                    powerData.setParamsId(electricParams.getCode());
//                    paramsCodeArr.add(electricParams.getCode());
                }

                dataList = powerDataMapper.queryDayChartsData(powerData);
            }else{
                //设备绑定采集参数
                powerData.setParamsId(paramsId);
//                paramsCodeArr.add(paramsId);

                dataList = powerDataMapper.queryDayChartsDataOther(powerData);
            }



            if (dataList.size() == 0) {
                continue;
            }
            //设备key值
            for (Map<String, Object> dataMap : dataList) {
                Map<String, Object> addMap = new HashMap();
                for (String key : dataMap.keySet()) {
                    //判断是否有key
                    String info = dataMap.get(key).toString();
                    String putKey = "";
                    if ("dataInfo".equals(key)) {
                        putKey = key + paramsId + powerData.getMeterType();
                    } else {
                        putKey = key;
                    }
                    addMap.put(putKey, info);
                }
                returnList.add(addMap);
            }
        }
        if (returnList.size() > 0) {
            combine = returnList.stream()
                    .collect(Collectors.groupingBy(group -> group.get("CJSJ").toString())) // 根据map中id的value值进行分组, 这一步的返回结果Map<String,List<Map<String, Object>>>，目的是将相同id下的value归类到一个value下
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

            //组装完成的数据进行校检，将缺失的数据赋值0
            for (Map<String, Object> map : combine) {
                for (String paramsId : paramsArr) {
                    String key = "dataInfo" + paramsId + powerData.getMeterType();
                    boolean flag = map.containsKey(key);
                    if (!flag) {
                        map.put(key, "0");
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(combine)) {
                combine.sort((m1, m2) -> {
                    return DateUtils.parseDate(String.valueOf(m1.get("CJSJ"))).compareTo(DateUtils.parseDate(String.valueOf(m2.get("CJSJ"))));
                });
            }

            return combine;
        } else {
            return combine;
        }
    }

    /**
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 逐日极值数据
     * @author gaojikun
     */
    @Override
    public AjaxResult queryMaxChartsData(PowerData powerData) {
        if (!StringUtils.hasText(powerData.getBranchId()) || !StringUtils.hasText(powerData.getMeterId())) {
            return AjaxResult.error("参数错误");
        }

        //返回数据
        List<Map<String, Object>> dataList = new ArrayList<>();

        if(!"3".equals(powerData.getMeterType())){
            ElectricParams electricParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, Long.parseLong(powerData.getParamsItemId()));
            if (electricParams == null && powerData.getParamsId().equals(PointPowerParam.Point_Meter_Code)) {
                powerData.setParamsItemId(powerData.getParamsId());
            } else {
                powerData.setParamsItemId(electricParams.getCode());
            }

            dataList = powerDataMapper.queryMaxChartsData(powerData);
        }else{
            powerData.setParamsItemId(powerData.getParamsItemId());

            dataList = powerDataMapper.queryMaxChartsDataOther(powerData);
        }

        return AjaxResult.success(dataList);
    }

    @Override
    public AjaxResult queryMaxTableData(PowerData powerData) {
        if (!StringUtils.hasText(powerData.getBranchId()) || !StringUtils.hasText(powerData.getMeterId())) {
            return AjaxResult.error("参数错误");
        }
        String getParamsItemId = powerData.getParamsItemId();
        ElectricParams electricParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, Long.parseLong(getParamsItemId));
        if (electricParams == null && powerData.getParamsItemId().equals(PointPowerParam.Point_Meter_Code)) {
            powerData.setParamsItemId(powerData.getParamsItemId());
        } else {
            powerData.setParamsItemId(electricParams.getCode());
        }
        List<Map<String, Object>> dataList = powerDataMapper.queryMaxChartsData(powerData);

        return AjaxResult.success(dataList);
    }

    @Override
    public List<Map<String, Object>> queryMaxExport(PowerData powerData) {
        String getParamsItemId = powerData.getParamsItemId();
        ElectricParams electricParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, Long.parseLong(getParamsItemId));
        if (electricParams == null && powerData.getParamsItemId().equals(PointPowerParam.Point_Meter_Code)) {
            powerData.setParamsItemId(powerData.getParamsItemId());
        } else {
            powerData.setParamsItemId(electricParams.getCode());
        }
        List<Map<String, Object>> dataList = powerDataMapper.queryMaxChartsData(powerData);

        List<Map<String, Object>> returnList = new ArrayList<>();
        for (Map<String, Object> map : dataList) {
            Map<String, Object> addMap = new LinkedHashMap<>();
            addMap.put("CJSJ", map.get("CJSJ").toString());
            addMap.put("maxInfo", map.get("maxInfo").toString());
            addMap.put("maxVTime", map.get("maxVTime").toString());
            addMap.put("minInfo", map.get("minInfo").toString());
            addMap.put("minVTime", map.get("minVTime").toString());
            addMap.put("avgInfo", map.get("avgInfo").toString());
            returnList.add(addMap);
        }

        //倒序
        returnList.sort((m1, m2) -> {
            return DateUtils.parseDate(String.valueOf(m2.get("CJSJ"))).compareTo(DateUtils.parseDate(String.valueOf(m1.get("CJSJ"))));
        });
        return returnList;
    }


    //获取昨日上月去年时间
    public String dataType(String CJSJ) {
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
        return null;
    }

}