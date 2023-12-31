package com.zc.datareported.service;

import com.ruoyi.common.core.domain.entity.SubitemConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.commhandler.SubitemData;
import com.zc.efounder.JEnterprise.mapper.commhandler.SubitemDataMapper;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildBaseInfo;
import com.zc.efounder.JEnterprise.mapper.energyDataReport.BuildBaseInfoMapper;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;
import com.zc.efounder.JEnterprise.mapper.energyDataReport.DataCenterBaseInfoMapper;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xiepufeng
 * @date 2020/11/11 11:22
 */
@Service
public class BasicDataHandler
{
    @Resource
    private DataCenterBaseInfoMapper dataCenterBaseInfoMapper;

    @Resource
    private BuildBaseInfoMapper buildBaseInfoMapper;

    @Resource
    private SubitemDataMapper subitemDataMapper;

    @Resource
    private RedisCache redisCache;




    /**
     * 获取数据中心基本信息
     *
     * @return
     */
    public DataCenterBaseInfo getDataCentreInfo()
    {
        List<DataCenterBaseInfo> dataCenterBaseInfos = dataCenterBaseInfoMapper.selectDataCenterBaseInfoList(null);

        if (dataCenterBaseInfos == null || dataCenterBaseInfos.isEmpty())
        {
            return null;
        }

        // 目前只支持一个数据中心
        for (DataCenterBaseInfo dataCenterBaseInfo : dataCenterBaseInfos)
        {
            return dataCenterBaseInfo;
        }

        return null;
    }

    /**
     * 获取建筑信息根据数据中心id
     *
     * @return
     */
    public List<BuildBaseInfo> getBuildingInfoList(DataCenterBaseInfo dataCenterBaseInfo)
    {
        if (dataCenterBaseInfo == null || dataCenterBaseInfo.getId() == null)
        {
            return null;
        }

        return buildBaseInfoMapper.selectBuildBaseInfoListByCenterId(dataCenterBaseInfo.getId());
    }

    /**
     * 获取分项能耗数据
     *
     * @param date
     * @return key (建筑节点编号) value （key （分项编号） value （能耗数据））
     */
    public Map<Long, Map<String, Double>> getEnergyData(List<BuildBaseInfo> buildBaseInfoList, Date date)
    {


        if (buildBaseInfoList == null || buildBaseInfoList.isEmpty() || date == null)
        {
            return null;
        }

        // key 建筑节点编号 value （key 分项编号， 能耗数据）
        Map<Long, Map<String, Double>> budingSubitemMap = new LinkedHashMap<>();

        //获取分项数据
        Map<String, SubitemData>  besSubitemDataMap = getSubitemEnergyData(date);

        if (besSubitemDataMap == null || besSubitemDataMap.isEmpty())
        {
            return null;
        }

        buildBaseInfoList.forEach(besBudingInformation -> {
            //建筑ID
            Long budingId = besBudingInformation.getId();


            //获取分项配置
            List<SubitemConfig> besSubitemConfs = getSubitemConfigByBudingId(budingId);

            if (besSubitemConfs == null || besSubitemConfs.isEmpty())
            {
                return;
            }

            besSubitemConfs.sort(Comparator.comparing(o -> String.valueOf(o.getBuildingEnergyCode())));

            besSubitemConfs.forEach(besSubitemConf -> {

                Map<String, Double> dataMap = budingSubitemMap.computeIfAbsent(budingId, k -> new HashMap<>());

                Double value = 0.0;

                SubitemData besSubitemData = besSubitemDataMap.get(besSubitemConf.getSubitemId());

                if (besSubitemData != null)
                {
                    value = besSubitemData.getDataValue();
                }

                dataMap.put(besSubitemConf.getSubitemId(), value);

            });

        });



        return budingSubitemMap;
    }

    /**
     * 获取分项配置
     *
     * @param budingId 建筑ID
     * @return {@code List<SubitemConfig> }
     * @Author qindehua
     * @Date 2022/11/10
     **/
    private List<SubitemConfig> getSubitemConfigByBudingId(Long budingId){
        List<SubitemConfig> data=new ArrayList<>();
        Map<String,SubitemConfig> map=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig);
        for (SubitemConfig value : map.values()) {
            if (budingId.equals(value.getBuildingId())){
                data.add(value);
            }
        }
        return  data;
    }

    /**
     * 获取上一个小时分项能耗数据
     * @param date
     * @return key 分项编号 value 分项能耗数据
     */
    public Map<String, SubitemData> getSubitemEnergyData(Date date)
    {
        if (date == null)
        {
            return null;
        }

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, -1);

        String time = new SimpleDateFormat("YYYY-MM-dd HH:00:00").format(calendar.getTime());

        //获取分项数据
        List<SubitemData>  subitemData = subitemDataMapper.getSubitemDataByTimeAndType(time, "0");

        if (subitemData == null || subitemData.isEmpty())
        {
            return null;
        }

        Map<String, SubitemData> data = new HashMap<>();

        subitemData.forEach(item -> {
            data.put(item.getSubitemId(), item);
        });

        return data;

    }


}
