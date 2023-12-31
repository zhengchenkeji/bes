package com.zc.efounder.JEnterprise.domain.baseData;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 物联设备对象 athena_bes_equipment
 *
 * @author gaojikun
 * @date 2023-03-08
 */
public class Equipment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     *
     * @Description: Double类型id
     *
     * @auther: wanghongjie
     * @date: 14:29 2023/4/13
     * @param:
     * @return:
     *
     */
    private Double doubleId;

    /**
     * 父主键
     */
    private Long pId;

    /**
     * 父编号
     */
    @Excel(name = "父设备编号")
    private String parentCode;

    /**
     * 产品主键
     */
//    @Excel(name = "产品主键")
    private Long productId;

    /**
     * 产品编号
     */
    @Excel(name = "产品编号")
    private String productCode;

    /**
     * 设备编号
     */
    @Excel(name = "设备编号")
    private String code;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    private String name;

    /**
     * 设备状态;0-离线;1-在线;
     */
    @Excel(name = "设备状态;0=离线,1=在线", readConverterExp = "0=离线,1=在线")
    private String state;

    /**
     * 网络类型;0-客户端;1-服务器;
     */
    @Excel(name = "网络类型;0=客户端;1=服务器;", readConverterExp = "0=客户端,1=服务器")
    private String networkType;

    /**
     * ip地址
     */
    @Excel(name = "ip地址")
    private String ipAddress;

    /**
     * 设备地址
     */
    @Excel(name = "设备地址")
    private Long sonAddress;

    /**
     * 端口
     */
    @Excel(name = "端口")
    private String portNum;

    /**
     * 创建人
     */
//    @Excel(name = "创建人")
    private String createName;

    /**
     * 修改人
     */
//    @Excel(name = "修改人")
    private String updateName;

    /**
     * 通讯协议
     */
    private String communication;

    /**
     * 消息协议
     */
    private String message;

    /**
     * 物联类型
     */
    private String iotName;

    /**
     * 数据接入
     */
    private String dataAccessName;

    /**
     * 品类ID
     */
    private Long categoryId;

    /**
     * 子设备数量
     */
    private Integer sonNum;

    /**
     * 是否电表;0-否;1-是;
     */
    private String meterState;

    /**
     * 园区
     */
    private String parkCode;

    /**
     * 是否离线报警;0-否;1-是
     */
    private String offlineAlarm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    /***功能主键**/
    private Long functionId;

    /***功能编号**/
    private String functionNum;

    /***功能名称**/
    private String functionName;

    /***功能类型;0-控制;1-采集;**/
    private String functionType;

    /*****下发方式;0-指令下发;1-数据项下发*/
    private String issuedType;

    /***数据项读写类型;2-只写;0-只读;1-读写;**/
    private String readType;

    /***数据项参数;0-没有参数;1-有参数（高低位、结构体参数）;**/
    private String haveParams;

    /**存储当前采集参数，只在绑定支路时会插入数据*/
    private List<ProductItemData> itemDataList;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setPortNum(String portNum) {
        this.portNum = portNum;
    }

    public String getPortNum() {
        return portNum;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getCreateName() {
        return createName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public String getUpdateName() {
        return updateName;
    }

    public String getCommunication() {
        return communication;
    }

    public void setCommunication(String communication) {
        this.communication = communication;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIotName() {
        return iotName;
    }

    public void setIotName(String iotName) {
        this.iotName = iotName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Long getSonAddress() {
        return sonAddress;
    }

    public void setSonAddress(Long sonAddress) {
        this.sonAddress = sonAddress;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDataAccessName() {
        return dataAccessName;
    }

    public void setDataAccessName(String dataAccessName) {
        this.dataAccessName = dataAccessName;
    }

    public Integer getSonNum() {
        return sonNum;
    }

    public void setSonNum(Integer sonNum) {
        this.sonNum = sonNum;
    }

    public String getMeterState() {
        return meterState;
    }

    public void setMeterState(String meterState) {
        this.meterState = meterState;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public String getFunctionNum() {
        return functionNum;
    }

    public void setFunctionNum(String functionNum) {
        this.functionNum = functionNum;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getReadType() {
        return readType;
    }

    public void setReadType(String readType) {
        this.readType = readType;
    }

    public String getHaveParams() {
        return haveParams;
    }

    public void setHaveParams(String haveParams) {
        this.haveParams = haveParams;
    }

    public Double getDoubleId() {
        return doubleId;
    }

    public void setDoubleId(Double doubleId) {
        this.doubleId = doubleId;
    }


    public List<ProductItemData> getItemDataList() {
        return itemDataList;
    }

    public void setItemDataList(List<ProductItemData> itemDataList) {
        this.itemDataList = itemDataList;
    }

    public String getOfflineAlarm() {
        return offlineAlarm;
    }

    public void setOfflineAlarm(String offlineAlarm) {
        this.offlineAlarm = offlineAlarm;
    }

    public String getIssuedType() {
        return issuedType;
    }

    public void setIssuedType(String issuedType) {
        this.issuedType = issuedType;
    }

    public Equipment(Long id, String name, Long pId) {
        this.id = id;
        this.name = name;
        this.pId = pId;
    }

    public Equipment(){

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("productId", getProductId())
                .append("code", getCode())
                .append("name", getName())
                .append("state", getState())
                .append("ipAddress", getIpAddress())
                .append("portNum", getPortNum())
                .append("remark", getRemark())
                .append("createTime", getCreateTime())
                .append("createName", getCreateName())
                .append("updateTime", getUpdateTime())
                .append("functionId", getFunctionId())
                .append("functionNum", getFunctionNum())
                .append("functionName", getFunctionName())
                .append("functionType", getFunctionType())
                .toString();
    }
}
