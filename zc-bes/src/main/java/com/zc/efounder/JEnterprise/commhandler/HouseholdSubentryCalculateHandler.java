package com.zc.efounder.JEnterprise.commhandler;


import com.zc.ApplicationContextProvider;
import com.ruoyi.common.utils.uuid.UUIDUtil;
import com.zc.efounder.JEnterprise.domain.commhandler.HouseholdSubitemData;
import com.zc.efounder.JEnterprise.mapper.commhandler.HouseholdSubitemDataMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;
import com.zc.connect.nettyServer.enums.StatisticalTypeEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * 计算分户分项数据
 *
 * @author xiepufeng
 * @date 2020/6/23 15:36
 */
public class HouseholdSubentryCalculateHandler {

    private static HouseholdSubitemDataMapper besHouseholdSubitemDataMapper = ApplicationContextProvider.getBean(HouseholdSubitemDataMapper.class);

    /**
     * 保存分户分项数据
     */
    public static void saveCalculateData(
            List<AthenaBesHouseholdBranchLink> fFhbhList,
            List<SubitemBranchLink> fFxbhList,
            String fCjsj,
            Double data,
            StatisticalTypeEnum typeEnum,
            String fYqbh,
            BigDecimal electricPriceSum) {

        if (null == fFhbhList
                || fFhbhList.isEmpty()
                || null == fFxbhList
                || fFxbhList.isEmpty()
                || null == fCjsj
                || null == data
                || null == fYqbh
                || null == typeEnum
                || null == electricPriceSum) {
            return;
        }

        HouseholdSubitemData besHouseholdSubitemData = new HouseholdSubitemData();

        besHouseholdSubitemData.setCollectTime(fCjsj); // 采集时间
        besHouseholdSubitemData.setDateType(typeEnum.getCode().toString()); // 统计类型
        besHouseholdSubitemData.setDataValue(data); // 支路能耗数据
        besHouseholdSubitemData.setElectricPriceSum(electricPriceSum); // 花费电价总金额


        try {

            for (AthenaBesHouseholdBranchLink besHouseholdBranchRlgl : fFhbhList) {

                String fFhbh = besHouseholdBranchRlgl.getHouseholdId().toString(); // 分户编号

                for (SubitemBranchLink besSubitem_branch_rlgl : fFxbhList) {

                    String fFxbh = besSubitem_branch_rlgl.getSubitemId().toString(); // 分项编号

                    besHouseholdSubitemData.setHouseholdId(fFhbh); // 设置分户编号

                    besHouseholdSubitemData.setSubitemId(fFxbh); // 设置分项编号

                    /**
                     * 能耗存库
                     * 方案一：首先查询该记录是否存在，不存则新增数据，存在则更新数据
                     * 方案二：首先更新数据，更新的数据不存在则新增（√）
                     */

                    // List<Map<String, String>> queryList = besHouseholdSubitemDataMapper.queryHouseholdGradeExists(fFhbh, fCjsj, typeEnum.getCode().toString(), fFxbh);

                    try {
                        if (!besHouseholdSubitemDataMapper.updateHouseholdGradeData(besHouseholdSubitemData)) {
                            besHouseholdSubitemData.setId(UUIDUtil.getRandom32BeginTimePK());
                            besHouseholdSubitemDataMapper.saveHouseholdGradeData(besHouseholdSubitemData);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
