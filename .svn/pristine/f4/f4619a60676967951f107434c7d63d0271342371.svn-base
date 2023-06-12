package com.zc.iot.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 服务订阅对象 athena_iot_service_sub
 * 
 * @author Athena-gongfanfei
 * @date 2021-10-27
 */
public class IotServiceSub extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 产品 */
    @Excel(name = "产品")
    private Long productKey;

    /** 消费类型  */
    @Excel(name = "消费类型 ")
    private Long consumptionType;

    /** 消费主体 */
    @Excel(name = "消费主体")
    private Long consumerGroupKey;

    /** 推送消息类型  */
    @Excel(name = "推送消息类型 ")
    private Long pushMessageType;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setProductKey(Long productKey) 
    {
        this.productKey = productKey;
    }

    public Long getProductKey() 
    {
        return productKey;
    }
    public void setConsumptionType(Long consumptionType) 
    {
        this.consumptionType = consumptionType;
    }

    public Long getConsumptionType() 
    {
        return consumptionType;
    }
    public void setConsumerGroupKey(Long consumerGroupKey) 
    {
        this.consumerGroupKey = consumerGroupKey;
    }

    public Long getConsumerGroupKey() 
    {
        return consumerGroupKey;
    }
    public void setPushMessageType(Long pushMessageType) 
    {
        this.pushMessageType = pushMessageType;
    }

    public Long getPushMessageType() 
    {
        return pushMessageType;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("productKey", getProductKey())
            .append("consumptionType", getConsumptionType())
            .append("consumerGroupKey", getConsumerGroupKey())
            .append("pushMessageType", getPushMessageType())
            .append("description", getDescription())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
