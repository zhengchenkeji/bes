package com.zc.efounder.JEnterprise.domain.energyInfo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;

/**
 * 手动录入能耗信息对象 athena_bes_manualentryenergy
 *
 * @author ruoyi
 * @date 2022-12-02
 */
@ApiModel(value = "ManualEntryEnergy",description = "手动录入能耗信息对象")
public class ManualEntryEnergy
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @ApiModelProperty(value = "唯一ID")
    private Long id;

    /** 控制器树id */
    @ApiModelProperty(value = "控制器树id",required = true)
    private Long controllerTreeid;

    @Excel(name = "控制器名称")
    @ApiModelProperty(value = "控制器名称")
    private String controllerName;
    /** 节点id */
    @ApiModelProperty(value = "节点id",required = true)
    private Long pointTreeid;

    @Excel(name = "点位名称")
    @ApiModelProperty(value = "点位名称")
    private String pointName;

    /** 能耗采集时间 */
    @Excel(name = "能耗采集时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "能耗采集时间",required = true)
    private Date energyCjsj;

    /** 能源类型 */
    @ApiModelProperty(value = "能源类型",required = true)
    private String energyType;

    /** 能源类型 名称*/
    @Excel(name = "能源类型")
    @ApiModelProperty(value = "能源类型 名称")
    private String energyTypeName;

    /**所选采集参数数组*/
    @ApiModelProperty(value = "所选采集参数数组",required = true)
    private List<ManualentryenergyCollection> electricparamsNameList;

    @Excel(name = "采集数值键值集合")
    @ApiModelProperty(value = "采集数值键值集合")
    private String excelParamValues;


    /**日期格式 年月日 */
    @ApiModelProperty(value = "日期格式 年月日")
    private Integer datetype;


    @ApiModelProperty(value = "创建时间",hidden = true)
    private  Date createTime;

    /**所选采集参数数组*/
    @ApiModelProperty(value = "所选采集参数数组")
    private String[]  paramsList;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setControllerTreeid(Long controllerTreeid)
    {
        this.controllerTreeid = controllerTreeid;
    }

    public Long getControllerTreeid()
    {
        return controllerTreeid;
    }
    public void setPointTreeid(Long pointTreeid)
    {
        this.pointTreeid = pointTreeid;
    }

    public Long getPointTreeid()
    {
        return pointTreeid;
    }
    public void setEnergyCjsj(Date energyCjsj)
    {
        this.energyCjsj = energyCjsj;
    }

    public Date getEnergyCjsj()
    {
        return energyCjsj;
    }
    public void setEnergyType(String energyType)
    {
        this.energyType = energyType;
    }

    public String getEnergyType()
    {
        return energyType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String[] getParamsList() {
        return paramsList;
    }

    public void setParamsList(String[] paramsList) {
        this.paramsList = paramsList;
    }

    public List<ManualentryenergyCollection> getElectricparamsNameList() {
        return electricparamsNameList;
    }

    public void setElectricparamsNameList(List<ManualentryenergyCollection> electricparamsNameList) {
        this.electricparamsNameList = electricparamsNameList;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getExcelParamValues() {
        return excelParamValues;
    }

    public void setExcelParamValues(String excelParamValues) {
        this.excelParamValues = excelParamValues;
    }


    public String getEnergyTypeName() {
        return energyTypeName;
    }

    public void setEnergyTypeName(String energyTypeName) {
        this.energyTypeName = energyTypeName;
    }


    public Integer getDatetype() {
        return datetype;
    }

    public void setDatetype(Integer datatype) {
        this.datetype = datatype;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("controllerTreeid", getControllerTreeid())
            .append("pointTreeid", getPointTreeid())
            .append("energyCjsj", getEnergyCjsj())
            .append("energyType", getEnergyType())
            .append("createtime", getCreateTime())
            .toString();
    }
}
