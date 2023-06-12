package com.zc.datareported.service;

import com.ruoyi.common.utils.DateUtils;
import com.zc.datareported.model.build.*;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildBaseInfo;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildGroupInfo;
import com.zc.efounder.JEnterprise.service.energyDataReport.impl.BuildGroupInfoServiceImpl;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;
import com.zc.common.framework.xmlprocessor.utils.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 建筑构建处理
 *
 * @author xiepufeng
 * @date 2020/11/11 11:15
 */
@Service
public class BuildXmlHandler
{
    /**
     * 文件存放位置
     */
    @Value("${data-centre.file-storage-location}")
    private String fileStorageLocation;
    @Autowired
    private BuildGroupInfoServiceImpl buildGroupInfoService;

    public boolean buildBuildXmlHandle(DataCenterBaseInfo dataCenterBaseInfo, List<BuildBaseInfo> buildBaseInfos, Date date)
    {
        if (dataCenterBaseInfo == null || buildBaseInfos == null || buildBaseInfos.isEmpty() || date == null)
        {
            return false;
        }

        BuildXmlModel buildXmlModel = new BuildXmlModel();

        // <common>
        CommonModel commonModel = new CommonModel();

        commonModel.setUploadDataCenterID(dataCenterBaseInfo.getDataCenterId()); // 数据中心代码

        String dateStr = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(date);

        commonModel.setCreateTime(dateStr); // 创建时间

        buildXmlModel.setCommon(commonModel);
        // </common>

        // <data>
        DataModel dataModel = new DataModel();

        // <DataCenterBaseInfo>
        DataCenterBaseInfoModel dataCenterBaseInfoModel = new DataCenterBaseInfoModel();

        dataCenterBaseInfoModel.setDataCenterID(dataCenterBaseInfo.getDataCenterId()); // 数据中心代码
        dataCenterBaseInfoModel.setDataCenterName(dataCenterBaseInfo.getDataCenterName()); // 数据中心名称
        dataCenterBaseInfoModel.setDataCenterType(dataCenterBaseInfo.getDataCenterType().intValue()); // 数据中心类型 1-数据中心 2-数据中心中转站
        dataCenterBaseInfoModel.setDataCenterManager(dataCenterBaseInfo.getDataCenterManager()); // 数据中心管理单位名称
        dataCenterBaseInfoModel.setDataCenterDesc(dataCenterBaseInfo.getDataCenterDesc()); // 数据中心描述
        dataCenterBaseInfoModel.setDataCenterTel(dataCenterBaseInfo.getDataCenterTel()); // 数据中心联系方式
        dataCenterBaseInfoModel.setDataCenterContract(dataCenterBaseInfo.getDataCenterContact()); // 业主联系人

        dataModel.setDataCenterBaseInfo(dataCenterBaseInfoModel);
        // </DataCenterBaseInfo>

        // <Build>

        //建筑信息列表
        List<BuildModel> buildModels = new ArrayList<>();

        //建筑信息ID列表
        List<String> buildIDList = new ArrayList<>();

        //建筑群列表
        List<BuildGroupBaseInfoModel> buildGroup = new ArrayList<>();

        //建筑群ID
        Long groupId=0L;
        buildBaseInfos.forEach((besBuildingInfo) -> {

            BuildModel buildModel = new BuildModel();
            buildModel.setId(String.valueOf(besBuildingInfo.getId())); // 节点代码
            buildIDList.add(String.valueOf(besBuildingInfo.getId()));

            BuildBaseInfoModel buildBaseInfo = new BuildBaseInfoModel();// 建筑节点信息
            buildBaseInfo.setOperation("N");
            buildBaseInfo.setDataCenterID(String.valueOf(besBuildingInfo.getDataCenterId())); // 数据中心代码
            buildBaseInfo.setBuildName(besBuildingInfo.getBuildName()); // 建筑物名称
            buildBaseInfo.setAliasName(besBuildingInfo.getAliasName()); // 建筑物别名
            buildBaseInfo.setBuildOwner(besBuildingInfo.getBuildOwner()); // 建筑物业主
            buildBaseInfo.setState(besBuildingInfo.getState().intValue()); // 建筑物监测状态，1-启用监测  2-停用监测
            buildBaseInfo.setDistrictCode(besBuildingInfo.getDistrictCode()); // 建筑物的6位行政区代码
            buildBaseInfo.setBuildAddr(besBuildingInfo.getBuildAddr()); // 建筑物地址
            buildBaseInfo.setBuildLong(besBuildingInfo.getBuildLong()); // 建筑物所在经度
            buildBaseInfo.setBuildLat(besBuildingInfo.getBuildLat()); // 建筑物所在纬度
            buildBaseInfo.setBuildYear(besBuildingInfo.getBuildYear().intValue()); // 建筑物建设时间
            buildBaseInfo.setUpFloor(besBuildingInfo.getUpFloor().intValue()); // 建筑物地上楼层数
            buildBaseInfo.setDownFloor(besBuildingInfo.getDownFloor().intValue()); // 建筑物地下楼层数
            buildBaseInfo.setBuildFunc(besBuildingInfo.getBuildFunc()); // 建筑物类型代码
            buildBaseInfo.setTotalArea(besBuildingInfo.getTotalArea()); // 建筑物总面积
            buildBaseInfo.setAirArea(besBuildingInfo.getAirArea()); // 空调面积
            buildBaseInfo.setHeatArea(besBuildingInfo.getHeatArea()); // 取暖面积
            buildBaseInfo.setAirType(besBuildingInfo.getAirType()); // 空调系统形式
            buildBaseInfo.setHeatType(besBuildingInfo.getHeatType()); // 采暖系统形式
            buildBaseInfo.setBodyCoef(String.valueOf(besBuildingInfo.getBodyCoef())); // 建筑物体形系数
            buildBaseInfo.setStruType(besBuildingInfo.getStruType()); // 建筑结构形式
            buildBaseInfo.setWallMatType(besBuildingInfo.getWallMatType()); // 外墙材料形式
            buildBaseInfo.setWallWarmType(besBuildingInfo.getWallWarmType()); // 外墙保温形式
            buildBaseInfo.setWallWinType(besBuildingInfo.getWallWinType()); // 建筑外窗类型
            buildBaseInfo.setGlassType(besBuildingInfo.getGlassType()); // 建筑玻璃类型
            buildBaseInfo.setWinFrameType(besBuildingInfo.getWinFrameType()); // 窗框材料类型
            buildBaseInfo.setIsStandard(besBuildingInfo.getIsStandard()); // 是否标杆建筑
            buildBaseInfo.setDesignDept(besBuildingInfo.getDesignDept()); // 监测方案设计单位
            buildBaseInfo.setWorkDept(besBuildingInfo.getWorkDept()); // 检测工程实施单位
            buildBaseInfo.setCreateTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,besBuildingInfo.getCreateTime())); // 录入时间
            buildBaseInfo.setAcceptDate(dateStr); // 开始上传能耗监测数据的日期

            buildModel.setBuildBaseInfo(buildBaseInfo);

            buildModels.add(buildModel);

            //如果建筑群ID改变 则查询详细数据
            if (!groupId.equals(besBuildingInfo.getBuildGroupId())){
                BuildGroupBaseInfoModel buildGroupBaseInfoModel= new BuildGroupBaseInfoModel(); // 建筑群信息
                // <BuildGroupBaseInfo>
                BuildGroupInfo buildGroupInfo=buildGroupInfoService.selectBuildGroupInfoById(besBuildingInfo.getBuildGroupId());

                buildGroupBaseInfoModel.setOperation("N");
                buildGroupBaseInfoModel.setBuildGroupName(buildGroupInfo.getBuildGroupName()); // 建筑群名称
                buildGroupBaseInfoModel.setGroupAliasName(buildGroupInfo.getGroupAliasName()); // 建筑群别名
                buildGroupBaseInfoModel.setGroupDesc(buildGroupInfo.getGroupDesc());// 建筑群描述
                buildGroup.add(buildGroupBaseInfoModel);
            }
        });

        dataModel.setBuild(buildModels);
        // </Build>

        // <BuildGroup>
        BuildGroupModel buildGroupModel = new BuildGroupModel();

        buildGroupModel.setId(dataCenterBaseInfo.getDataCenterId());// 数据中心代码
        /**建筑群列表**/
        buildGroupModel.setBuildGroupBaseInfo(buildGroup);

        // </BuildGroupBaseInfo>

        // <BuildGroupRelaInfo>

        BuildGroupRelateInfoModel buildGroupRelateInfoModel = new BuildGroupRelateInfoModel(); // 建筑群关联的建筑信息

        buildGroupRelateInfoModel.setOperation("N");
        buildGroupRelateInfoModel.setBuildID(buildIDList);

        buildGroupModel.setBuildGroupRelateInfo(buildGroupRelateInfoModel);
        // </BuildGroupRelaInfo>

        dataModel.setBuildGroup(buildGroupModel);

        // </BuildGroup>

        buildXmlModel.setData(dataModel);

        // </data>

        // 命名规则 上传文件目录名+build.xml

        return XmlUtils.createXmlFile(buildXmlModel, fileStorageLocation, DataRARHandler.getRarName(date, dataCenterBaseInfo.getDataCenterId()) + "Build");

    }

}
