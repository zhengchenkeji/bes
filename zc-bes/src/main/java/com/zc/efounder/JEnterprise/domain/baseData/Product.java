package com.zc.efounder.JEnterprise.domain.baseData;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.NotBlank;

/**
 * 产品定义对象 athena_bes_product
 *
 * @author gaojikun
 * @date 2023-03-07
 */
public class Product extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @ApiModelProperty(value = "产品主键",required = true)
    @NotBlank(message = "产品主键不能为空")
    private Long id;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @NotBlank(message = "产品名称不能为空")
    @ApiModelProperty(value = "产品名称",required = true)
    private String name;

    /** 产品编号 */
    @Excel(name = "产品编号")
    @NotBlank(message = "产品编号不能为空")
    @ApiModelProperty(value = "产品编号",required = true)
    private String code;

    /** 启用状态;0-停用;1-启用 */
    @Excel(name = "启用状态",readConverterExp="0=停用,1=正常")
    @NotBlank(message = "启用状态不能为空")
    @ApiModelProperty(value = "启用状态",required = true)
    private String state;

    /** 品类主键 */
    @NotBlank(message = "品类主键不能为空")
    @ApiModelProperty(value = "品类主键",required = true)
    private Long categoryId;
    @Excel(name = "品类")
    @NotBlank(message = "品类名称不能为空")
    @ApiModelProperty(value = "品类名称",required = true)
    private String categoryName;

    /** 物联类型 */
    @NotBlank(message = "物联类型不能为空")
    @ApiModelProperty(value = "物联类型",required = true)
    private String iotType;

    @Excel(name = "物联类型")
    @ApiModelProperty(value = "物联类型名称",required = true)
    private String iotTypeName;

    /** 通讯协议 */
    @Excel(name = "通讯协议")
    @NotBlank(message = "通讯协议不能为空")
    @ApiModelProperty(value = "通讯协议",required = true)
    private String communicationProtocol;

    @Excel(name = "通讯协议")
    private String communicationProtocolName;

    /** 消息协议 */
    @NotBlank(message = "消息协议不能为空")
    @ApiModelProperty(value = "消息协议",required = true)
    private Long messageProtocol;
    @Excel(name = "消息协议")
    private String messageProtocolName;

    /** 产品描述 */
    @Excel(name = "产品描述")
    @ApiModelProperty(value = "产品描述")
    private String productDescribe;

    /** 数据接入 */
    private String dataAccess;

    /** 创建人 */
//    @Excel(name = "创建人")
    private String createName;

    /** 修改人 */
//    @Excel(name = "修改人")
    private String updateName;

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
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
    public void setState(String state)
    {
        this.state = state;
    }

    public String getState()
    {
        return state;
    }
    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }
    public void setIotType(String iotType)
    {
        this.iotType = iotType;
    }

    public String getIotType()
    {
        return iotType;
    }
    public void setCommunicationProtocol(String communicationProtocol)
    {
        this.communicationProtocol = communicationProtocol;
    }

    public String getCommunicationProtocol()
    {
        return communicationProtocol;
    }
    public void setMessageProtocol(Long messageProtocol)
    {
        this.messageProtocol = messageProtocol;
    }

    public Long getMessageProtocol()
    {
        return messageProtocol;
    }
    public void setProductDescribe(String productDescribe)
    {
        this.productDescribe = productDescribe;
    }

    public String getProductDescribe()
    {
        return productDescribe;
    }
    public void setCreateName(String createName)
    {
        this.createName = createName;
    }

    public String getCreateName()
    {
        return createName;
    }
    public void setUpdateName(String updateName)
    {
        this.updateName = updateName;
    }

    public String getUpdateName()
    {
        return updateName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getIotTypeName() {
        return iotTypeName;
    }

    public void setIotTypeName(String iotTypeName) {
        this.iotTypeName = iotTypeName;
    }

    public String getCommunicationProtocolName() {
        return communicationProtocolName;
    }

    public void setCommunicationProtocolName(String communicationProtocolName) {
        this.communicationProtocolName = communicationProtocolName;
    }

    public String getMessageProtocolName() {
        return messageProtocolName;
    }

    public void setMessageProtocolName(String messageProtocolName) {
        this.messageProtocolName = messageProtocolName;
    }

    public String getDataAccess() {
        return dataAccess;
    }

    public void setDataAccess(String dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("code", getCode())
            .append("state", getState())
            .append("categoryId", getCategoryId())
            .append("iotType", getIotType())
            .append("communicationProtocol", getCommunicationProtocol())
            .append("messageProtocol", getMessageProtocol())
            .append("productDescribe", getProductDescribe())
            .append("createTime", getCreateTime())
            .append("createName", getCreateName())
            .append("updateTime", getUpdateTime())
            .append("updateName", getUpdateName())
            .toString();
    }
}
