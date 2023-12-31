package com.zc.efounder.JEnterprise.mapper.partition;

import com.zc.efounder.JEnterprise.domain.partition.PartitionInfoModel;
import com.zc.efounder.JEnterprise.domain.partition.PartitionParamModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PartitionMapper {

    /**
     * 创建分区
     * @param partitionParamModel
     */
    void createPartition(PartitionParamModel partitionParamModel);

    /**
     * 添加表分区
     * @param partitionParamModel
     */
    void addPartition(PartitionParamModel partitionParamModel);

    /**
     * 查询分区信息
     * @param tableName
     * @param partitionName
     * @return
     */
    List<PartitionInfoModel> queryPartition(@Param("tableName") String tableName, @Param("partitionName") String partitionName);

    /**
     * 删除表分区
     * @param tableName
     * @return
     */
    Boolean deletePartition(@Param("tableName") String tableName);

}
