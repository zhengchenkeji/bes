package com.zc.efounder.JEnterprise.domain.partition;

import java.util.List;

/**
 * 分区参数
 */
public class PartitionParamModel {

    private String tableName; // 表名

    private String rangeField; // 分区属字段名称

    private List<PartitionSingleModel> partitionSingleList;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRangeField() {
        return rangeField;
    }

    public void setRangeField(String rangeField) {
        this.rangeField = rangeField;
    }

    public List<PartitionSingleModel> getPartitionSingleList() {
        return partitionSingleList;
    }

    public void setPartitionSingleList(List<PartitionSingleModel> partitionSingleList) {
        this.partitionSingleList = partitionSingleList;
    }
}
