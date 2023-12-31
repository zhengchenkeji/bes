package com.zc.efounder.JEnterprise.domain.excelTableImport;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.common.utils.ExcelVOAttribute;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 9:52 2020/9/17
 * @Modified By:
 */
public class CouplerExcel extends BaseEntity {
    private static final long serialVersionUID = -5047518246389194555L;

    @Excel(name = "耦合器名称")
    private String sysName;
    /**
     * 别名
     */
    private String alias;

    @Excel(name = "耦合器类型")
    private String deviceNodeId;

    @Excel(name = "通信地址")
    private String port;

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDeviceNodeId() {
        return deviceNodeId;
    }

    public void setDeviceNodeId(String deviceNodeId) {
        this.deviceNodeId = deviceNodeId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
