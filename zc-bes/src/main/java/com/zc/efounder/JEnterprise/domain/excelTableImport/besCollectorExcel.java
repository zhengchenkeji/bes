package com.zc.efounder.JEnterprise.domain.excelTableImport;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.common.utils.ExcelVOAttribute;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 10:28 2020/9/2
 * @Modified By:
 */
public class besCollectorExcel extends BaseEntity {

    private static final long serialVersionUID = -8222656874382683079L;

    @Excel(name = "系统名称")
    private String fSysName;

    @Excel(name = "别名")
    private String fNickName;

    @Excel(name = "所属区域")
    private String fSsqy;

    @Excel(name = "安装位置")
    private String fAzwz;

    @Excel(name = "描述")
    private String fDescription;

    @Excel(name = "IP地址")
    private String fIpAddr;

    @Excel(name = "采集周期")
    private String fCollCycle;

    @Excel(name = "点类型")
    private String fNodeType;

    @Excel(name = "历史数据保存周期")
    private String fHisDataSaveCycle;

    @Excel(name = "园区编号")
    private String fYqbh;

    @Excel(name = "上传采样周期")
    private String fUploadCycle;

    @Excel(name = "默认网关")
    private String fGateway;

    @Excel(name = "子网掩码")
    private String fMask;

    @Excel(name = "主机ip")
    private String fIpMaster;

    @Excel(name = "主机端口")
    private String fPortMaster;


    public String getfSysName() {
        return fSysName;
    }

    public void setfSysName(String fSysName) {
        this.fSysName = fSysName;
    }

    public String getfNickName() {
        return fNickName;
    }

    public void setfNickName(String fNickName) {
        this.fNickName = fNickName;
    }

    public String getfSsqy() {
        return fSsqy;
    }

    public void setfSsqy(String fSsqy) {
        this.fSsqy = fSsqy;
    }

    public String getfAzwz() {
        return fAzwz;
    }

    public void setfAzwz(String fAzwz) {
        this.fAzwz = fAzwz;
    }

    public String getfDescription() {
        return fDescription;
    }

    public void setfDescription(String fDescription) {
        this.fDescription = fDescription;
    }

    public String getfIpAddr() {
        return fIpAddr;
    }

    public void setfIpAddr(String fIpAddr) {
        this.fIpAddr = fIpAddr;
    }

    public String getfCollCycle() {
        return fCollCycle;
    }

    public void setfCollCycle(String fCollCycle) {
        this.fCollCycle = fCollCycle;
    }

    public String getfNodeType() {
        return fNodeType;
    }

    public void setfNodeType(String fNodeType) {
        this.fNodeType = fNodeType;
    }

    public String getfHisDataSaveCycle() {
        return fHisDataSaveCycle;
    }

    public void setfHisDataSaveCycle(String fHisDataSaveCycle) {
        this.fHisDataSaveCycle = fHisDataSaveCycle;
    }

    public String getfYqbh() {
        return fYqbh;
    }

    public void setfYqbh(String fYqbh) {
        this.fYqbh = fYqbh;
    }

    public String getfUploadCycle() {
        return fUploadCycle;
    }

    public void setfUploadCycle(String fUploadCycle) {
        this.fUploadCycle = fUploadCycle;
    }

    public String getfGateway() {
        return fGateway;
    }

    public void setfGateway(String fGateway) {
        this.fGateway = fGateway;
    }

    public String getfMask() {
        return fMask;
    }

    public void setfMask(String fMask) {
        this.fMask = fMask;
    }

    public String getfIpMaster() {
        return fIpMaster;
    }

    public void setfIpMaster(String fIpMaster) {
        this.fIpMaster = fIpMaster;
    }

    public String getfPortMaster() {
        return fPortMaster;
    }

    public void setfPortMaster(String fPortMaster) {
        this.fPortMaster = fPortMaster;
    }
}
