package com.zc.efounder.JEnterprise.domain.excelTableImport;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.common.utils.ExcelVOAttribute;

/**
 * @Author: wanghongjie
 * @Description:线路
 * @Date: Created in 15:24 2022/11/11
 * @Modified By:
 */
public class LineExcel extends BaseEntity  {
    private static final long serialVersionUID = 3202271891686538751L;

    /**
     * 所属节点类ID
     */
    @Excel(name = "线路名称")
    private String sysName;

    /**
     * 所属节点类ID
     */
    @Excel(name = "线路地址")
    private String lineAddr;

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getLineAddr() {
        return lineAddr;
    }

    public void setLineAddr(String lineAddr) {
        this.lineAddr = lineAddr;
    }
}
