package com.zc.efounder.JEnterprise.domain.electricPowerTranscription;

import java.util.List;
import java.util.Map;

/**
 * 电力集抄导出接收类
 *
 * @author liuwenge
 * @date 2022/11/28
 */
public class ElectricityStatementExport {


    /** 表头 */
    private List<Map<String, Object>> column;

    /** 数据列表 */
    private List<Map<String, Object>> dataList;


    public List<Map<String, Object>> getColumn() {
        return column;
    }

    public void setColumn(List<Map<String, Object>> column) {
        this.column = column;
    }

    public List<Map<String, Object>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
    }
}
