package com.zc.efounder.JEnterprise.commhandler;

import com.zc.ApplicationContextProvider;
import com.ruoyi.common.utils.uuid.UUIDUtil;
import com.zc.efounder.JEnterprise.domain.commhandler.HouseholdData;
import com.zc.efounder.JEnterprise.mapper.commhandler.HouseholdDataMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import com.zc.connect.nettyServer.enums.StatisticalTypeEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * 计算分户数据
 *
 * @author xiepufeng
 * @date 2020/6/20 9:10
 */
public class HouseholdCalculateHandler {
    private static HouseholdDataMapper besHouseholdDataMapper = ApplicationContextProvider.getBean(HouseholdDataMapper.class);

  /*  private static HouseholdConfigCache householdConfigCache = ApplicationContextProvider.getBean(HouseholdConfigCache.class);

    private static EnergyTypeCache energyTypeCache = ApplicationContextProvider.getBean(EnergyTypeCache.class);*/


    /**
     * 保存分户数据
     */
    public static void saveCalculateData(
            List<AthenaBesHouseholdBranchLink> fFhbhList,
            String fCjsj,
            Double data,
            StatisticalTypeEnum typeEnum,
            String fYqbh,
            BigDecimal electricPriceSum) {

        if (null == fFhbhList
                || fFhbhList.isEmpty()
                || null == fCjsj
                || null == data
                || null == fYqbh
                || null == typeEnum
                || null == electricPriceSum) {
            return;
        }

        HouseholdData besHouseholdData = new HouseholdData();

        besHouseholdData.setCollectTime(fCjsj); // 采集时间
        besHouseholdData.setDateType(typeEnum.getCode().toString()); // 统计类型
        besHouseholdData.setDataValue(data); // 数据值
        besHouseholdData.setParkId(fYqbh); // 园区编号
        besHouseholdData.setElectricPriceSum(electricPriceSum);//电价总金额

        try {
            for (AthenaBesHouseholdBranchLink besHouseholdBranchRlgl : fFhbhList) {

                String fFhbh = besHouseholdBranchRlgl.getHouseholdId().toString();
                besHouseholdData.setHouseholdId(fFhbh); // 分户编号

                /**
                 * 计算人均能耗等
                 */
                // 注释原因：数据丢失严重
          /*  // 根据分户编号从缓存中获取分户配置信息
            BesHouseholdConf besHouseholdConf = householdConfigCache.getCachedElement(fFhbh);


            BESEnergyType besEnergyType = energyTypeCache.getCachedElement(besHouseholdConf.getfNybh());

            try
            {
                Double price = Double.parseDouble(besEnergyType.getfPrice()); // 单价

                Double allMoney = price * data;

                besHouseholdData.setfAllMoney(Double.valueOf(String.format("%.2f", allMoney))); // 总金额

                Double coal = Double.parseDouble(besEnergyType.getfCoalAmount()); // 单位耗煤量

                Double coalAmount = coal * data;

                besHouseholdData.setfCoalAmount(Double.valueOf(String.format("%.2f", coalAmount))); // 耗煤量

                Double co2 = Double.parseDouble(besEnergyType.getfCo2()); // 单位产生二氧化碳量

                Double co2Amount = co2 * data;

                besHouseholdData.setfCo2(Double.valueOf(String.format("%.2f", co2Amount))); // 二氧化碳量

                Integer personNums = Integer.parseInt(besHouseholdConf.getfPersonNums()); // 人数

                if (personNums > 0)
                {
                    Double personData = data/personNums; // 人均能耗

                    besHouseholdData.setfPermanData(Double.valueOf(String.format("%.2f", personData)));

                    Double personMoney = allMoney/personNums; // 人均金额

                    besHouseholdData.setfPermanMoney(Double.valueOf(String.format("%.2f", personMoney))); // 人均金额
                }

                Double area = Double.parseDouble(besHouseholdConf.getfArea()); // 面积

                if (area > 0)
                {
                    Double unitareaData = data/area; // 单位面积能耗

                    besHouseholdData.setfUnitareaData(Double.valueOf(String.format("%.2f", unitareaData))); // 单位面积能耗

                    Double unitareaMoney = allMoney/area; // 单位面积金额

                    besHouseholdData.setfUnitareaMoney(Double.valueOf(String.format("%.2f", unitareaMoney)));
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }
             */
                /**
                 * 能耗存库
                 * 方案一：首先查询该记录是否存在，不存则新增数据，存在则更新数据
                 * 方案二：首先更新数据，更新的数据不存在则新增（√）
                 */
                // String count = besHouseholdDataMapper.queryHouseholdExists(besHouseholdData).getRows();

                try {
                    if (!besHouseholdDataMapper.updateHouseholdData(besHouseholdData)) {
                        besHouseholdData.setId(UUIDUtil.getRandom32BeginTimePK());
                        besHouseholdDataMapper.saveHouseholdData(besHouseholdData);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
