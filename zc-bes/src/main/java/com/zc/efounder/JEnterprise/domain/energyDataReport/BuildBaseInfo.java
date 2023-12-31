package com.zc.efounder.JEnterprise.domain.energyDataReport;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 建筑基本项数据对象 build_base_info
 *
 * @author ruoyi
 * @date 2022-09-14
 */
@ApiModel(value = "BuildBaseInfo",description = "建筑基本项数据对象")
public class BuildBaseInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /** 所属数据中心 */
    @ApiModelProperty(value = "所属数据中心",required = true)
    private Long dataCenterId;

    /** 所属数据中心名称 */
    @Excel(name = "所属数据中心")
    @ApiModelProperty(value = "所属数据中心名称")
    private String dataCenterName;

    /** 所属建筑群 */
    @ApiModelProperty(value = "所属建筑群",required = true)
    private Long buildGroupId;

    /** 所属建筑群名称 */
    @ApiModelProperty(value = "所属建筑群")
    @Excel(name = "所属建筑群")
    private String buildGroupName;

    /** 建筑名称 */
    @Excel(name = "建筑名称")
    @ApiModelProperty(value = "建筑名称",required = true)
    private String buildName;

    /** 建筑字母别名 */
    @Excel(name = "建筑字母别名")
    @ApiModelProperty(value = "建筑字母别名",required = true)
    private String aliasName;

    /** 业主 */
    @Excel(name = "业主")
    @ApiModelProperty(value = "业主",required = true)
    private String buildOwner;

    /** 监测状态 */
    @Excel(name = "监测状态", readConverterExp = "1=启用监测,0=停用监测")
    @ApiModelProperty(value = "监测状态",required = true)
    private Long state;

    /** 所属行政区划 */
    @Excel(name = "所属行政区划")
    @ApiModelProperty(value = "所属行政区划",required = true)
    private String districtCode;

    /** 建筑地址 */
    @Excel(name = "建筑地址")
    @ApiModelProperty(value = "建筑地址",required = true)
    private String buildAddr;

    /** 建筑坐标-经度 */
    @Excel(name = "建筑坐标-经度")
    @ApiModelProperty(value = "建筑坐标-经度",required = true)
    private String buildLong;

    /** 建筑坐标-纬度 */
    @Excel(name = "建筑坐标-纬度")
    @ApiModelProperty(value = "建筑坐标-纬度",required = true)
    private String buildLat;

    /** 建设年代 */
    @Excel(name = "建设年代")
    @ApiModelProperty(value = "建设年代",required = true)
    private Long buildYear;

    /** 地上建筑层数 */
    @Excel(name = "地上建筑层数")
    @ApiModelProperty(value = "地上建筑层数",required = true)
    private Long upFloor;

    /** 地下建筑层数 */
    @Excel(name = "地下建筑层数")
    @ApiModelProperty(value = "地下建筑层数",required = true)
    private Long downFloor;

    /** 建筑功能(建筑物类型代码) */
    @Excel(name = "建筑功能", readConverterExp = "A=办公建筑,B=商场建筑,C=宾馆饭店建筑,D=文化教育建筑,E=医疗卫生建筑,F=体育建筑,G=综合建筑,H=其它建筑")
    @ApiModelProperty(value = "建筑功能 A=办公建筑,B=商场建筑,C=宾馆饭店建筑,D=文化教育建筑,E=医疗卫生建筑,F=体育建筑,G=综合建筑,H=其它建筑",required = true)
    private String buildFunc;

    /** 建筑总面积 */
    @Excel(name = "建筑总面积")
    @ApiModelProperty(value = "建筑总面积",required = true)
    private String totalArea;

    /** 空调面积 */
    @Excel(name = "空调面积")
    @ApiModelProperty(value = "空调面积",required = true)
    private String airArea;

    /** 采暖面积 */
    @Excel(name = "采暖面积")
    @ApiModelProperty(value = "采暖面积",required = true)
    private String heatArea;

    /** 建筑空调系统形式 */
    @Excel(name = "建筑空调系统形式", readConverterExp = "A=集中式全空气系统,B=风机盘管+风机系统,C=分体式空调或VRV的局部式机组系统,D=其他")
    @ApiModelProperty(value = "建筑空调系统形式 A=集中式全空气系统,B=风机盘管+风机系统,C=分体式空调或VRV的局部式机组系统,D=其他",required = true)
    private String airType;

    /** 建筑采暖系统形式 */
    @Excel(name = "建筑采暖系统形式", readConverterExp = "A=散热器采暖,B=地板辐射采暖,C=电辐射采暖,D=空调系统集中采暖,E=其他")
    @ApiModelProperty(value = "建筑采暖系统形式 A=散热器采暖,B=地板辐射采暖,C=电辐射采暖,D=空调系统集中采暖,E=其他",required = true)
    private String heatType;

    /** 建筑体型系数 */
    @Excel(name = "建筑体型系数")
    @ApiModelProperty(value = "建筑体型系数")
    private Long bodyCoef;

    /** 建筑结构形式 */
    @Excel(name = "建筑结构形式", readConverterExp = "A=框架结构,B=框-剪结构,C=剪力墙结构,D=砖-混结构,E=钢结构,F=简体结构,G=木结构,H=其他")
    @ApiModelProperty(value = "建筑结构形式  A=框架结构,B=框-剪结构,C=剪力墙结构,D=砖-混结构,E=钢结构,F=简体结构,G=木结构,H=其他",required = true)
    private String struType;

    /** 外墙材料形式 */
    @Excel(name = "外墙材料形式", readConverterExp = "A=砖,B=建筑砌块,C=板材墙体,D=复合墙板和墙体,E=玻璃幕墙,F=其他")
    @ApiModelProperty(value = "外墙材料形式  A=砖,B=建筑砌块,C=板材墙体,D=复合墙板和墙体,E=玻璃幕墙,F=其他",required = true)
    private String wallMatType;

    /** 外墙保温形式 */
    @Excel(name = "外墙保温形式", readConverterExp = "A=内保温,B=外保温,C=夹心保温,D=其他")
    @ApiModelProperty(value = "外墙保温形式 A=内保温,B=外保温,C=夹心保温,D=其他",required = true)
    private String wallWarmType;

    /** 外窗类型 */
    @Excel(name = "外窗类型", readConverterExp = "A=单玻单层窗,B=单玻双层窗,C=单玻单层窗+单玻双层窗,D=中空双层玻璃窗,E=中空三层玻璃窗,F=中空充惰性气体,G=其他")
    @ApiModelProperty(value = "外窗类型 A=单玻单层窗,B=单玻双层窗,C=单玻单层窗+单玻双层窗,D=中空双层玻璃窗,E=中空三层玻璃窗,F=中空充惰性气体,G=其他",required = true)
    private String wallWinType;

    /** 玻璃类型 */
    @Excel(name = "玻璃类型", readConverterExp = "A=普通玻璃,B=镀膜玻璃,C=Low-e玻璃,D=其他")
    @ApiModelProperty(value = "玻璃类型 A=普通玻璃,B=镀膜玻璃,C=Low-e玻璃,D=其他",required = true)
    private String glassType;

    /** 窗框材料类型 */
    @Excel(name = "窗框材料类型", readConverterExp = "A=钢窗,B=铝合金,C=木窗,D=断热窗框,E=塑窗,F=其他")
    @ApiModelProperty(value = "窗框材料类型 A=钢窗,B=铝合金,C=木窗,D=断热窗框,E=塑窗,F=其他",required = true)
    private String winFrameType;

    /** 是否标杆建筑 */
    @Excel(name = "是否标杆建筑", readConverterExp = "true=是,false=否")
    @ApiModelProperty(value = "是否标杆建筑 true=是,false=否",required = true)
    private String isStandard;

    /** 监测方案设计单位 */
    @Excel(name = "监测方案设计单位")
    @ApiModelProperty(value = "监测方案设计单位",required = true)
    private String designDept;

    /** 监测工程实施单位 */
    @Excel(name = "监测工程实施单位")
    @ApiModelProperty(value = "监测工程实施单位",required = true)
    private String workDept;

    /** 录入人 */
    @ApiModelProperty(value = "录入人")
    private String createUser;


    /** 开始监测日期 */
    @ApiModelProperty(value = "开始监测日期")
    private Date monitorDate;

    /** 工程验收日期 */
    @ApiModelProperty(value = "工程验收日期")
    private Date acceptDate;

    /** 所属园区 */
    @ApiModelProperty(value = "所属园区",required = true)
    @Excel(name = "所属园区")
    private String parkId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setDataCenterId(Long dataCenterId)
    {
        this.dataCenterId = dataCenterId;
    }

    public Long getDataCenterId()
    {
        return dataCenterId;
    }
    public void setBuildGroupId(Long buildGroupId)
    {
        this.buildGroupId = buildGroupId;
    }

    public Long getBuildGroupId()
    {
        return buildGroupId;
    }
    public void setBuildName(String buildName)
    {
        this.buildName = buildName;
    }

    public String getBuildName()
    {
        return buildName;
    }
    public void setAliasName(String aliasName)
    {
        this.aliasName = aliasName;
    }

    public String getAliasName()
    {
        return aliasName;
    }
    public void setBuildOwner(String buildOwner)
    {
        this.buildOwner = buildOwner;
    }

    public String getBuildOwner()
    {
        return buildOwner;
    }
    public void setState(Long state)
    {
        this.state = state;
    }

    public Long getState()
    {
        return state;
    }
    public void setDistrictCode(String districtCode)
    {
        this.districtCode = districtCode;
    }

    public String getDistrictCode()
    {
        return districtCode;
    }
    public void setBuildAddr(String buildAddr)
    {
        this.buildAddr = buildAddr;
    }

    public String getBuildAddr()
    {
        return buildAddr;
    }
    public void setBuildLong(String buildLong)
    {
        this.buildLong = buildLong;
    }

    public String getBuildLong()
    {
        return buildLong;
    }
    public void setBuildLat(String buildLat)
    {
        this.buildLat = buildLat;
    }

    public String getBuildLat()
    {
        return buildLat;
    }
    public void setBuildYear(Long buildYear)
    {
        this.buildYear = buildYear;
    }

    public Long getBuildYear()
    {
        return buildYear;
    }
    public void setUpFloor(Long upFloor)
    {
        this.upFloor = upFloor;
    }

    public Long getUpFloor()
    {
        return upFloor;
    }
    public void setDownFloor(Long downFloor)
    {
        this.downFloor = downFloor;
    }

    public Long getDownFloor()
    {
        return downFloor;
    }
    public void setBuildFunc(String buildFunc)
    {
        this.buildFunc = buildFunc;
    }

    public String getBuildFunc()
    {
        return buildFunc;
    }
    public void setTotalArea(String totalArea)
    {
        this.totalArea = totalArea;
    }

    public String getTotalArea()
    {
        return totalArea;
    }
    public void setAirArea(String airArea)
    {
        this.airArea = airArea;
    }

    public String getAirArea()
    {
        return airArea;
    }
    public void setHeatArea(String heatArea)
    {
        this.heatArea = heatArea;
    }

    public String getHeatArea()
    {
        return heatArea;
    }
    public void setAirType(String airType)
    {
        this.airType = airType;
    }

    public String getAirType()
    {
        return airType;
    }
    public void setHeatType(String heatType)
    {
        this.heatType = heatType;
    }

    public String getHeatType()
    {
        return heatType;
    }
    public void setBodyCoef(Long bodyCoef)
    {
        this.bodyCoef = bodyCoef;
    }

    public Long getBodyCoef()
    {
        return bodyCoef;
    }
    public void setStruType(String struType)
    {
        this.struType = struType;
    }

    public String getStruType()
    {
        return struType;
    }
    public void setWallMatType(String wallMatType)
    {
        this.wallMatType = wallMatType;
    }

    public String getWallMatType()
    {
        return wallMatType;
    }
    public void setWallWarmType(String wallWarmType)
    {
        this.wallWarmType = wallWarmType;
    }

    public String getWallWarmType()
    {
        return wallWarmType;
    }
    public void setWallWinType(String wallWinType)
    {
        this.wallWinType = wallWinType;
    }

    public String getWallWinType()
    {
        return wallWinType;
    }
    public void setGlassType(String glassType)
    {
        this.glassType = glassType;
    }

    public String getGlassType()
    {
        return glassType;
    }
    public void setWinFrameType(String winFrameType)
    {
        this.winFrameType = winFrameType;
    }

    public String getWinFrameType()
    {
        return winFrameType;
    }
    public void setIsStandard(String isStandard)
    {
        this.isStandard = isStandard;
    }

    public String getIsStandard()
    {
        return isStandard;
    }
    public void setDesignDept(String designDept)
    {
        this.designDept = designDept;
    }

    public String getDesignDept()
    {
        return designDept;
    }
    public void setWorkDept(String workDept)
    {
        this.workDept = workDept;
    }

    public String getWorkDept()
    {
        return workDept;
    }
    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    public String getCreateUser()
    {
        return createUser;
    }
    public void setMonitorDate(Date monitorDate)
    {
        this.monitorDate = monitorDate;
    }

    public Date getMonitorDate()
    {
        return monitorDate;
    }
    public void setAcceptDate(Date acceptDate)
    {
        this.acceptDate = acceptDate;
    }

    public Date getAcceptDate()
    {
        return acceptDate;
    }
    public void setParkId(String parkId)
    {
        this.parkId = parkId;
    }

    public String getParkId()
    {
        return parkId;
    }

    public String getDataCenterName() {
        return dataCenterName;
    }

    public void setDataCenterName(String dataCenterName) {
        this.dataCenterName = dataCenterName;
    }

    public String getBuildGroupName() {
        return buildGroupName;
    }

    public void setBuildGroupName(String buildGroupName) {
        this.buildGroupName = buildGroupName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("dataCenterId", getDataCenterId())
            .append("buildGroupId", getBuildGroupId())
            .append("buildName", getBuildName())
            .append("aliasName", getAliasName())
            .append("buildOwner", getBuildOwner())
            .append("state", getState())
            .append("districtCode", getDistrictCode())
            .append("buildAddr", getBuildAddr())
            .append("buildLong", getBuildLong())
            .append("buildLat", getBuildLat())
            .append("buildYear", getBuildYear())
            .append("upFloor", getUpFloor())
            .append("downFloor", getDownFloor())
            .append("buildFunc", getBuildFunc())
            .append("totalArea", getTotalArea())
            .append("airArea", getAirArea())
            .append("heatArea", getHeatArea())
            .append("airType", getAirType())
            .append("heatType", getHeatType())
            .append("bodyCoef", getBodyCoef())
            .append("struType", getStruType())
            .append("wallMatType", getWallMatType())
            .append("wallWarmType", getWallWarmType())
            .append("wallWinType", getWallWinType())
            .append("glassType", getGlassType())
            .append("winFrameType", getWinFrameType())
            .append("isStandard", getIsStandard())
            .append("designDept", getDesignDept())
            .append("workDept", getWorkDept())
            .append("createTime", getCreateTime())
            .append("createUser", getCreateUser())
            .append("monitorDate", getMonitorDate())
            .append("acceptDate", getAcceptDate())
            .append("parkId", getParkId())
            .append("buildGroupName", getBuildGroupName())
            .append("dataCenterName", getDataCenterName())
            .toString();
    }
}
