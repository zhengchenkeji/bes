package com.zc.efounder.JEnterprise.domain.energyInfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 园区对象 park
 *
 * @author ruoyi
 * @date 2022-09-08
 */
@ApiModel(value = "Park",description = "园区对象")
public class Park extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 园区编号 */
    @Excel(name = "园区编号")
    @ApiModelProperty(value = "园区编号",required = true)
    private String code;

    /** 用户名称 */
    @ApiModelProperty(value = "用户名称",required = true)
    @NotBlank(message = "用户名称不能为空!")
    private String userName;

    /** 组织机构id */
    @ApiModelProperty(value = "组织机构id",required = true)
    @NotBlank(message = "组织机构id不能为空!")
    private String organizationId;

    /** 园区名称 */
    @Excel(name = "园区名称")
    @ApiModelProperty(value = "园区名称",required = true)
    @NotBlank(message = "园区名称不能为空!")
    private String name;

    /** 节点编码 */
    @ApiModelProperty(value = "节点编码",required = true)
    @NotBlank(message = "节点编码不能为空!")
    private String nodeCode;

    /** 总面积 */
    @Excel(name = "总面积")
    @ApiModelProperty("总面积")
    private String allArea;

    /** 监测面积 */
    @Excel(name = "监测面积")
    @ApiModelProperty("监测面积")
    private String monitorArea;

    /** 供暖面积 */
    @Excel(name = "供暖面积")
    @ApiModelProperty("供暖面积")
    private String heatArea;

    /** 总人口 */
    @Excel(name = "总人口")
    @ApiModelProperty("总人口")
    private String personNums;

    /** 建筑时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "建筑时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "建筑时间",required = true)
    @NotNull(message = "建筑时间不能为空!")
    private Date buildTime;

    /** 设备运行时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "设备运行时间",required = true)
    @NotNull(message = "设备运行时间不能为空!")
    private Date equipmentRuntime;

    /** 联系人 */
    @Excel(name = "联系人")
    @ApiModelProperty("联系人")
    private String contactName;

    /** 联系人电话 */
    @Excel(name = "联系人电话")
    @ApiModelProperty("联系人电话")
    private String contactPhone;

    /** 联系人邮箱 */
    @Excel(name = "联系人邮箱")
    @ApiModelProperty("联系人邮箱")
    private String contactEmail;

    /** $column.columnComment */
    @ApiModelProperty("经度")
    @Excel(name = "经度")
    private String longitude;

    /** $column.columnComment */
    @ApiModelProperty("纬度")
    @Excel(name = "纬度")
    private String latitude;

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }
    public void setOrganizationId(String organizationId)
    {
        this.organizationId = organizationId;
    }

    public String getOrganizationId()
    {
        return organizationId;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setNodeCode(String nodeCode)
    {
        this.nodeCode = nodeCode;
    }

    public String getNodeCode()
    {
        return nodeCode;
    }
    public void setAllArea(String allArea)
    {
        this.allArea = allArea;
    }

    public String getAllArea()
    {
        return allArea;
    }
    public void setMonitorArea(String monitorArea)
    {
        this.monitorArea = monitorArea;
    }

    public String getMonitorArea()
    {
        return monitorArea;
    }
    public void setHeatArea(String heatArea)
    {
        this.heatArea = heatArea;
    }

    public String getHeatArea()
    {
        return heatArea;
    }
    public void setPersonNums(String personNums)
    {
        this.personNums = personNums;
    }

    public String getPersonNums()
    {
        return personNums;
    }
    public void setBuildTime(Date buildTime)
    {
        this.buildTime = buildTime;
    }

    public Date getBuildTime()
    {
        return buildTime;
    }
    public void setEquipmentRuntime(Date equipmentRuntime)
    {
        this.equipmentRuntime = equipmentRuntime;
    }

    public Date getEquipmentRuntime()
    {
        return equipmentRuntime;
    }
    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getContactName()
    {
        return contactName;
    }
    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }
    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    public String getContactEmail()
    {
        return contactEmail;
    }
    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getLongitude()
    {
        return longitude;
    }
    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLatitude()
    {
        return latitude;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("code", getCode())
            .append("userName", getUserName())
            .append("organizationId", getOrganizationId())
            .append("name", getName())
            .append("nodeCode", getNodeCode())
            .append("allArea", getAllArea())
            .append("monitorArea", getMonitorArea())
            .append("heatArea", getHeatArea())
            .append("personNums", getPersonNums())
            .append("buildTime", getBuildTime())
            .append("equipmentRuntime", getEquipmentRuntime())
            .append("contactName", getContactName())
            .append("contactPhone", getContactPhone())
            .append("contactEmail", getContactEmail())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("longitude", getLongitude())
            .append("latitude", getLatitude())
            .toString();
    }
}
