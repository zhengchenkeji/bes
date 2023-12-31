package com.zc.efounder.JEnterprise.domain.baseData;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 产品配置-数据项对象 athena_bes_product_iteam_data
 *
 * @author gaojikun
 * @date 2023-03-07
 */
public class ProductItemData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 序号 */
    @Excel(name = "排序")
    private Long sortNum;

    /** 产品主键 */
//    @Excel(name = "产品主键")
    private Long productId;

    @Excel(name = "产品编号")
    private String productCode;

    /** 数据项编号(对应设备) */
    @Excel(name = "数据项编号(对应设备)")
    private String dataItemNum;

    /** 数据项标识(平台自定义) */
    @Excel(name = "数据项标识(平台自定义)")
    private String dataItemMark;

    /** 数据项名称 */
    @Excel(name = "数据项名称")
    private String name;

    /** 数据类型 */
    private String dataType;

    @Excel(name = "数据类型")
    private String dataName;

    /** 单位 */
    @Excel(name = "单位")
    private String dataUnit;

    /** 读写类型;0-只读;1-读写;2-只写 */
    @Excel(name = "读写类型",readConverterExp="0=只读,1=读写,2=只写")
    private String readType;

    /** 是否保存;0-否;1-是; */
    @Excel(name = "是否保存",readConverterExp="0=否,1=是")
    private String preserveType;

    /** 小数点位置; */
    private String radixPoint;

    /**比率*/
    private String ratioSize;

    /** 创建人 */
//    @Excel(name = "创建人")
    private String createName;

    /** 修改人 */
//    @Excel(name = "修改人")
    private String updateName;

    /** 数据值 */
    private BigDecimal dataValue;

    /** 更新时间 */
    private Date dataTime;

    /** 读功能码 */
    private String functionCode;

    /** 写功能码 */
    private String writeFunctionCode;

    /**mqtt订阅主题*/
    private String subscribeAddress;

    /**能源类型*/
    private String energyCode;

    /**能源类型*/
    private String energyName;

    /**枚举参数*/
    private List<ParamsItemData> enumDetail;

    /**高位参数*/
    private List<ProductItemData> highDetail;

    /**低位参数*/
    private List<ProductItemData> lowDetail;

    /**结构体参数*/
    private List<ProductItemData> structDetail;

    /**高低位*/
    private String highLow;

    /**二进制*/
    private String binarySystem;

    /**字节长度*/
    private Long byteLength;

    /**数据项id*/
    private Long itemDataId;

    /**参数类型*/
    private String paramsType;

    /**下发值*/
    private BigDecimal sendValue;


    /**寄存器数量 null按照1个寄存器处理*/
    private Integer registersNum;

    /** 设备id */
    private Long equipmentId;

    /** 设备自定义数据项名称 */
    private String itemDataCustomName;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setSortNum(Long sortNum)
    {
        this.sortNum = sortNum;
    }

    public Long getSortNum()
    {
        return sortNum;
    }
    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Long getProductId()
    {
        return productId;
    }
    public void setDataItemNum(String dataItemNum)
    {
        this.dataItemNum = dataItemNum;
    }

    public String getDataItemNum()
    {
        return dataItemNum;
    }
    public void setDataItemMark(String dataItemMark)
    {
        this.dataItemMark = dataItemMark;
    }

    public String getDataItemMark()
    {
        return dataItemMark;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setDataType(String dataType)
    {
        this.dataType = dataType;
    }

    public String getDataType()
    {
        return dataType;
    }
    public void setDataUnit(String dataUnit)
    {
        this.dataUnit = dataUnit;
    }

    public String getDataUnit()
    {
        return dataUnit;
    }
    public void setReadType(String readType)
    {
        this.readType = readType;
    }

    public String getReadType()
    {
        return readType;
    }
    public void setPreserveType(String preserveType)
    {
        this.preserveType = preserveType;
    }

    public String getPreserveType()
    {
        return preserveType;
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

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public BigDecimal getDataValue() {
        return dataValue;
    }

    public void setDataValue(BigDecimal dataValue) {
        this.dataValue = dataValue;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public String getWriteFunctionCode() {
        return writeFunctionCode;
    }

    public void setWriteFunctionCode(String writeFunctionCode) {
        this.writeFunctionCode = writeFunctionCode;
    }

    public String getSubscribeAddress() {
        return subscribeAddress;
    }

    public void setSubscribeAddress(String subscribeAddress) {
        this.subscribeAddress = subscribeAddress;
    }

    public String getRadixPoint() {
        return radixPoint;
    }

    public void setRadixPoint(String radixPoint) {
        this.radixPoint = radixPoint;
    }

    public String getRatioSize() {
        return ratioSize;
    }

    public void setRatioSize(String ratioSize) {
        this.ratioSize = ratioSize;
    }

    public String getEnergyCode() {
        return energyCode;
    }

    public void setEnergyCode(String energyCode) {
        this.energyCode = energyCode;
    }

    public List<ParamsItemData> getEnumDetail() {
        return enumDetail;
    }

    public void setEnumDetail(List<ParamsItemData> enumDetail) {
        this.enumDetail = enumDetail;
    }

    public List<ProductItemData> getHighDetail() {
        return highDetail;
    }

    public void setHighDetail(List<ProductItemData> highDetail) {
        this.highDetail = highDetail;
    }

    public List<ProductItemData> getLowDetail() {
        return lowDetail;
    }

    public void setLowDetail(List<ProductItemData> lowDetail) {
        this.lowDetail = lowDetail;
    }

    public String getHighLow() {
        return highLow;
    }

    public void setHighLow(String highLow) {
        this.highLow = highLow;
    }

    public String getBinarySystem() {
        return binarySystem;
    }

    public void setBinarySystem(String binarySystem) {
        this.binarySystem = binarySystem;
    }

    public Long getByteLength() {
        return byteLength;
    }

    public void setByteLength(Long byteLength) {
        this.byteLength = byteLength;
    }

    public List<ProductItemData> getStructDetail() {
        return structDetail;
    }

    public void setStructDetail(List<ProductItemData> structDetail) {
        this.structDetail = structDetail;
    }

    public Long getItemDataId() {
        return itemDataId;
    }

    public void setItemDataId(Long itemDataId) {
        this.itemDataId = itemDataId;
    }

    public String getParamsType() {
        return paramsType;
    }

    public void setParamsType(String paramsType) {
        this.paramsType = paramsType;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public BigDecimal getSendValue() {
        return sendValue;
    }

    public void setSendValue(BigDecimal sendValue) {
        this.sendValue = sendValue;
    }

    public Integer getRegistersNum() {
        return registersNum;
    }

    public void setRegistersNum(Integer registersNum) {
        this.registersNum = registersNum;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getItemDataCustomName() {
        return itemDataCustomName;
    }

    public void setItemDataCustomName(String itemDataCustomName) {
        this.itemDataCustomName = itemDataCustomName;
    }

    public String getEnergyName() {
        return energyName;
    }

    public void setEnergyName(String energyName) {
        this.energyName = energyName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("sortNum", sortNum)
                .append("productId", productId)
                .append("productCode", productCode)
                .append("dataItemNum", dataItemNum)
                .append("dataItemMark", dataItemMark)
                .append("name", name)
                .append("equipmentId", equipmentId)
                .append("itemDataCustomName", itemDataCustomName)
                .append("dataType", dataType)
                .append("dataName", dataName)
                .append("dataUnit", dataUnit)
                .append("readType", readType)
                .append("preserveType", preserveType)
                .append("radixPoint", radixPoint)
                .append("ratioSize", ratioSize)
                .append("createName", createName)
                .append("updateName", updateName)
                .append("dataValue", dataValue)
                .append("dataTime", dataTime)
                .append("functionCode", functionCode)
                .append("writeFunctionCode", writeFunctionCode)
                .append("subscribeAddress", subscribeAddress)
                .append("energyCode", energyCode)
                .append("enumDetail", enumDetail)
                .append("highDetail", highDetail)
                .append("lowDetail", lowDetail)
                .append("structDetail", structDetail)
                .append("highLow", highLow)
                .append("binarySystem", binarySystem)
                .append("byteLength", byteLength)
                .append("itemDataId", itemDataId)
                .append("paramsType", paramsType)
                .append("sendValue", sendValue)
                .append("registersNum", registersNum)
                .toString();
    }
}
