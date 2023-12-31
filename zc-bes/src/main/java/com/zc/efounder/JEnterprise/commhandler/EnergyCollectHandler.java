package com.zc.efounder.JEnterprise.commhandler;

import com.zc.ApplicationContextProvider;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.uuid.UUIDUtil;
import com.zc.efounder.JEnterprise.Cache.*;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.domain.commhandler.MonitoringErrorLog;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.commhandler.SyncTime;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceLink;
import com.zc.efounder.JEnterprise.mapper.commhandler.JobManagerMapper;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.connect.business.dto.ReceiveMsg;
import com.zc.connect.business.dto.edc.AmmeterData;
import com.zc.connect.client.util.DataUtil;
import com.zc.efounder.JEnterprise.mapper.systemSetting.ElectricityPriceSettingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 能耗采集处理类
 *
 * @author xiepufeng
 * @date 2020/6/12 7:56
 */
public class EnergyCollectHandler {
    /**
     * 能源类型：电
     */
    public static final String ENERGY_TYPE_ELECTRIC = "01000";

    private static final Logger log = LoggerFactory.getLogger(EnergyCollectHandler.class);

//    public static Map<String, Map<String, Map<String, Object>>> monitoringDataTemp = new HashMap<>();


    //redis缓存
    private static RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);

    //电价缓存方法
    private static ElectricityPriceLinkCache electricityPriceLinkCache = ApplicationContextProvider.getBean(ElectricityPriceLinkCache.class);

    // 采集器缓存方法
    private static ControllerCache collectorCache = ApplicationContextProvider.getBean(ControllerCache.class);

    // 电表缓存方法
    private static MeterCache ammeterCache = ApplicationContextProvider.getBean(MeterCache.class);

    // 采集方案采集参数关联定义缓存
    private static ElectricCollRlglCache electricCollRlglCache = ApplicationContextProvider.getBean(ElectricCollRlglCache.class);

    // 采集参数定义缓存
    private static ElectricParamsCache electricParamsCache = ApplicationContextProvider.getBean(ElectricParamsCache.class);

    //电表实时数据表
    private static EnergyRealTimeDataCache energyRealTimeDataCache = ApplicationContextProvider.getBean(EnergyRealTimeDataCache.class);

    private static JobManagerMapper besJobManagerMapper = ApplicationContextProvider.getBean(JobManagerMapper.class);

    private static ElectricityPriceSettingMapper electricityPriceSettingMapper = ApplicationContextProvider.getBean(ElectricityPriceSettingMapper.class);

    //保存间隔时长
    private static Integer saveCycle = 60;

    public static Map<String, Map<String, Map<String, Object>>> beforeControllerData = new HashMap<>();

    public static Map<String, Map<String, Object>> beforeControllerPointData = new HashMap<>();


    // todo 明天继续
    public static void ammeterDataHandle(ReceiveMsg<List<AmmeterData>> msg) {

        List<AmmeterData> ammeterData = msg.getData();

        if (null == ammeterData || ammeterData.isEmpty()) {
            log.warn("下位机数据异常（电表数据不存在）");
            return;
        }

        String ip = msg.getIp();

        if (!StringUtils.hasText(ip)) {
            log.warn("下位机数据异常（ip 地址不存在）");
            return;
        }

        //  能耗采集器参数
        Controller collector;

        try {
            // 根据channelId地址查询表 bes_collector 能耗采集器是否存在
            collector = collectorCache.getDdcByChannelId(ip);
        } catch (Exception e) {
            log.warn("根据  " + ip + "查询能耗采集器参数，数据库发生异常");
            e.printStackTrace();
            return;
        }

        // 判断能耗采集器是否存在
        if (null == collector) {
            log.warn("当前 " + ip + "没有配置能耗采集器");
            return;
        }
        // 园区编号（从设备树缓存中获取园区编号）
        DeviceTree deviceTree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, Long.valueOf(collector.getDeviceTreeId()));
        String parkId = deviceTree.getPark();
        // 解析后的能耗数据（只包括纳入能耗统计的数据）
        List<Map<String, Object>> dataList = new ArrayList<>();

        // 解析后的能耗数据（全部数据）
        List<Map<String, Object>> allDataList = new ArrayList<>();

        // 遍历下位机上传的电表数据
        for (AmmeterData ammeterDatum : ammeterData) {

            // 下位机电能数据
            String electricData = ammeterDatum.getElectricData();

            // 实际采集的电能参数个数
            Integer collectCount = ammeterDatum.getCollectCount();

            if (null == electricData
                    || electricData.isEmpty()
                    || null == collectCount
                    || collectCount <= 0) {
                log.warn("下位机数据异常（电能参数不存在）");
                continue;
            }

            // 分解下位机电能数据，保存到数组
            String[] electricDataArray = electricData.split(",");

            // 判断下位机的电能数据个数与实际电能数据个数是否一致（下位机参数 collectCount 定义个数与电能数据个数不一致）
            if (!collectCount.equals(electricDataArray.length)) {
                log.warn("下位机数据异常（下位机参数 collectCount 定义个数与电能数据个数不一致）");
                continue;
            }

            // 下位机 meterID （对应上位机 sbId）
            Integer meterID = ammeterDatum.getMeterID();

            // 判断下位机 meterID 是否存在
            if (null == meterID) {
                log.warn("下位机数据异常（电表参数 meterID 不存在）");
                continue;
            }

            AthenaElectricMeter ammeterParam = ammeterCache.getMeterByMeterId(meterID);

            if (ammeterParam == null) {
                log.warn("上位机与下位机电表一致（上位机没有对应的电能参数）");
                continue;
            }

            List<ElectricCollRlgl> besElectricCollRlgls = electricCollRlglCache.getElectricCollRlglByCollId(ammeterParam.getCollectionMethodCode().intValue());

            if (null == besElectricCollRlgls || besElectricCollRlgls.isEmpty()) {
                log.warn("上位机电能参数不存在（上位机没有对应的电能参数）");
                continue;
            }

            // 变比（从下位机电表参数中获取参数变比）
            Integer rate = ammeterParam.getRate().intValue();

            // 格式化下位机上传的时间
            String dateTime = DataUtil.formatDate(ammeterDatum.getDateYear(),
                    ammeterDatum.getDateMonth(),
                    ammeterDatum.getDateDay(),
                    ammeterDatum.getTimeHour(),
                    ammeterDatum.getTimeMinute(),
                    ammeterDatum.getTimeSecond()
            );

            // 电价
            BigDecimal electricPrice = calculationElectricityPrice(dateTime);

            //从缓存取出上次保存时间
            SyncTime syncTime = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionTime, meterID.toString());
            //计算上传间隔时间,不能小于设置的保存周期
            if (syncTime != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Integer diff = 0;
                try {
                    Date startTime = dateFormat.parse(dateTime);
                    Date lastTime = dateFormat.parse(syncTime.getTime());
                    Long a = (startTime.getTime() - lastTime.getTime()) / 1000;
                    diff = a.intValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (diff < saveCycle) {
                    log.warn("保存周期小于" + saveCycle + "秒");
                    continue;
                }
            }
            //如果缓存没有或者大于设置的周期 则更新缓存
            SyncTime syncTime1 = new SyncTime();
            syncTime1.setTime(dateTime);
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionTime, meterID.toString(), syncTime1);


            // 电表系统名称（从下位机电表参数中获取电表系统名称）
            String ammeterName = ammeterParam.getSysName();

            // 推送第三方电表参数
            //String fSysNameOld = ammeterParam.getfSysNameOld();

            // 1、遍历电能数据 2、解析原始能耗数据
            for (int i = 0; i < electricDataArray.length; i++) {

                ElectricCollRlgl besElectricCollRlgl = besElectricCollRlgls.get(i);

                if (besElectricCollRlgl == null) {
                    log.warn("上位机电能参数不存在（上位机没有对应的电能参数）");
                    break;
                }

                ElectricParams besElectricParams = electricParamsCache.getCacheByEnergyCode(besElectricCollRlgl.getElectricCode());

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

                // 解析原始数据
                Double data = DataUtil.parseRawData(electricDataArray[i], rate, decimals, enctype, isRate);

                Map<String, Object> dataMap = new HashMap<>();
                String id = UUIDUtil.getRandom32BeginTimePK();

                // id
                dataMap.put("id", id);
                // 电表系统名称
                dataMap.put("meteruuid", ammeterName);
                //电表id
                dataMap.put("meterId", meterID);

                // 推送第三方电表参数
//                dataMap.put("fSysNameOld", fSysNameOld);
                // 电能数据
                dataMap.put("data", data);
                // 采集时间
                dataMap.put("l_time", dateTime);
                // 电能编号（电能参数）
                dataMap.put("electricId", electricId);
                // 园区编号
                dataMap.put("parkid", parkId);
                // 能源编号（能源类型）
                dataMap.put("fNybh", fNybh);
                // 电价单价
                dataMap.put("electricPrice", electricPrice);

                if (statistical.equals(1) && !Objects.equals(data, 0.0)) {
                    dataList.add(dataMap);
                }

                allDataList.add(dataMap);

                // 加入电表原始数据缓存缓存
                Map<String, Map<String, Object>> originalCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData, meterID.toString());

                if (originalCache == null) {
                    originalCache = new HashMap<>();
                }

                originalCache.put(electricId, dataMap);
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData, meterID.toString(), originalCache);


            }

        }

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

    }

    /**
     * 保存电表差值数据到能耗监控差值数据表数据表
     *
     * @param newData
     * @param ip
     */
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
                            electricPriceSum = electricPriceSum.setScale(10, BigDecimal.ROUND_UP);
                            dataMap.put("electricPriceSum", electricPriceSum);

                            data.add(dataMap);

                            // 支路计算统计
                            BranchCalculateHandler.branchCalculateHandle(dataMap);
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

        //更新能耗数据缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData, ip, beforeControllerData);

        if (data.size() > 0) {
            // 保存能耗监控差值数据表数据
            besJobManagerMapper.insertMonitoringData(data);
        }

    }


    public static void ammeterDataHandle(List<Map> dataList, String ip) {

        if (null == dataList || dataList.isEmpty() || !StringUtils.hasText(ip)) {
            log.warn("ip:" + ip + "接收ddc点能耗数据，没有能耗数据");
            return;
        }

        List<Map<String, Object>> data = new ArrayList<>();

        // 当前时间
        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 电价
        BigDecimal electricPrice = calculationElectricityPrice(nowTime);

        for (Map dataMap : dataList) {
            String f_sys_name = (String) dataMap.get("sysName");
            //推送第三方电表参数
            String f_sys_name_old = (String) dataMap.get("sysNameOld");
            String f_energy_type = (String) dataMap.get("energyCode");
            Double f_date = Double.parseDouble(dataMap.get("date").toString());
            String f_yqbh = (String) dataMap.get("park");
            Long meterId = (Long) dataMap.get("meterId");

            if (null == f_sys_name || null == f_energy_type || null == f_date) {
                log.warn("接收ddc点能耗数据，参数错误，系统名称或者能源编号或者能耗数据不存在");
                continue;
            }

//            BESAmmeter ammeterInfo = ammeterCache.getCachedElement(f_sys_name);
//
//            if (null == ammeterInfo)
//            {
//                log.warn("接收ddc点能耗数据，根据系统名称没有查询到相应的电表信息，请查看虚点和实点电表配置是否有问题");
//                continue;
//            }

//           // 园区编号
//            String f_yqbh = ammeterInfo.getfYqbh();


            Map<String, Object> datum = new HashMap<>();

            datum.put("id", UUIDUtil.getRandom32BeginTimePK());
            datum.put("meteruuid", f_sys_name);
            // 推送第三方电表参数
            datum.put("fSysNameOld", f_sys_name_old);
            datum.put("data", f_date);
            datum.put("l_time", nowTime);
            datum.put("parkid", f_yqbh);
            datum.put("fNybh", f_energy_type);
            datum.put("meterId", meterId);
            // 电价总价
            datum.put("electricPrice", electricPrice);
            // 电能编号（电能参数）
            if(dataMap.get("electricId") != null && !StringUtils.isEmpty(dataMap.get("electricId").toString())){
                datum.put("electricId", dataMap.get("electricId").toString());
                dataMap.put("electricId", dataMap.get("electricId").toString());
            }else{
                datum.put("electricId", PointPowerParam.Point_Meter_Code);
                dataMap.put("electricId", PointPowerParam.Point_Meter_Code);
            }


            data.add(datum);

        }

        try {
            // 插入电表数据表
            besJobManagerMapper.insertCalculateData(data);
            // 插入电表原始数据表 TODO 电能参数类型没有存储（电能参数表没有字段区分是什么类型）
            besJobManagerMapper.insertEnectricData(data);
        } catch (Exception e) {
            log.warn("接收ddc点能耗数据，保存电表数据表的数据发生异常");
            e.printStackTrace();
            return;
        }
        // 保存电表差值数据到能耗监控差值数据表数据表
        saveMonitoringPointData(data, ip);

        // 推送第三方电表数据
        // pushAmmeterData(data);
    }

    /**
     * 点位电表-保存电表差值数据到能耗监控差值数据表数据表
     *
     * @param newData
     * @param ip
     */
    public static void saveMonitoringPointData(List<Map<String, Object>> newData, String ip) {
        if (null == newData || newData.isEmpty() || !StringUtils.hasText(ip)) {
            return;
        }

        beforeControllerPointData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionPointData, ip);

        // 差值数据
        List<Map<String, Object>> data = new ArrayList<>();

        newData.forEach(item -> {
            String sysName = (String) item.get("meteruuid");
            Long meterId = Long.parseLong(item.get("meterId").toString());

            //缓存中的电表数据
            Map<String, Object> beforeMeterData = new HashMap<>();

            //采集器不为空
            if (beforeControllerPointData != null) {

                //采集器中有这块电表的数据
                if (beforeControllerPointData.containsKey(meterId.toString())) {
                    //取出缓存中的电表数据
                    beforeMeterData = beforeControllerPointData.get(meterId.toString());

                    Double dataNew = (Double) item.get("data");
                    Double dataBefore = (Double) beforeMeterData.get("data");

                    BigDecimal dataNew1 = new BigDecimal(String.valueOf(dataNew));
                    BigDecimal dataBefore2 = new BigDecimal(String.valueOf(dataBefore));

                    Double betweenValue = dataNew1.subtract(dataBefore2).doubleValue();

                    if (betweenValue < 0) {
                        MonitoringErrorLog monitoringErrorLog = new MonitoringErrorLog();
                        monitoringErrorLog.setSysName(sysName);
                        monitoringErrorLog.setMeterType("1");
                        monitoringErrorLog.setBeforeData(dataBefore);
                        monitoringErrorLog.setNewData(dataNew);
                        monitoringErrorLog.setDiffData(betweenValue);
                        besJobManagerMapper.insertMonitoringErrorLog(monitoringErrorLog);
                        log.warn("差值运算，数据错误，能耗值不能是负数");
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
                        electricPriceSum = electricPriceSum.setScale(10, BigDecimal.ROUND_UP);
                        dataMap.put("electricPriceSum", electricPriceSum);
                        data.add(dataMap);

                        // 支路计算统计
                        BranchCalculateHandler.branchCalculateHandle(dataMap);
                    }

                } else {
                    log.warn("采集器:" + ip + "中的电表:" + sysName + "没有能耗数据缓存,本次不进行差值计算");
                }


            } else {
                beforeControllerPointData = new HashMap<>();
                log.warn("采集器:" + ip + "没有能耗数据缓存,本次不进行差值计算");
            }
            //将电表数据放入采集器数据中
            beforeControllerPointData.put(meterId.toString(), item);

        });

        //更新能耗数据缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionPointData, ip, beforeControllerPointData);

        if (data.size() > 0) {
            // 保存能耗监控差值数据表数据
            besJobManagerMapper.insertMonitoringData(data);
        }

    }


    /**
     * @Author:gaojikun
     * @Date:2023-02-20 10:22
     * @Param:
     * @Description:获取电价价格
     * @Return:
     */
    public static BigDecimal calculationElectricityPrice(String dateTime) {
        //先获取时间，季节id
        String seassonId = electricityPriceLinkCache.getSeasonIdCache(dateTime);
        if (seassonId == null || "".equals(seassonId)) {
            System.out.println("数据上传，查询电价，获取季节id出现异常");
            return new BigDecimal("0");
        }
        String timeId = electricityPriceLinkCache.getTimeIdCache(Integer.parseInt(dateTime.substring(11, 13)));
        if (timeId == null || "".equals(timeId)) {
            System.out.println("数据上传，查询电价，获取时间id出现异常");
            return new BigDecimal("0");
        }

        //缓存取字段
        ElectricityPriceLink electricityPriceLinkObject = electricityPriceLinkCache.getCacheBySeasonIdAndTimeId(seassonId, timeId);
        //根据字段，时间从缓存取电价
        String priceType = "";
        if (electricityPriceLinkObject != null) {
            priceType = electricityPriceLinkObject.getPriceType();
            if ("".equals(priceType)) {
                return new BigDecimal("0");
            }
        } else {
            return new BigDecimal("0");
        }
        //获取字段月份电价
        BigDecimal electricityPrice = electricityPriceLinkCache.getElectricityPrice(priceType, dateTime.substring(0, 7));
        return electricityPrice;

//        Map<String, Object> fieMap = electricityPriceSettingMapper.queryFieIdBySeasonTime(dateTime.substring(5, 10), dateTime.substring(11, 16));
//        if(fieMap == null){
//            return new BigDecimal("0");
//        }
        //获取字段月份电价
//        Map<String, Object> priceMap = electricityPriceSettingMapper.queryElectricityPrice(dateTime.substring(0, 7));
//        if (priceMap == null ) {
//            priceMap = electricityPriceSettingMapper.queryElectricityPriceLast();
//            if (priceMap == null) {
//                return new BigDecimal("0");
//            }
//        }

//        //转化为bigdecimal
//        String fieStr = fieMap.get("price_type").toString();
//        String priceStr = priceMap.get(fieStr).toString();
//        BigDecimal priceBigDecimal = new BigDecimal(priceStr);
//        return priceBigDecimal;
    }
}
