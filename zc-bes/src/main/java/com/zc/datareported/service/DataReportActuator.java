package com.zc.datareported.service;

import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildBaseInfo;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 数据上报执行器
 *
 * @author xiepufeng
 * @date 2020/11/10 18:16
 */
@Component
public class DataReportActuator extends TimerTask
{

    /**
     * 执行周期 （一小时）
     */
    private final static int INTERVAL = 60 * 60 * 1000;
    /**
     * 每小时的第几分钟 （10分钟）
     */
    private static int DELAY = 10 * 60 * 1000;

    //数据获取
    @Resource
    private BasicDataHandler basicDataHandler;

    //建筑xml
    @Resource
    private BuildXmlHandler buildXmlHandler;

    //能源xml
    @Resource
    private EnergyXmlHandler energyXmlHandler;

    @Resource
    private DataRARHandler dataRARHandler;

    /**
     * 是否执行（0 不启用 1 启用）
     */
    @Value("${data-centre.enable}")
    private String enable;


    @Override
    public void run()
    {

        try
        {
            Date date = new Date();
            generateDate(date);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public boolean generateDate(Date date)
    {

        if (date == null)
        {
            return false;
        }
        // 获取数据中心基本信息
        DataCenterBaseInfo dataCenterBaseInfo = basicDataHandler.getDataCentreInfo();

        // 获取建筑信息根据数据中心id
        List<BuildBaseInfo> besBuildingInfoList = basicDataHandler.getBuildingInfoList(dataCenterBaseInfo);

        boolean buildXml = buildXmlHandler.buildBuildXmlHandle(dataCenterBaseInfo, besBuildingInfoList, date);

        if (!buildXml)
        {
            return false;
        }
        //获取分项能耗数据     key (建筑节点编号) value （key （分项编号） value （能耗数据））
        Map<Long, Map<String, Double>> energyData = basicDataHandler.getEnergyData(besBuildingInfoList, date);

        boolean energyXml = energyXmlHandler.buildEnergyXmlHandle(dataCenterBaseInfo, besBuildingInfoList,  energyData, date);

        if (!energyXml)
        {
            return false;
        }

        return dataRARHandler.compression(dataCenterBaseInfo, date);
    }


    /**
     * 启动
     */
    public void start()
    {

        if (!"1".equals(this.enable))
        {
            return;
        }

        Timer timer = new Timer(true);

        int delayMinute = DELAY / (60 * 1000);

        Calendar calendar = Calendar.getInstance();

        int currentMinute = calendar.get(Calendar.MINUTE);

        if (currentMinute > delayMinute)
        {
            DELAY = (60 - currentMinute + delayMinute) * 60 * 1000;
        }
        else
        {
            DELAY = (delayMinute - currentMinute)  * 60 * 1000;
        }

        timer.schedule(this, DELAY, INTERVAL);

    }
}
