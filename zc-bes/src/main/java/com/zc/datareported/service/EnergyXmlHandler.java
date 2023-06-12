package com.zc.datareported.service;

import com.ruoyi.common.core.domain.entity.SubitemConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.datareported.model.build.CommonModel;
import com.zc.datareported.model.energy.BuildEnergyModel;
import com.zc.datareported.model.energy.EnergyDataModel;
import com.zc.datareported.model.energy.EnergyItemHourResultModel;
import com.zc.datareported.model.energy.EnergyXmlModel;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildBaseInfo;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.mapper.energyInfo.SubitemConfigMapper;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.framework.xmlprocessor.utils.XmlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 能耗数据构建处理
 *
 * @author xiepufeng
 * @date 2020/11/13 8:49
 */
@Service
public class EnergyXmlHandler
{
    /**
     * 文件存放位置
     */
    @Value("${data-centre.file-storage-location}")
    private String fileStorageLocation;

    @Resource
    private SubitemConfigMapper subitemConfigMapper;
    @Resource
    private RedisCache redisCache;

    /**
     * 构建 Energy.xml 文件
     * @param dataCenterBaseInfo 数据中心信息
     * @param energyData 能耗数据
     * @param date 日期
     * @return
     */
    public boolean buildEnergyXmlHandle(DataCenterBaseInfo dataCenterBaseInfo, List<BuildBaseInfo> besBuildingInfoList, Map<Long, Map<String, Double>> energyData, Date date)
    {
        if (dataCenterBaseInfo == null
                || energyData == null
                || energyData.isEmpty()
                || date == null
                || besBuildingInfoList == null
                || besBuildingInfoList.isEmpty())
        {
            return false;
        }
//
//        List<Long> budingList = new ArrayList<>();
///********暂时注释*******/
//        for (BuildBaseInfo besBudingInformation : besBuildingInfoList) {
//            budingList.add(besBudingInformation.getId());
//        }

        EnergyXmlModel energyXmlModel = new EnergyXmlModel();

        // <common>
        CommonModel commonModel = new CommonModel();

        commonModel.setUploadDataCenterID(dataCenterBaseInfo.getDataCenterId()); // 数据中心代码

        String dateStr = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(date);

        commonModel.setCreateTime(dateStr); // 创建时间

        energyXmlModel.setCommon(commonModel);
        // </common>

        // <data>
        EnergyDataModel energyDataModel = new EnergyDataModel();

        // <Build>  建筑能耗集合
        List<BuildEnergyModel> buildList = new ArrayList<>();

        String endHour = new SimpleDateFormat("YYYY-MM-dd HH:00:00").format(date);

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, - 1);

        String startHour = new SimpleDateFormat("YYYY-MM-dd HH:00:00").format(calendar.getTime());
       // budingId (建筑节点编号) subitemDataMap （key （分项编号） value （能耗数据））
        energyData.forEach((budingId, subitemDataMap) ->
        {
            BuildEnergyModel buildEnergyModel = new BuildEnergyModel();

            buildEnergyModel.setId(String.valueOf(budingId));

            /*******************暂未完善 先以建筑节点编号代替****************************/
            String hourResultID = budingId + new SimpleDateFormat("YYYYMMddHH").format(date);

            // <EnergyItemHourResult>
            List<EnergyItemHourResultModel> energyItemHourResultModelList = new ArrayList<>();
//            subitemDataMap （key （分项编号） value （能耗数据）
            subitemDataMap.forEach((subitemId, value) ->
            {
                EnergyItemHourResultModel energyItemHourResultModel = new EnergyItemHourResultModel();

                SubitemConfig besSubitemConf = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig,subitemId);
                if (besSubitemConf == null) {
                    SubitemConfig dataMapper = subitemConfigMapper.selectSubitemConfigBySubitemId(subitemId);
                    if (dataMapper == null) {
                        return;
                    } else {
                        besSubitemConf = dataMapper;
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig, dataMapper.getSubitemId(), dataMapper);
                    }
                }

                String energyCode =  besSubitemConf.getEnergyCode();//能源编号
                String subitemCode =  besSubitemConf.getBuildingEnergyCode();//建筑能耗代码

                //获取耗煤量
                Map<String, EnergyType> map=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
                EnergyType energyType =new EnergyType();

                for (EnergyType item : map.values()) {
                    if (energyCode.equals(item.getCode())){
                         energyType=item;
                        break;
                    }
                }
                //进行计算
                Double hourEquValue = energyType.getCoalAmount() * value;

                energyItemHourResultModel.setHourResultID(hourResultID);
                energyItemHourResultModel.setEnergyItemCode(subitemCode);
                energyItemHourResultModel.setStartHour(startHour);
                energyItemHourResultModel.setEndHour(endHour);
                energyItemHourResultModel.setHourValue(String.format("%.4f", value));
                energyItemHourResultModel.setHourEquValue(String.format("%.4f", hourEquValue));
                energyItemHourResultModel.setState(1);

                energyItemHourResultModelList.add(energyItemHourResultModel);

            });

            buildEnergyModel.setEnergyItemHourResult(energyItemHourResultModelList);
            // </EnergyItemHourResult>

            buildList.add(buildEnergyModel);

        });
        // </Build>
        energyDataModel.setBuild(buildList);
        // </data>

        energyXmlModel.setData(energyDataModel);

        return XmlUtils.createXmlFile(energyXmlModel, fileStorageLocation, DataRARHandler.getRarName(date, dataCenterBaseInfo.getDataCenterId()) + "Energy");

    }
}
