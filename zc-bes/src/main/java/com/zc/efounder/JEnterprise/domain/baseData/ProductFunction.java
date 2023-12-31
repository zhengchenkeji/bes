package com.zc.efounder.JEnterprise.domain.baseData;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品配置-功能定义对象 athena_bes_product_function
 * 
 * @author gaojikun
 * @date 2023-03-08
 */
public class ProductFunction extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 产品主键 */
//    @Excel(name = "产品主键")
    private Long productId;

    @Excel(name = "产品编号")
    private String productCode;

    /** 功能编号 */
    @Excel(name = "功能编号")
    private String functionNum;

    /** 功能名称 */
    @Excel(name = "功能名称")
    private String name;

    /** 功能类型;0-控制;1-采集; */
    @Excel(name = "功能类型",readConverterExp="0=控制,1=采集")
    private String type;

    /** 下发方式;0-指令下发;1-数据项下发 */
    @Excel(name = "下发方式",readConverterExp="0=指令下发,1=数据项下发")
    private String issuedType;

    /** 通讯模式;0-同步;1-异步 */
    @Excel(name = "通讯模式",readConverterExp="0=同步,1=异步")
    private String communicationMode;

    /** 指令 */
//    @Excel(name = "指令")
    private String instruct;

    /** 数据长度 */
//    @Excel(name = "数据长度")
    private String dataLen;

    /** 创建人 */
//    @Excel(name = "创建人")
    private String createName;

    /** 修改人 */
//    @Excel(name = "修改人")
    private String updateName;

    /** 数据项 */
    private Long dataItem;

    /** 下发值 */
    private BigDecimal itemValue;

    /** 寄存器数量 */
    private Long registerNumber;

    /** 数据项参数信息 */
    private List<FunctionItemData> paramsList;

    /** 是否校检 */
    private String proofreadingState;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }
    public void setFunctionNum(String functionNum) 
    {
        this.functionNum = functionNum;
    }

    public String getFunctionNum() 
    {
        return functionNum;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setIssuedType(String issuedType) 
    {
        this.issuedType = issuedType;
    }

    public String getIssuedType() 
    {
        return issuedType;
    }
    public void setCommunicationMode(String communicationMode) 
    {
        this.communicationMode = communicationMode;
    }

    public String getCommunicationMode() 
    {
        return communicationMode;
    }
    public void setInstruct(String instruct) 
    {
        this.instruct = instruct;
    }

    public String getInstruct() 
    {
        return instruct;
    }
    public void setDataLen(String dataLen) 
    {
        this.dataLen = dataLen;
    }

    public String getDataLen() 
    {
        return dataLen;
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

    public Long getDataItem() {
        return dataItem;
    }

    public void setDataItem(Long dataItem) {
        this.dataItem = dataItem;
    }

    public BigDecimal getItemValue() {
        return itemValue;
    }

    public void setItemValue(BigDecimal itemValue) {
        this.itemValue = itemValue;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public List<FunctionItemData> getParamsList() {
        return paramsList;
    }

    public void setParamsList(List<FunctionItemData> paramsList) {
        this.paramsList = paramsList;
    }

    public Long getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(Long registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getProofreadingState() {
        return proofreadingState;
    }

    public void setProofreadingState(String proofreadingState) {
        this.proofreadingState = proofreadingState;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("productId", getProductId())
            .append("functionNum", getFunctionNum())
            .append("name", getName())
            .append("type", getType())
            .append("issuedType", getIssuedType())
            .append("communicationMode", getCommunicationMode())
            .append("instruct", getInstruct())
            .append("dataLen", getDataLen())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("createName", getCreateName())
            .append("updateTime", getUpdateTime())
            .append("updateName", getUpdateName())
            .toString();
    }
}
