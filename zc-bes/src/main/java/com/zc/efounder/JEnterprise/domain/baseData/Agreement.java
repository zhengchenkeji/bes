package com.zc.efounder.JEnterprise.domain.baseData;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备协议对象 athena_bes_agreement
 *
 * @author sunshangeng
 * @date 2023-03-14
 */
public class Agreement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 协议名称 */
    @Excel(name = "协议名称")
    private String agreementName;

    /** 协议编码 */
    @Excel(name = "协议编码")
    private String agreementCode;

    /** 协议类型 */
    @Excel(name = "协议类型",readConverterExp="0=http,1=mqtt,2=tcp,3=ups")
    private Integer agreementType;

    /** http接口地址 */
    @Excel(name = "http接口地址")
    private String httpAddress;

    /** mqtt订阅地址 */
    @Excel(name = "mqtt订阅地址")
    private String mqttAddress;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setAgreementName(String agreementName)
    {
        this.agreementName = agreementName;
    }

    public String getAgreementName()
    {
        return agreementName;
    }
    public void setAgreementCode(String agreementCode)
    {
        this.agreementCode = agreementCode;
    }

    public String getAgreementCode()
    {
        return agreementCode;
    }
    public void setAgreementType(Integer agreementType)
    {
        this.agreementType = agreementType;
    }

    public Integer getAgreementType()
    {
        return agreementType;
    }
    public void setHttpAddress(String httpAddress)
    {
        this.httpAddress = httpAddress;
    }

    public String getHttpAddress()
    {
        return httpAddress;
    }
    public void setMqttAddress(String mqttAddress)
    {
        this.mqttAddress = mqttAddress;
    }

    public String getMqttAddress()
    {
        return mqttAddress;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("agreementName", getAgreementName())
            .append("agreementCode", getAgreementCode())
            .append("agreementType", getAgreementType())
            .append("httpAddress", getHttpAddress())
            .append("mqttAddress", getMqttAddress())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
