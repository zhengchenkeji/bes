package com.zc.efounder.JEnterprise.commhandler;

import com.zc.ApplicationContextProvider;
import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UUIDUtil;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.commhandler.BesBranchData;
import com.zc.efounder.JEnterprise.domain.commhandler.BesEnergyData;
import com.zc.efounder.JEnterprise.domain.commhandler.MonitoringErrorLog;
import com.zc.efounder.JEnterprise.domain.commhandler.SyncTime;
import com.zc.efounder.JEnterprise.mapper.commhandler.EnergyDataMapper;
import com.zc.efounder.JEnterprise.mapper.commhandler.JobManagerMapper;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import com.zc.efounder.JEnterprise.domain.energyInfo.ManualEntryEnergy;
import com.zc.efounder.JEnterprise.domain.energyInfo.ManualentryenergyCollection;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.connect.nettyServer.enums.StatisticalTypeEnum;
import com.zc.connect.util.DataUtil;
import com.zc.efounder.JEnterprise.Cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zc.efounder.JEnterprise.commhandler.EnergyCollectHandler.calculationElectricityPrice;

/**
 * description:手动录入能耗操作类
 * author: sunshangeng
 * date:2022/12/6 15:18
 */
public class EntryEnergyHandler {

    private static final EnergyDataMapper energyDataMapper = ApplicationContextProvider.getBean(EnergyDataMapper.class);

    private static final BranchMeterLinkCache branchAmmeteRrlglCache = ApplicationContextProvider.getBean(BranchMeterLinkCache.class);

    private static final HouseholdBranchLinkCache householdBranchRlglCache = ApplicationContextProvider.getBean(HouseholdBranchLinkCache.class);

    private static final SubitemBranchLinkCache subitemBranchRlglCache = ApplicationContextProvider.getBean(SubitemBranchLinkCache.class);

    private static final RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);

    private static MeterCache meterCache = ApplicationContextProvider.getBean(MeterCache.class);

    private static ElectricCollRlglCache electricCollRlglCache = ApplicationContextProvider.getBean(ElectricCollRlglCache.class);

    private static ElectricParamsCache electricParamsCache = ApplicationContextProvider.getBean(ElectricParamsCache.class);

    private static JobManagerMapper besJobManagerMapper = ApplicationContextProvider.getBean(JobManagerMapper.class);


    public static Map<String, Map<String, Map<String, Object>>> beforeControllerData = new HashMap<>();


    private static final Logger log = LoggerFactory.getLogger(EntryEnergyHandler.class);


    /**
     * @description:保存支路园区能耗
     * @author: sunshangeng
     * @return: void
     **/
    public static void branchCalculateHandle(Map<String, Object> dataMap) {
        if (null == dataMap || dataMap.isEmpty()) {
            return;
        }

        Double value = (Double) dataMap.get("data");
        String f_dnbh = (String) dataMap.get("fNybh");
        String parkId = (String) dataMap.get("parkid");
        //gaojikun 修改电表ID为系统名称
        String meterId = dataMap.get("meterId").toString();
        String electricId = (String) dataMap.get("electricId");
        BigDecimal electricPriceSum = (BigDecimal)dataMap.get("electricPriceSum");

        /**组装数据*/
        String timeHour = DataUtil.formatDate((String) dataMap.get("l_time"), StatisticalTypeEnum.HOUR);
        String timeDay = DataUtil.formatDate((String) dataMap.get("l_time"), StatisticalTypeEnum.DAY);
        String timeMonth = DataUtil.formatDate((String) dataMap.get("l_time"), StatisticalTypeEnum.MONTH);
        String timeYear = DataUtil.formatDate((String) dataMap.get("l_time"), StatisticalTypeEnum.YEAR);
        String cjsj = dataMap.get("l_time") + "";
        List<AthenaBranchMeterLink> besBranchAmmeterRlgls = branchAmmeteRrlglCache.getCacheByMeterId(meterId, electricId);

        if (besBranchAmmeterRlgls == null || besBranchAmmeterRlgls.isEmpty()) {
            return;
        }

        try {

            for (AthenaBranchMeterLink besBranchAmmeterRlgl : besBranchAmmeterRlgls) {

                String f_zlbh = besBranchAmmeterRlgl.getBranchId().toString();
                String f_operator = besBranchAmmeterRlgl.getOperator();

                if ("0".equals(f_operator)) {
                    value = -value;
                }

                AthenaBranchConfig besBranchConf = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, f_zlbh);

                if (besBranchConf != null && StringUtils.hasText(besBranchConf.getParentId().toString()) && besBranchConf.getParentId().toString().equals("0")) {

                    BesEnergyData besEnergyData = new BesEnergyData();
                    besEnergyData.setfNybh(f_dnbh);
                    besEnergyData.setfCjsj(timeHour);
                    besEnergyData.setfType(StatisticalTypeEnum.HOUR.toString());
                    besEnergyData.setfData(value);
                    besEnergyData.setfYqbh(parkId);


                    /**计算是否有大于当前时间的数据来判断是否存入数据库*/
                    Integer gtrows = energyDataMapper.queryEnergyGtCjsj(besEnergyData);

                    /**判断是否有大于当前时间的如果没有直接插入*/
                    if (gtrows == 0) {
                        EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeHour, StatisticalTypeEnum.HOUR, cjsj);
                        EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeDay, StatisticalTypeEnum.DAY, cjsj);
                        EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeMonth, StatisticalTypeEnum.MONTH, cjsj);
                        EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeYear, StatisticalTypeEnum.YEAR, cjsj);
                    }
//                    else{
//                        /**查询相等的数据*/
//                        Integer eqrows = energyDataMapper.queryEnergyGtCjsj(besEnergyData);
//                        /**根据数据获取最新插入的能耗数据*/
//                        int originalrows=energyDataMapper.queryoriginalGtCjsj(f_dnbh,dataMap.get("l_time")+"");
//                        /**判断是否有等于当前采集小时数的  且能耗采集时间小于当前时间的如果有更新插入,没有不做操作*/
//                        if(eqrows>0&&originalrows==0){
//                            EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeHour, StatisticalTypeEnum.HOUR,cjsj);
//                            EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeDay, StatisticalTypeEnum.DAY,cjsj);
//                            EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeMonth, StatisticalTypeEnum.MONTH,cjsj);
//                            EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeYear, StatisticalTypeEnum.YEAR,cjsj);
//                        }
//                    }
                }
                BesBranchData besBranchData = new BesBranchData();
                besBranchData.setfCjsj(timeHour);
                besBranchData.setfType(StatisticalTypeEnum.HOUR.toString());
                besBranchData.setfDnbh(f_dnbh);
                besBranchData.setfZlbh(f_zlbh);
                /**计算是否有大于当前时间的数据来判断是否存入数据库*/
                Integer gtrows = energyDataMapper.queryBrancGtCjsj(besBranchData);
                if (gtrows == 0) {
                    saveCalculateData(f_zlbh, f_dnbh, timeHour, StatisticalTypeEnum.HOUR, value, parkId, cjsj, electricPriceSum); // 按小时计算
                    saveCalculateData(f_zlbh, f_dnbh, timeDay, StatisticalTypeEnum.DAY, value, parkId, cjsj, electricPriceSum); // 按天计算
                    saveCalculateData(f_zlbh, f_dnbh, timeMonth, StatisticalTypeEnum.MONTH, value, parkId, cjsj, electricPriceSum); // 按月计算
                    saveCalculateData(f_zlbh, f_dnbh, timeYear, StatisticalTypeEnum.YEAR, value, parkId, cjsj, electricPriceSum); // 按年计算
                }
//                else{
//                    /**查询相等的数据*/
//                    Integer eqrows = energyDataMapper.queryBrancEqCjsj(besBranchData);
//                    /**根据数据获取最新插入的能耗数据*/
//                    int originalrows=energyDataMapper.queryoriginalGtCjsj(f_dnbh,dataMap.get("l_time")+"");
//                    /**判断是否有等于当前采集小时数的 且能耗采集时间小于当前时间的如果有更新插入,没有不做操作*/
//                    if(eqrows>0&&originalrows==0){
//                        saveCalculateData(f_zlbh, f_dnbh, timeHour, StatisticalTypeEnum.HOUR, value, parkId,cjsj); // 按小时计算
//                        saveCalculateData(f_zlbh, f_dnbh, timeDay, StatisticalTypeEnum.DAY, value, parkId,cjsj); // 按天计算
//                        saveCalculateData(f_zlbh, f_dnbh, timeMonth, StatisticalTypeEnum.MONTH, value, parkId,cjsj); // 按月计算
//                        saveCalculateData(f_zlbh, f_dnbh, timeYear, StatisticalTypeEnum.YEAR, value, parkId,cjsj); // 按年计算
//                    }
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * @description:保存支路数据
     * @author: sunshangeng
     *
     *
     */
    private static void saveCalculateData(
            String f_zlbh,
            String f_dnbh,
            String time,
            StatisticalTypeEnum typeEnum,
            Double value,
            String f_yqbh, String cjsj,
            BigDecimal electricPriceSum) {

        if (null == f_zlbh
                || null == f_dnbh
                || null == time
                || null == typeEnum
                || null == f_yqbh
                || null == value) {
            return;
        }

        BesBranchData besBranchData = new BesBranchData();
        besBranchData.setfZlbh(f_zlbh);
        besBranchData.setfCjsj(time);
        besBranchData.setfType(typeEnum.getCode().toString());
        besBranchData.setfData(value);
        besBranchData.setfAccurateCjsj(cjsj);
        besBranchData.setElectricPriceSum(electricPriceSum);
        /**
         * 能耗存库
         * 方案一：首先查询该记录是否存在，不存则新增数据，存在则更新数据
         * 方案二：首先更新数据，更新的数据不存在则新增（√）
         */
        // String count = besBranchDataMapper.queryBranchExists(besBranchData).getRows();

        try {
            if (!energyDataMapper.updateBranchData(besBranchData)) {
                besBranchData.setfId(UUIDUtil.getRandom32BeginTimePK());
                besBranchData.setfDnbh(f_dnbh);

                energyDataMapper.saveBranchData(besBranchData);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // 获取所有的分项编号
        List<SubitemBranchLink> subentryNumbers = subitemBranchRlglCache.getCacheByBranchId(f_zlbh);

        // 保存分项能耗数据
        SubentryCalculateHandler.saveCalculateData(subentryNumbers, time, value, typeEnum, f_yqbh);

        // 获取所有的分户编号
        List<AthenaBesHouseholdBranchLink> householdNumbers = householdBranchRlglCache.getCacheByBranchId(f_zlbh);

        // 保存分户能耗数据
        HouseholdCalculateHandler.saveCalculateData(householdNumbers, time, value, typeEnum, f_yqbh, electricPriceSum);

        // 保存分户分项数据
        HouseholdSubentryCalculateHandler.saveCalculateData(householdNumbers, subentryNumbers, time, value, typeEnum, f_yqbh, electricPriceSum);
    }


    /***
     * @description:电表数据
     * @author: sunshangeng
     *
     */
    public static Boolean saveMeterEnergy(ManualEntryEnergy manualentryenergy) {
        if (manualentryenergy.getElectricparamsNameList().size() == 0) {
            log.warn("下位机数据异常（电表数据不存在）");
            return false;
        }
        //  能耗采集器参数
        Controller collector = meterCache.getControllerByMeterTreeid(manualentryenergy.getPointTreeid() + "");


        // 判断能耗采集器是否存在
        if (null == collector) {
            log.warn("当前 能耗节点没有配置能耗采集器");
            return false;
        }

        String ip = collector.getIp();

        if (!StringUtils.hasText(ip)) {
            log.warn("下位机数据异常（ip 地址不存在）");
            return false;
        }

        // 园区编号（从设备树缓存中获取园区编号）
        DeviceTree deviceTree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, Long.valueOf(collector.getDeviceTreeId()));
        String parkId = deviceTree.getPark();
        // 解析后的能耗数据（只包括纳入能耗统计的数据）
        List<Map<String, Object>> dataList = new ArrayList<>();

        // 解析后的能耗数据（全部数据）
        List<Map<String, Object>> allDataList = new ArrayList<>();


        // 遍历下位机上传的电表数据
//            for (AmmeterData ammeterDatum : ammeterData) {

        // 下位机电能数据
//                String electricData = ammeterDatum.getElectricData();

        // 实际采集的电能参数个数
//                Integer collectCount = ammeterDatum.getCollectCount();

//                if (null == electricData
//                        || electricData.isEmpty()
//                        || null == collectCount
//                        || collectCount <= 0) {
//                    log.warn("下位机数据异常（电能参数不存在）");
//                    continue;
//                }

        // 分解下位机电能数据，保存到数组
//                String[] electricDataArray = electricData.split(",");

//                // 判断下位机的电能数据个数与实际电能数据个数是否一致（下位机参数 collectCount 定义个数与电能数据个数不一致）
//                if (!collectCount.equals(electricDataArray.length)) {
//                    log.warn("下位机数据异常（下位机参数 collectCount 定义个数与电能数据个数不一致）");
//                    continue;
//                }
//
//                // 下位机 meterID （对应上位机 sbId）
//                Integer meterID = ammeterDatum.getMeterID();
//
//                // 判断下位机 meterID 是否存在
//                if (null == meterID) {
//                    log.warn("下位机数据异常（电表参数 meterID 不存在）");
//                    continue;
//                }

        AthenaElectricMeter ammeterParam = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, manualentryenergy.getPointTreeid());
//                        meterCache.getMeterByMeterId(meterID);
        if (ammeterParam == null) {
            log.warn("上位机与下位机电表一致（上位机没有对应的电能参数）");
            return false;
        }

//
//                if (null == besElectricCollRlgls || besElectricCollRlgls.isEmpty()) {
//                    log.warn("上位机电能参数不存在（上位机没有对应的电能参数）");
//                    continue;
//                }

        // 变比（从下位机电表参数中获取参数变比）
        Integer rate = ammeterParam.getRate().intValue();

//                // 格式化下位机上传的时间
//                String dateTime = DataUtil.formatDate(ammeterDatum.getDateYear(),
//                        ammeterDatum.getDateMonth(),
//                        ammeterDatum.getDateDay(),
//                        ammeterDatum.getTimeHour(),
//                        ammeterDatum.getTimeMinute(),
//                        ammeterDatum.getTimeSecond()
//                );

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //从缓存取出上次保存时间
//                SyncTime syncTime = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionTime, ammeterParam.getMeterId());
//                //计算上传间隔时间,不能小于设置的保存周期
//                if (syncTime != null) {
//                    Integer diff = 0;
//                    try {
//                        Date startTime = manualentryenergy.getEnergyCjsj();
//                        Date lastTime = dateFormat.parse(syncTime.getTime());
//                        Long a = (startTime.getTime() - lastTime.getTime()) / 1000;
//                        diff = a.intValue();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (diff < saveCycle) {
//                        log.warn("保存周期小于" + saveCycle + "秒");
//                        continue;
//                    }
//                }
        //如果缓存没有或者大于设置的周期 则更新缓存
        SyncTime syncTime1 = new SyncTime();
        syncTime1.setTime(dateFormat.format(manualentryenergy.getEnergyCjsj()));
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionTime, ammeterParam.getMeterId().toString(), syncTime1);


        // 电表系统名称（从下位机电表参数中获取电表系统名称）
        String ammeterName = ammeterParam.getSysName();

        // 推送第三方电表参数
        //String fSysNameOld = ammeterParam.getfSysNameOld();

        // 1、遍历电能数据 2、解析原始能耗数据
//                for (int i = 0; i < electricDataArray.length; i++) {

        for (ManualentryenergyCollection manualentryenergyCollection : manualentryenergy.getElectricparamsNameList()) {
            ElectricCollRlgl besElectricCollRlgl = electricCollRlglCache.getElectricCollRlglByCollIdandParamsId(ammeterParam.getCollectionMethodCode().intValue(), manualentryenergyCollection.getParamCode());


//                    ElectricCollRlgl besElectricCollRlgl = besElectricCollRlgls.get(i);

//                    if (besElectricCollRlgl == null) {
//                        log.warn("上位机电能参数不存在（上位机没有对应的电能参数）");
//                        break;
//                    }

            ElectricParams besElectricParams = electricParamsCache.getCacheByEnergyCode(manualentryenergyCollection.getParamCode());
//                            redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams,manualentryenergyCollection.getParamId());
//                            electricParamsCache.getCacheByEnergyCode(manualentryenergyCollection.getParamId());

            if (besElectricParams == null) {
                log.warn("上位机电能参数不存在（上位机没有对应的电能参数）");
                break;
            }

            // 小数位数（从下位机电能参数中获取小数位置参数）
            Integer decimals = besElectricParams.getPointLocation().intValue();
            // 编码规则（从下位机电能参数中获取编码规则参数）
            Integer enctype = besElectricParams.getDataEncodeType().intValue();
            // 是否纳入能耗统计的标志
            Integer statistical = Integer.parseInt(besElectricCollRlgl.getStatisticalParam());

            // 电能编号（电能参数）
            String electricId = besElectricParams.getCode();
            // 能源编号（能源类型）
            String fNybh = besElectricParams.getEnergyCode();

            // 是否变比
            String isRate = besElectricCollRlgl.getIsRate();

            /**处理因数据类型导致无法处理能耗数值的问题*/
            String rawData = manualentryenergyCollection.getEnergyValue().intValue() + "";

            // 解析原始数据
            Double data = com.zc.connect.client.util.DataUtil.parseRawData(rawData, rate, decimals, enctype, isRate);

            Map<String, Object> dataMap = new HashMap<>();
            String id = UUIDUtil.getRandom32BeginTimePK();

            // id
            dataMap.put("id", id);
            // 电表系统名称
            dataMap.put("meteruuid", ammeterName);
            //电表id
            dataMap.put("meterId", ammeterParam.getMeterId());

            // 推送第三方电表参数
//                dataMap.put("fSysNameOld", fSysNameOld);
            // 电能数据
            dataMap.put("data", data);
            // 采集时间
            dataMap.put("l_time", dateFormat.format(manualentryenergy.getEnergyCjsj()));
            // 电能编号（电能参数）
            dataMap.put("electricId", electricId);
            // 园区编号
            dataMap.put("parkid", parkId);
            // 能源编号（能源类型）
            dataMap.put("fNybh", fNybh);
            //电价单价
            String dataTime = dateFormat.format(manualentryenergy.getEnergyCjsj());
            BigDecimal electricPrice = calculationElectricityPrice(dataTime);
            dataMap.put("electricPrice", electricPrice);

//            dataMap.put("type",manualentryenergy.getEnergyType());
            if (!Objects.equals(data, 0.0)) {
                dataList.add(dataMap);
            }

            allDataList.add(dataMap);

            // 加入电表原始数据缓存缓存
            Map<String, Map<String, Object>> originalCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData, ammeterParam.getMeterId().toString());

            if (originalCache == null) {
                originalCache = new HashMap<>();
            }

            originalCache.put(electricId, dataMap);
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData, ammeterParam.getMeterId().toString(), originalCache);
        }
//            }
        if (!dataList.isEmpty()) {
            // 插入电表数据表
            besJobManagerMapper.insertCalculateData(dataList);
            // 保存电表差值数据到能耗监控差值数据表数据表
            saveMonitoringData(dataList, ip);
            // 推送第三方电表数据
            // pushAmmeterData(dataList);
        }
        if (!allDataList.isEmpty()) {
            // 插入电表原始数据表 TODO 电能参数类型没有存储（电能参数表没有字段区分是什么类型）
            besJobManagerMapper.insertEnectricData(allDataList);
        }

        return true;
    }

    /***
     * @author: sunshangeng
     *处理非电表其他能耗节点*/
    public static Boolean savePointEnery(ManualEntryEnergy entryEnergy) {

        if (null == entryEnergy) {
            log.warn("手动能耗接收能耗数据，没有能耗数据");
            return false;
        }

        List<Map<String, Object>> data = new ArrayList<>();

        //  获取控制器
        Controller collector = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, entryEnergy.getControllerTreeid());

        /**获取当前点位详情*/
        DeviceTree point = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, entryEnergy.getPointTreeid());
        /**获取当前点位电表详情*/
        AthenaElectricMeter meter = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, entryEnergy.getPointTreeid());


        for (ManualentryenergyCollection collection : entryEnergy.getElectricparamsNameList()) {
            String f_sys_name = point.getSysName();
            //推送第三方电表参数
//            String f_sys_name_old = (String) dataMap.get("sysNameOld");
            String f_energy_type = entryEnergy.getEnergyType();
            Double f_date = collection.getEnergyValue();
            String f_yqbh = point.getPark();
            Long meterId = meter.getMeterId();
            if (null == f_sys_name || null == f_energy_type || null == f_date) {
                log.warn("接收Ai点能耗数据，参数错误，系统名称或者能源编号或者能耗数据不存在");
                return false;
            }
            // 园区编号
//            String f_yqbh = ammeterInfo.getfYqbh();
            Map<String, Object> datum = new HashMap<>();

            datum.put("id", UUIDUtil.getRandom32BeginTimePK());
            datum.put("meteruuid", f_sys_name);
            // 推送第三方电表参数
//            datum.put("fSysNameOld", f_sys_name_old);
            datum.put("data", f_date);
            datum.put("l_time", DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", entryEnergy.getEnergyCjsj()));
            datum.put("parkid", f_yqbh);
            datum.put("fNybh", f_energy_type);
            /** 因未绑定支路这里传入点位树id*/
            datum.put("meterId", meterId);
            // 电能编号（电能参数）
            datum.put("electricId", PointPowerParam.Point_Meter_Code);
            //电价单价
            String dataTime = DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", entryEnergy.getEnergyCjsj());
            BigDecimal electricPrice = calculationElectricityPrice(dataTime);
            datum.put("electricPrice", electricPrice);

//            datum.put("type",entryEnergy.getEnergyType());
            data.add(datum);
        }

        try {
            // 插入电表数据表
            besJobManagerMapper.insertCalculateData(data);
            // 插入电表原始数据表 TODO 电能参数类型没有存储（电能参数表没有字段区分是什么类型）
            besJobManagerMapper.insertEnectricData(data);
        } catch (Exception e) {
            log.warn("接收AI点能耗数据，保存电表数据表的数据发生异常");
            e.printStackTrace();
            return false;
        }
        // 保存电表差值数据到能耗监控差值数据表数据表
        saveMonitoringData(data, collector.getIp());

        return true;
        // 推送第三方电表数据
        // pushAmmeterData(data);
    }


    /**
     * @description:保存电表差值
     * @author: sunshangeng
     **/

    public static void saveMonitoringData(List<Map<String, Object>> newData, String ip) {
        if (null == newData || newData.isEmpty() || !StringUtils.hasText(ip)) {
            return;
        }
        // 获取上一次缓存的采集器能耗数据
        //beforeControllerData缓存格式: 采集器ip -> 电表名称 -> 电能参数id -> 数据值
        // '127.0.0.1':{
        //      'meter1-1':{
        //          '100001':{...},
        //          '100002':{...}
        //      },
        //      'meter1-2':{
        //          '100001':{...},
        //          '100002':{...}
        //      },
        // }
        beforeControllerData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData, ip);

//        List<Map<String, Object>> removelist =new ArrayList<>();
        // 差值数据
        List<Map<String, Object>> data = new ArrayList<>();

        // 1.遍历上来的数据
        // 2.如果缓存中有对应的采集器、电表、采集参数,则与缓存中的数据计算差值
        // 3.将此次数据输入组装后放入缓存,用于下次计算差值
        newData.forEach(item -> {
            String sysName = (String) item.get("meteruuid");
            String electricId = (String) item.get("electricId");
            //缓存中的电表数据
            Map<String, Map<String, Object>> beforeMeterData = new HashMap<>();

            //采集器数据缓存不为空
            if (beforeControllerData != null) {

                //采集器中有这块电表的数据
                if (beforeControllerData.containsKey(sysName)) {
                    //取出缓存中的电表数据
                    beforeMeterData = beforeControllerData.get(sysName);

                    //电表中有这个采集参数的数据时则进行差值计算
                    if (beforeMeterData.containsKey(electricId)) {
                        Map<String, Object> beforeElectricData = beforeMeterData.get(electricId);
                        Double dataNew = (Double) item.get("data");
                        Double dataBefore = (Double) beforeElectricData.get("data");

                        BigDecimal dataNew1 = new BigDecimal(String.valueOf(dataNew));
                        BigDecimal dataBefore2 = new BigDecimal(String.valueOf(dataBefore));

                        Double betweenValue = dataNew1.subtract(dataBefore2).doubleValue();

                        //差值负数时不存差值数据,不更新缓存, 但是插入错误日志表athena_bes_monitoring_error_log
                        if (betweenValue < 0) {
                            MonitoringErrorLog monitoringErrorLog = new MonitoringErrorLog();
                            monitoringErrorLog.setSysName(sysName);
                            monitoringErrorLog.setElectricId(electricId);
                            monitoringErrorLog.setMeterType("0");
                            monitoringErrorLog.setBeforeData(dataBefore);
                            monitoringErrorLog.setNewData(dataNew);
                            monitoringErrorLog.setDiffData(betweenValue);
                            besJobManagerMapper.insertMonitoringErrorLog(monitoringErrorLog);
                            log.warn("差值运算，数据错误，能耗值不能是负数");
                            /***/
//                            newData.remove(item);
//                            removelist.add(item);
                            return;
                        } else {
                            Map<String, Object> dataMap = new HashMap<>();
                            dataMap.put("id", UUIDUtil.getRandom32BeginTimePK());
                            dataMap.put("meteruuid", sysName);
                            dataMap.put("data", betweenValue);
                            dataMap.put("l_time", item.get("l_time"));
                            dataMap.put("parkid", item.get("parkid"));
                            dataMap.put("fNybh", item.get("fNybh"));
                            dataMap.put("meterId", item.get("meterId"));
                            dataMap.put("electricId", item.get("electricId"));
                            BigDecimal electricPriceSum = new BigDecimal(item.get("electricPrice").toString()).multiply(new BigDecimal(betweenValue));
                            electricPriceSum = electricPriceSum.setScale(10,BigDecimal.ROUND_UP);
                            dataMap.put("electricPriceSum", electricPriceSum);
                            // 支路计算统计
                            data.add(dataMap);
                            EntryEnergyHandler.branchCalculateHandle(dataMap);
                            // 支路计算统计
//                            BranchCalculateHandler.branchCalculateHandle(dataMap);
                        }
                    } else {
                        log.warn("电表:" + sysName + "中的采集参数:" + electricId + "没有能耗数据缓存,本次不进行差值计算");
                    }
                } else {
                    log.warn("采集器:" + ip + "中的电表:" + sysName + "没有能耗数据缓存,本次不进行差值计算");
                }
            } else {
                beforeControllerData = new HashMap<>();
                log.warn("采集器:" + ip + "没有能耗数据缓存,本次不进行差值计算");
            }

            //将采集参数数据放入电表中
            beforeMeterData.put(electricId, item);
            //将电表数据放入采集器数据中
            beforeControllerData.put(sysName, beforeMeterData);
        });
        /**sunshangeng 删除差值计算错误的原始数据*/
//        for (Map<String, Object> map : removelist) {
//
//            besJobManagerMapper.deleteCalculateData(map.get("id")+"");
//        }
        //更新能耗数据缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData, ip, beforeControllerData);
        if (data.size() > 0) {
            // 保存能耗监控差值数据表数据
            besJobManagerMapper.insertMonitoringData(data);
        }
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dbname = "DB2B305";
        String csz = "336.62";
        String cz = "0.04";
        Double fdz = 0.01;
        String time1 = "2022-11-18 23:59:53";
        Date date = formatter.parse(time1);
        double savecsz = Double.parseDouble(csz);
        double savecz = Double.parseDouble(cz);
        for (int i = 0; i < 25; i++) {
            // 获取当前时间
//			Date date = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            // 把日期往后增加一天,整数  往后推,负数往前移动
            calendar.add(Calendar.DATE, 1);
            // 这个时间就是日期往后推一天的结果
            date = calendar.getTime();
            String savetime = org.apache.http.client.utils.DateUtils.formatDate(date, "yyyy-MM-dd HH:mm:ss");
//			savecsz=savecsz+savecz;

            java.math.BigDecimal b1 = new java.math.BigDecimal(Double.toString(savecsz));
            java.math.BigDecimal b2 = new java.math.BigDecimal(Double.toString(savecz));
            savecsz = b1.add(b2).doubleValue();
            if (i % 2 == 0) {
                b1 = new java.math.BigDecimal(Double.toString(savecsz));
                b2 = new java.math.BigDecimal(Double.toString(fdz));
                savecsz = b1.add(b2).doubleValue();
            }
            String sql = "insert into bes_calculate_data (F_id,F_DBSYS_NAME,F_DATA,F_CJSJ,F_YQBH) VALUES( REPLACE(UUID(),'-',''),'" + dbname + "'," + savecsz + ",'" + savetime + "','0000');";

            System.out.println(sql);
        }

        System.out.println(123);
    }
}
