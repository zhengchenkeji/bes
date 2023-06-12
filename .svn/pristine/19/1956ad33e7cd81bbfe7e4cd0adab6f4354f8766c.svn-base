package com.zc.efounder.JEnterprise.domain.energyDataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 数据中心基本信息对象 data_center_base_info
 *
 * @author ruoyi
 * @date 2022-09-13
 */
@ApiModel(value = "DataCenterBaseInfo",description = "数据中心基本信息对象")
public class DataCenterBaseInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /** 数据中心代码由部里统一分配，用于唯一标识数据中心和数据中转站 */
    @Excel(name = "代码")
    @ApiModelProperty(value = "数据中心代码由部里统一分配，用于唯一标识数据中心和数据中转站",required = true)
    private String dataCenterId;

    /** 数据中心名称，最多24个汉字 */
    @Excel(name = "名称")
    @ApiModelProperty(value = "数据中心名称",required = true)
    private String dataCenterName;

    /** 数据中心ip地址 */
    @Excel(name = "ip地址")
    @ApiModelProperty(value = "数据中心ip地址",required = true)
    private String dataCenterIp;

    /** 数据中心端口号 */
    @Excel(name = "端口号")
    @ApiModelProperty(value = "数据中心端口号",required = true)
    private Long dataCenterPort;

    /** 数据中心类型
            1-数据中心
            2- 数据中转站 */
    @Excel(name = "类型", readConverterExp = "1=数据中心,2=数据中转站")
    @ApiModelProperty(value = "类型 1=数据中心,2=数据中转站",required = true)
    private Long dataCenterType;

    /** 数据中心主管单位名称，最多24个汉字 */
    @Excel(name = "主管单位")
    @ApiModelProperty(value = "数据中心主管单位名称，最多24个汉字",required = true)
    private String dataCenterManager;

    /** 用文字描述数据中心管理的城市数目、等信息 */
    @ApiModelProperty(value = "描述",required = true)
    @Excel(name = "描述")
    private String dataCenterDesc;

    /** 联系人 */
    @Excel(name = "联系人")
    @ApiModelProperty(value = "联系人",required = true)
    private String dataCenterContact;

    /** 联系电话 */
    @Excel(name = "联系电话")
    @ApiModelProperty(value = "联系电话",required = true)
    private String dataCenterTel;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setDataCenterIp(String dataCenterIp)
    {
        this.dataCenterIp = dataCenterIp;
    }

    public String getDataCenterIp()
    {
        return dataCenterIp;
    }
    public void setDataCenterPort(Long dataCenterPort)
    {
        this.dataCenterPort = dataCenterPort;
    }

    public Long getDataCenterPort()
    {
        return dataCenterPort;
    }
    public void setDataCenterId(String dataCenterId)
    {
        this.dataCenterId = dataCenterId;
    }

    public String getDataCenterId()
    {
        return dataCenterId;
    }
    public void setDataCenterName(String dataCenterName)
    {
        this.dataCenterName = dataCenterName;
    }

    public String getDataCenterName()
    {
        return dataCenterName;
    }
    public void setDataCenterType(Long dataCenterType)
    {
        this.dataCenterType = dataCenterType;
    }

    public Long getDataCenterType()
    {
        return dataCenterType;
    }
    public void setDataCenterManager(String dataCenterManager)
    {
        this.dataCenterManager = dataCenterManager;
    }

    public String getDataCenterManager()
    {
        return dataCenterManager;
    }
    public void setDataCenterDesc(String dataCenterDesc)
    {
        this.dataCenterDesc = dataCenterDesc;
    }

    public String getDataCenterDesc()
    {
        return dataCenterDesc;
    }
    public void setDataCenterContact(String dataCenterContact)
    {
        this.dataCenterContact = dataCenterContact;
    }

    public String getDataCenterContact()
    {
        return dataCenterContact;
    }
    public void setDataCenterTel(String dataCenterTel)
    {
        this.dataCenterTel = dataCenterTel;
    }

    public String getDataCenterTel()
    {
        return dataCenterTel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("dataCenterIp", getDataCenterIp())
            .append("dataCenterPort", getDataCenterPort())
            .append("dataCenterId", getDataCenterId())
            .append("dataCenterName", getDataCenterName())
            .append("dataCenterType", getDataCenterType())
            .append("dataCenterManager", getDataCenterManager())
            .append("dataCenterDesc", getDataCenterDesc())
            .append("dataCenterContact", getDataCenterContact())
            .append("dataCenterTel", getDataCenterTel())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
