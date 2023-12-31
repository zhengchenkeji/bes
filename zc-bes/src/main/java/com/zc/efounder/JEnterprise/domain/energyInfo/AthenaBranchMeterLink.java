package com.zc.efounder.JEnterprise.domain.energyInfo;

import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 支路电表关联对象 athena_bes_branch_meter_link
 * @author qindehua
 */
public class AthenaBranchMeterLink extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 关联ID */
    private Long id;
    /** 设备树ID */
    private Long deviceTreeId;

    /** 所属支路 */
    private Long branchId;

    /** 所属电表 - 系统名称 */
    private Long meterId;

    /** 运算符 0：减   1：加 */
    private String operator;

    /** 系统名称 */
    private String sysName;

    /** 电表别名 */
    private String alias;
    /** 电能参数 */
    private String electricParam;

    private List<ElectricParams> electricParamsList;

    /** 类型；point-点位 meter-电表 */
    private String type;

    /** 设备类型 0：bes设备  1：第三方设备*/
    private String deviceType;

    /**采集方案**/
    private Long collectionMethodCode;

    public Long getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setDeviceTreeId(Long deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public Long getCollectionMethodCode() {
        return collectionMethodCode;
    }

    public void setCollectionMethodCode(Long collectionMethodCode) {
        this.collectionMethodCode = collectionMethodCode;
    }


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setBranchId(Long branchId)
    {
        this.branchId = branchId;
    }

    public Long getBranchId()
    {
        return branchId;
    }
    public void setMeterId(Long meterId)
    {
        this.meterId = meterId;
    }

    public Long getMeterId()
    {
        return meterId;
    }
    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getOperator()
    {
        return operator;
    }

    public String getElectricParam() {
        return electricParam;
    }

    public void setElectricParam(String electricParam) {
        this.electricParam = electricParam;
    }

    public List<ElectricParams> getElectricParamsList() {
        return electricParamsList;
    }

    public void setElectricParamsList(List<ElectricParams> electricParamsList) {
        this.electricParamsList = electricParamsList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("branchId", getBranchId())
            .append("meterId", getMeterId())
            .append("operator", getOperator())
            .append("sysName", getSysName())
            .append("alias", getAlias())
            .append("electricParam", getElectricParam())
            .append("type", getType())
                .append("deviceType", getDeviceType())

                .toString();
    }

    public AthenaBranchMeterLink() {
    }

    public AthenaBranchMeterLink(Long branchId, Long meterId, String operator,String electricParam) {
        this.branchId = branchId;
        this.meterId = meterId;
        this.operator = operator;
        this.electricParam = electricParam;
    }

    public AthenaBranchMeterLink(Long branchId, Long meterId, String operator,String electricParam,
                                 String sysName, String alias,String type,Long collectionMethodCode,Long deviceTreeId,String deviceType) {
        this.branchId = branchId;
        this.meterId = meterId;
        this.operator = operator;
        this.sysName = sysName;
        this.alias = alias;
        this.electricParam = electricParam;
        this.type = type;
        this.collectionMethodCode = collectionMethodCode;
        this.deviceTreeId = deviceTreeId;
        this.deviceType=deviceType;
    }
}
