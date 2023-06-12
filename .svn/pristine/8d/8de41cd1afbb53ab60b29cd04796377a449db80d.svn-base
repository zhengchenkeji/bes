package com.zc.iot.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 消费者对象 athena_iot_consumer
 * 
 * @author Athena-gongfanfei
 * @date 2021-10-27
 */
public class IotConsumer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 消费者名称 */
    @Excel(name = "消费者名称")
    private String name;

    /** 地址 */
    @Excel(name = "地址")
    private String url;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 消费组 */
    @Excel(name = "消费组")
    private Long consumerGroupKey;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setUrl(String url) 
    {
        this.url = url;
    }

    public String getUrl() 
    {
        return url;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setConsumerGroupKey(Long consumerGroupKey) 
    {
        this.consumerGroupKey = consumerGroupKey;
    }

    public Long getConsumerGroupKey() 
    {
        return consumerGroupKey;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("url", getUrl())
            .append("description", getDescription())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("consumerGroupKey", getConsumerGroupKey())
            .toString();
    }
}
