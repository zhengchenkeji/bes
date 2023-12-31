package com.zc.efounder.JEnterprise.service.electricPowerTranscription.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.Cache.BranchMeterLinkCache;
import com.zc.efounder.JEnterprise.Cache.ElectricCollRlglCache;
import com.zc.efounder.JEnterprise.Cache.ElectricParamsCache;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.ElectricityStatement;
import com.zc.efounder.JEnterprise.mapper.electricPowerTranscription.ElectricityStatementMapper;
import com.zc.efounder.JEnterprise.service.electricPowerTranscription.ElectricityStatementService;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liuwenge
 * @Description 电力报表Service业务层处理
 * @date 2022/11/9 10:48
 */
@Service
public class ElectricityStatementServiceImpl implements ElectricityStatementService {

    @Resource
    private ElectricityStatementMapper electricityStatementMapper;
    @Resource
    private BranchMeterLinkCache branchMeterLinkCache;
    @Resource
    private ElectricCollRlglCache electricCollRlglCache;
    @Resource
    private ElectricParamsCache electricParamsCache;
    @Resource
    private RedisCache redisCache;

    /**
     * 查询所有园区
     */
    @Override
    public List<Park> getAllPark() {
        return electricityStatementMapper.getAllPark();
    }


    /**
     * 查询数据
     */
    @Override
    public AjaxResult queryData(ElectricityStatement electricityStatement) {
        electricityStatement.setDateTime(electricityStatement.getDateTime().substring(0, 13));
        //获取所有选择的支路ID
        List<Long> branchIds = electricityStatement.getBranchIds();
        String dateTime = electricityStatement.getDateTime();
        if (branchIds.size() < 1 || !StringUtils.hasText(dateTime)) {
            return AjaxResult.error("参数错误");
        }
        Map<String, Object> result = new HashMap<>();
        //查询数据
        result = queryDataInfo(branchIds, result, dateTime);

        return AjaxResult.success(result);

    }

    //电力集抄查询数据
    private Map<String, Object> queryDataInfo(List<Long> branchIds, Map<String, Object> result, String dateTime) {
        List<Map<String, Object>> column = new ArrayList<>();
        List<Map<String, Object>> resultData = new ArrayList<>();
        //系统名称
        List<String> sysNameAllList = new ArrayList<>();
        //电表ID
        List<String> meterIdAllList = new ArrayList<>();
        //设备ID
        List<String> equipmentAllList = new ArrayList<>();
        //数据项ID
        List<String> itemDataAllList = new ArrayList<>();
        //先查出 支路下面包含的电表 + 设备
        for (Long branchId : branchIds) {
            Map<String, List<String>> meterInfo = branchMeterLinkCache.getMeterByBranchId(branchId);
            if (meterInfo != null) {
                List<String> sysNameList = meterInfo.get("sysNameList");
                sysNameAllList.addAll(sysNameList);
                List<String> meterIdList = meterInfo.get("meterIdList");
                meterIdAllList.addAll(meterIdList);
                List<String> equipmentList = meterInfo.get("equipmentList");
                equipmentAllList.addAll(equipmentList);
                List<String> itemDataList = meterInfo.get("itemDataList");
                itemDataAllList.addAll(itemDataList);
            }
        }
        //去重后的电表名称列表
        List<String> sysNameList = sysNameAllList.stream().distinct().collect(Collectors.toList());
        //去重后的电表id列表
        List<String> meterIdList = meterIdAllList.stream().distinct().collect(Collectors.toList());
        //去重后的设备id列表
        List<String> equipmentList = equipmentAllList.stream().distinct().collect(Collectors.toList());
        //去重后的数据项id列表
        List<String> itemDataList = itemDataAllList.stream().distinct().collect(Collectors.toList());

        if (meterIdList.size() > 0 || itemDataList.size() > 0) {

            if (meterIdList.size() > 0) {
                //通过电表id查出所有的采集方案id
                List<String> collMethodCodeList = electricityStatementMapper.queryCollMethodCode(meterIdList);
                //组装table表头
                for (String collMethodCode : collMethodCodeList) {
                    if (collMethodCode.equals("0")) {
                        Map<String, Object> pointParam = new HashMap<>();
                        pointParam.put("code", PointPowerParam.Point_Meter_Code);
                        pointParam.put("name", "点位默认参数");
                        column.add(pointParam);
                    } else {
                        List<ElectricCollRlgl> electricCollRlgl = electricCollRlglCache.getElectricCollRlglByCollId(Integer.parseInt(collMethodCode));
                        Map<String, Object> meterParam;
                        for (ElectricCollRlgl collRlgl : electricCollRlgl) {
                            meterParam = new HashMap<>();
                            ElectricParams electricParams = electricParamsCache.getCacheByEnergyCode(collRlgl.getElectricCode());
                            //缓存中没有的话从数据库中查询,如果查到了则更新数据库
                            if (electricParams == null) {
                                ElectricParams electricParams1 = electricityStatementMapper.queryElectricParams(collRlgl.getElectricCode());
                                if (electricParams1 != null) {
                                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, electricParams1.getId(), electricParams1);
                                    meterParam.put("code", collRlgl.getElectricCode());
                                    meterParam.put("name", electricParams1.getName());
                                    column.add(meterParam);
                                }
                            } else {
                                meterParam.put("code", collRlgl.getElectricCode());
                                meterParam.put("name", electricParams.getName());
                                column.add(meterParam);
                            }
                        }
                    }
                }
            }
            if (itemDataList.size() > 0) {
                //组装table表头
                for (String itemDataId : itemDataList) {
                    //查询数据项
                    ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, itemDataId);
                    Map<String, Object> pointParam = new HashMap<>();
                    pointParam.put("code", productItemData.getId());
                    pointParam.put("name", productItemData.getName());
                    column.add(pointParam);
                }
            }

            //通过电表名称查出所有的电表数据
            List<Map<String, Object>> dataList = new ArrayList<>();
            List<Map<String, Object>> dataListOther = new ArrayList<>();
            if (meterIdList.size() > 0) {
                dataList = electricityStatementMapper.queryData(sysNameList, dateTime);
            }
            if (itemDataList.size() > 0) {
                dataListOther = electricityStatementMapper.queryDataOther(equipmentList, dateTime);
            }
            //所有的数据
            dataList.addAll(dataListOther);

            Map<String, Map<String, Object>> allMeter = new HashMap<>();

            //先组装所有的电表数据, 数据直接在这里面取
            for (Map<String, Object> data : dataList) {
                String meterName = data.get("F_DBSYS_NAME").toString();
                String electricCode = data.get("F_DNBH").toString();
                String electricData = data.get("F_DATA").toString();
                Map<String, Object> meterInfo;
                if (allMeter.containsKey(meterName)) {
                    meterInfo = allMeter.get(meterName);

                } else {
                    meterInfo = new HashMap<>();
                }
                meterInfo.put(electricCode, electricData);

                allMeter.put(meterName, meterInfo);
            }

            //循环支路,根据支路id查出所有的
            for (Long branchId : branchIds) {
                //获取支路名称
                AthenaBranchConfig athenaBranchConfig = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, branchId);
                String branchName = athenaBranchConfig.getBranchName();

                //查询支路下的电表
                Map<String, List<String>> meterInfo = branchMeterLinkCache.getMeterByBranchId(branchId);
                List<String> meterSysNameList = meterInfo.get("sysNameList");
                List<String> equipmentListTwo = meterInfo.get("equipmentList");
                if (meterSysNameList.size() > 0) {
                    Map<String, Object> meterRow;
                    for (String meterSysName : meterSysNameList) {
                        meterRow = new HashMap<>();
                        meterRow.put("branchName", branchName);
                        meterRow.put("meterName", meterSysName);

                        if (allMeter.containsKey(meterSysName)) {
                            Map<String, Object> meter = allMeter.get(meterSysName);
                            for (Map<String, Object> map : column) {
                                String code = map.get("code").toString();
                                if (meter.containsKey(code)) {
                                    meterRow.put(code, meter.get(code));
                                } else {
                                    meterRow.put(code, "--");
                                }
                            }
                        } else {
                            for (Map<String, Object> map : column) {
                                String code = map.get("code").toString();
                                meterRow.put(code, "--");
                            }
                        }

                        resultData.add(meterRow);
                    }
                } else {
                    Map<String, Object> meterRow = new HashMap<>();
                    meterRow.put("branchName", branchName);
                    meterRow.put("meterName", "--");
                    for (Map<String, Object> map : column) {
                        String code = map.get("code").toString();
                        meterRow.put(code, "--");
                    }
                    resultData.add(meterRow);
                }

                if (equipmentListTwo.size() > 0) {
                    Map<String, Object> meterRow;
                    for (String meterSysName : equipmentListTwo) {
                        meterRow = new HashMap<>();
                        meterRow.put("branchName", branchName);
                        meterRow.put("meterName", meterSysName);

                        if (allMeter.containsKey(meterSysName)) {
                            Map<String, Object> meter = allMeter.get(meterSysName);
                            for (Map<String, Object> map : column) {
                                String code = map.get("code").toString();
                                if (meter.containsKey(code)) {
                                    meterRow.put(code, meter.get(code));
                                } else {
                                    meterRow.put(code, "--");
                                }
                            }
                        } else {
                            for (Map<String, Object> map : column) {
                                String code = map.get("code").toString();
                                meterRow.put(code, "--");
                            }
                        }

                        resultData.add(meterRow);
                    }
                }

            }
        } else {
            for (Long branchId : branchIds) {
                //获取支路名称
                AthenaBranchConfig athenaBranchConfig = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, branchId);
                String branchName = athenaBranchConfig.getBranchName();

                Map<String, Object> meterRow = new HashMap<>();
                meterRow.put("branchName", branchName);
                meterRow.put("meterName", "--");
                for (Map<String, Object> map : column) {
                    String code = map.get("code").toString();
                    meterRow.put(code, "--");
                }
                resultData.add(meterRow);

            }
        }

        result.put("column", column);
        result.put("resultData", resultData);

        return result;
    }

    /**
     * 查询数据
     */
    @Override
    public Map<String, List<Map<String, Object>>> exportTable(ElectricityStatement electricityStatement) {
        electricityStatement.setDateTime(electricityStatement.getDateTime().substring(0, 13));
        List<Long> branchIds = electricityStatement.getBranchIds();
        String dateTime = electricityStatement.getDateTime();
        if (branchIds.size() < 1 || !StringUtils.hasText(dateTime)) {
            return null;
        }
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        List<Map<String, Object>> column = new ArrayList<>();
        List<Map<String, Object>> resultData = new ArrayList<>();

        List<String> sysNameAllList = new ArrayList<>();
        List<String> meterIdAllList = new ArrayList<>();
        //设备ID
        List<String> equipmentAllList = new ArrayList<>();
        //数据项ID
        List<String> itemDataAllList = new ArrayList<>();
        //先查出 支路下面包含的电表
        for (Long branchId : branchIds) {

            Map<String, List<String>> meterInfo = branchMeterLinkCache.getMeterByBranchId(branchId);
            List<String> sysNameList = meterInfo.get("sysNameList");
            sysNameAllList.addAll(sysNameList);

            List<String> meterIdList = meterInfo.get("meterIdList");
            meterIdAllList.addAll(meterIdList);

            List<String> equipmentList = meterInfo.get("equipmentList");
            equipmentAllList.addAll(equipmentList);

            List<String> itemDataList = meterInfo.get("itemDataList");
            itemDataAllList.addAll(itemDataList);
        }
        //去重后的电表名称列表
        List<String> sysNameList = sysNameAllList.stream().distinct().collect(Collectors.toList());
        //去重后的电表id列表
        List<String> meterIdList = meterIdAllList.stream().distinct().collect(Collectors.toList());
        //去重后的电表id列表
        List<String> equipmentList = equipmentAllList.stream().distinct().collect(Collectors.toList());
        //去重后的电表id列表
        List<String> itemDataList = itemDataAllList.stream().distinct().collect(Collectors.toList());

        if (meterIdList.size() > 0 || itemDataList.size() > 0) {

            if (meterIdList.size() > 0) {
                //通过电表id查出所有的采集方案id
                List<String> collMethodCodeList = electricityStatementMapper.queryCollMethodCode(meterIdList);

                //组装table表头
                for (String collMethodCode : collMethodCodeList) {
                    if (collMethodCode.equals("0")) {
                        Map<String, Object> pointParam = new HashMap<>();
                        pointParam.put("code", "1999999");
                        pointParam.put("name", "点位默认参数");
                        column.add(pointParam);
                    } else {
                        List<ElectricCollRlgl> electricCollRlgl = electricCollRlglCache.getElectricCollRlglByCollId(Integer.parseInt(collMethodCode));
                        Map<String, Object> meterParam;
                        for (ElectricCollRlgl collRlgl : electricCollRlgl) {
                            meterParam = new HashMap<>();
                            ElectricParams electricParams = electricParamsCache.getCacheByEnergyCode(collRlgl.getElectricCode());
                            //缓存中没有的话从数据库中查询,如果查到了则更新数据库
                            if (electricParams == null) {
                                ElectricParams electricParams1 = electricityStatementMapper.queryElectricParams(collRlgl.getElectricCode());
                                if (electricParams1 != null) {
                                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, electricParams1.getId(), electricParams1);
                                    meterParam.put("code", collRlgl.getElectricCode());
                                    meterParam.put("name", electricParams1.getName());
                                    column.add(meterParam);
                                }
                            } else {
                                meterParam.put("code", collRlgl.getElectricCode());
                                meterParam.put("name", electricParams.getName());
                                column.add(meterParam);
                            }
                        }
                    }
                }
            }
            if (itemDataList.size() > 0) {
                //组装table表头
                for (String itemDataId : itemDataList) {
                    //查询数据项
                    ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, itemDataId);
                    Map<String, Object> pointParam = new HashMap<>();
                    pointParam.put("code", productItemData.getId());
                    pointParam.put("name", productItemData.getName());
                    column.add(pointParam);
                }
            }

            //通过电表名称查出所有的电表数据
            List<Map<String, Object>> dataList = new ArrayList<>();
            List<Map<String, Object>> dataListOther = new ArrayList<>();
            if (meterIdList.size() > 0) {
                dataList = electricityStatementMapper.queryData(sysNameList, dateTime);
            }
            if (itemDataList.size() > 0) {
                dataListOther = electricityStatementMapper.queryDataOther(equipmentList, dateTime);
            }
            //所有的数据
            dataList.addAll(dataListOther);

            Map<String, Map<String, Object>> allMeter = new HashMap<>();

            //先组装所有的电表数据, 数据直接在这里面取
            for (Map<String, Object> data : dataList) {
                String meterName = data.get("F_DBSYS_NAME").toString();
                String electricCode = data.get("F_DNBH").toString();
                String electricData = data.get("F_DATA").toString();
                Map<String, Object> meterInfo;
                if (allMeter.containsKey(meterName)) {
                    meterInfo = allMeter.get(meterName);

                } else {
                    meterInfo = new HashMap<>();
                }
                meterInfo.put(electricCode, electricData);

                allMeter.put(meterName, meterInfo);
            }

            //循环支路,根据支路id查出所有的
            for (Long branchId : branchIds) {
                //获取支路名称
                AthenaBranchConfig athenaBranchConfig = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, branchId);
                String branchName = athenaBranchConfig.getBranchName();

                //查询支路下的电表
                Map<String, List<String>> meterInfo = branchMeterLinkCache.getMeterByBranchId(branchId);
                List<String> meterSysNameList = meterInfo.get("sysNameList");
                List<String> equipmentListTwo = meterInfo.get("equipmentList");
                if (meterSysNameList.size() > 0) {
                    Map<String, Object> meterRow;
                    for (String meterSysName : meterSysNameList) {
                        meterRow = new HashMap<>();
                        meterRow.put("branchName", branchName);
                        meterRow.put("meterName", meterSysName);

                        if (allMeter.containsKey(meterSysName)) {
                            Map<String, Object> meter = allMeter.get(meterSysName);
                            for (Map<String, Object> map : column) {
                                String code = map.get("code").toString();
                                if (meter.containsKey(code)) {
                                    meterRow.put(code, meter.get(code));
                                } else {
                                    meterRow.put(code, "--");
                                }
                            }
                        } else {
                            for (Map<String, Object> map : column) {
                                String code = map.get("code").toString();
                                meterRow.put(code, "--");
                            }
                        }

                        resultData.add(meterRow);
                    }
                } else {
                    Map<String, Object> meterRow = new HashMap<>();
                    meterRow.put("branchName", branchName);
                    meterRow.put("meterName", "--");
                    for (Map<String, Object> map : column) {
                        String code = map.get("code").toString();
                        meterRow.put(code, "--");
                    }
                    resultData.add(meterRow);
                }

                if (equipmentListTwo.size() > 0) {
                    Map<String, Object> meterRow;
                    for (String meterSysName : equipmentListTwo) {
                        meterRow = new HashMap<>();
                        meterRow.put("branchName", branchName);
                        meterRow.put("meterName", meterSysName);

                        if (allMeter.containsKey(meterSysName)) {
                            Map<String, Object> meter = allMeter.get(meterSysName);
                            for (Map<String, Object> map : column) {
                                String code = map.get("code").toString();
                                if (meter.containsKey(code)) {
                                    meterRow.put(code, meter.get(code));
                                } else {
                                    meterRow.put(code, "--");
                                }
                            }
                        } else {
                            for (Map<String, Object> map : column) {
                                String code = map.get("code").toString();
                                meterRow.put(code, "--");
                            }
                        }

                        resultData.add(meterRow);
                    }
                }

            }
        } else {
            for (Long branchId : branchIds) {
                //获取支路名称
                AthenaBranchConfig athenaBranchConfig = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, branchId);
                String branchName = athenaBranchConfig.getBranchName();

                Map<String, Object> meterRow = new HashMap<>();
                meterRow.put("branchName", branchName);
                meterRow.put("meterName", "--");
                for (Map<String, Object> map : column) {
                    String code = map.get("code").toString();
                    meterRow.put(code, "--");
                }
                resultData.add(meterRow);

            }
        }


        result.put("column", column);
        result.put("resultData", resultData);


        return result;

    }


}