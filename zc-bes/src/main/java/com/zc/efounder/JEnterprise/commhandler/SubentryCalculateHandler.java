package com.zc.efounder.JEnterprise.commhandler;


import com.zc.ApplicationContextProvider;
import com.ruoyi.common.utils.uuid.UUIDUtil;
import com.zc.efounder.JEnterprise.domain.commhandler.SubitemData;
import com.zc.efounder.JEnterprise.mapper.commhandler.SubitemDataMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;
import com.zc.connect.nettyServer.enums.StatisticalTypeEnum;

import java.util.List;

/**
 *  计算分项数据
 * @author xiepufeng
 * @date 2020/6/19 10:21
 */
public class SubentryCalculateHandler
{

    private static SubitemDataMapper besSubitemDataMapper = ApplicationContextProvider.getBean(SubitemDataMapper.class);

    /**
     * 保存分项数据
     */
    public static void saveCalculateData(
            List<SubitemBranchLink> fFxbhList,
            String fCjsj,
            Double data,
            StatisticalTypeEnum typeEnum,
            String fYqbh)
    {

        if (null == fFxbhList
                || fFxbhList.isEmpty()
                || null == fCjsj
                || null == data
                || null == fYqbh
                || null == typeEnum)
        {
            return;
        }
        SubitemData besSubitemData = new SubitemData();

        besSubitemData.setCollectTime(fCjsj);
        besSubitemData.setDateType(typeEnum.getCode().toString());
        besSubitemData.setDataValue(data);
        besSubitemData.setParkId(fYqbh);

        try {
            for (SubitemBranchLink besSubitem_branch_rlgl : fFxbhList) {

                String fFxbh = besSubitem_branch_rlgl.getSubitemId().toString();
                besSubitemData.setSubitemId(fFxbh);

                /**
                 * 能耗存库
                 * 方案一：首先查询该记录是否存在，不存则新增数据，存在则更新数据
                 * 方案二：首先更新数据，更新的数据不存在则新增（√）
                 */
                // String count = besSubitemDataMapper.queryGradeExists(besSubitemData).getRows();

                try
                {
                    if (!besSubitemDataMapper.updateGradeData(besSubitemData))
                    {
                        besSubitemData.setId(UUIDUtil.getRandom32BeginTimePK());
                        besSubitemDataMapper.saveGradeData(besSubitemData);

                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
