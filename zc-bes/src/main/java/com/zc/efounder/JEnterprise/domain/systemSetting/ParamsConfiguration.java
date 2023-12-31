package com.zc.efounder.JEnterprise.domain.systemSetting;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;

/**
 * 采集参数关联对象
 *
 * @author gaojikun
 * @date 2022-11-30
 */
@ApiModel(value = "ParamsConfiguration",description = "采集参数关联对象")
public class  ParamsConfiguration extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(hidden = true)
    private Long id;

    /** 采集参数ID */
    @NotNull(message = "采集参数ID不能为空")
    @ApiModelProperty(value = "采集参数ID",required = true)
    private Long paramId;

    /** 采集参数分项ID */
    @NotNull(message = "采集参数分项ID不能为空")
    @ApiModelProperty(value = "采集参数分项ID",required = true)
    private Long paramsId;
    @ApiModelProperty(value = "模糊搜索")
    private String keywords;

    /** 参数类型；0-bes;1-第三方数据项 */
    private String deviceType;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }

    public Long getParamsId() {
        return paramsId;
    }

    public void setParamsId(Long paramsId) {
        this.paramsId = paramsId;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
