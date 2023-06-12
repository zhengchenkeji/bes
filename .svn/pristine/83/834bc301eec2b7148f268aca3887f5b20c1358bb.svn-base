package com.zc.efounder.JEnterprise.commhandler;


import com.zc.ApplicationContextProvider;
import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.uuid.UUIDUtil;
import com.zc.efounder.JEnterprise.Cache.BranchMeterLinkCache;
import com.zc.efounder.JEnterprise.Cache.HouseholdBranchLinkCache;
import com.zc.efounder.JEnterprise.Cache.SubitemBranchLinkCache;
import com.zc.efounder.JEnterprise.domain.commhandler.BesBranchData;
import com.zc.efounder.JEnterprise.mapper.commhandler.EnergyDataMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.connect.nettyServer.enums.StatisticalTypeEnum;
import com.zc.connect.util.DataUtil;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 支路计量处理
 *
 * @author xiepufeng
 * @date 2020/6/12 7:50
 */
public class BranchCalculateHandler {

    private static final EnergyDataMapper energyDataMapper = ApplicationContextProvider.getBean(EnergyDataMapper.class);

    private static final BranchMeterLinkCache branchAmmeteRrlglCache = ApplicationContextProvider.getBean(BranchMeterLinkCache.class);

    private static final HouseholdBranchLinkCache householdBranchRlglCache = ApplicationContextProvider.getBean(HouseholdBranchLinkCache.class);

    private static final SubitemBranchLinkCache subitemBranchRlglCache = ApplicationContextProvider.getBean(SubitemBranchLinkCache.class);

    private static final RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);


    public static void branchCalculateHandle(Map<String, Object> dataMap) {
        if (null == dataMap || dataMap.isEmpty()) {
            return;
        }


        String sysName = (String) dataMap.get("meteruuid");

        Double value = (Double) dataMap.get("data");
        String f_dnbh = (String) dataMap.get("fNybh");
        String parkId = (String) dataMap.get("parkid");
        //gaojikun 修改电表ID为系统名称
        String meterId = dataMap.get("meterId").toString();
        String electricId = (String) dataMap.get("electricId");
        BigDecimal electricPriceSum = (BigDecimal)dataMap.get("electricPriceSum");

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

                if (besBranchConf != null && !StringUtils.hasText(besBranchConf.getParentId().toString())) {
                    // 计算总能耗
                    EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeHour, StatisticalTypeEnum.HOUR, cjsj);
                    EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeDay, StatisticalTypeEnum.DAY, cjsj);
                    EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeMonth, StatisticalTypeEnum.MONTH, cjsj);
                    EnergyCalculateHandler.saveEnergyCalculate(f_dnbh, value, parkId, timeYear, StatisticalTypeEnum.YEAR, cjsj);
                }

                saveCalculateData(f_zlbh, f_dnbh, timeHour, StatisticalTypeEnum.HOUR, value, parkId, cjsj, electricPriceSum); // 按小时计算
                saveCalculateData(f_zlbh, f_dnbh, timeDay, StatisticalTypeEnum.DAY, value, parkId, cjsj, electricPriceSum); // 按天计算
                saveCalculateData(f_zlbh, f_dnbh, timeMonth, StatisticalTypeEnum.MONTH, value, parkId, cjsj, electricPriceSum); // 按月计算
                saveCalculateData(f_zlbh, f_dnbh, timeYear, StatisticalTypeEnum.YEAR, value, parkId, cjsj, electricPriceSum); // 按年计算

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 保存支路数据
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
                || null == value
                || null == electricPriceSum) {
            return;
        }

        BesBranchData besBranchData = new BesBranchData();
        besBranchData.setfZlbh(f_zlbh);
        besBranchData.setfCjsj(time);
        besBranchData.setfType(typeEnum.getCode().toString());
        besBranchData.setfData(value);
        besBranchData.setfAccurateCjsj(cjsj);
        besBranchData.setElectricPriceSum(electricPriceSum);//电价总金额

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
}
