package com.zc.efounder.JEnterprise.domain.deviceTree.dto;

import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;

import java.util.List;

public class AthenaElectricMeterDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 电表ID */
    private String meterId;

    /** 设备树ID */
    private Long deviceTreeId;

    /** 电表别名 */
    private String alias;

    /** 所属系统名称 */
    private String sysName;

    /** 采集方案id */
    private Long collectionMethodCode;

    private List<ElectricParams> electricParamsList;

    /** 类型;0-电表;1-点位 */
    private String type;

    public Long getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setDeviceTreeId(Long deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
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

    public Long getCollectionMethodCode() {
        return collectionMethodCode;
    }

    public void setCollectionMethodCode(Long collectionMethodCode) {
        this.collectionMethodCode = collectionMethodCode;
    }

    public List<ElectricParams> getElectricParamsList() {
        return electricParamsList;
    }

    public void setElectricParamsList(List<ElectricParams> electricParams) {
        this.electricParamsList = electricParams;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AthenaElectricMeterDto(String meterId, String alias, String sysName,
                                  Long collectionMethodCode, List<ElectricParams> electricParamsList,String type,Long deviceTreeId) {
        this.meterId = meterId;
        this.alias = alias;
        this.sysName = sysName;
        this.collectionMethodCode = collectionMethodCode;
        this.electricParamsList = electricParamsList;
        this.type = type;
        this.deviceTreeId = deviceTreeId;
    }

    public AthenaElectricMeterDto() {

    }
}
