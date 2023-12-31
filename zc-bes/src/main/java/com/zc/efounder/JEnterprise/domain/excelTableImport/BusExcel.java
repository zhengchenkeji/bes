package com.zc.efounder.JEnterprise.domain.excelTableImport;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.common.utils.ExcelVOAttribute;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:23 2020/9/2
 * @Modified By:
 */
public class BusExcel extends BaseEntity {

    private static final long serialVersionUID = -8625917474807821019L;

    @Excel(name = "总线名称")
    private String sysName;

    @Excel(name = "端口号")
    private String port;

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
