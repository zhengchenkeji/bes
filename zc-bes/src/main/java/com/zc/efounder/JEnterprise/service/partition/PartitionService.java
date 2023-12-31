package com.zc.efounder.JEnterprise.service.partition;

import com.zc.efounder.JEnterprise.domain.partition.PartitionParamModel;
import com.zc.common.core.model.DataReception;

/**
 * 数据库表分区
 */
public interface PartitionService {

    /**
     * 创建表分区
     * @param partitionParamModel
     * @return
     */
    DataReception createPartition(PartitionParamModel partitionParamModel);
    /**
     * 添加表分区
     * @param partitionParamModel
     * @return
     */
    DataReception addPartition(PartitionParamModel partitionParamModel);

    /**
     * 查询表分区
     * @param tableName
     * @param partitionName
     * @return
     */
    DataReception queryPartition(String tableName, String partitionName);

    /**
     * 删除表分区
     * @param tableName
     * @return
     */
    DataReception deletePartition(String tableName);
}
