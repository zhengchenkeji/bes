package com.zc.efounder.JEnterprise.service.partition.impl;

import com.zc.efounder.JEnterprise.domain.partition.PartitionParamModel;
import com.zc.efounder.JEnterprise.domain.partition.PartitionSingleModel;
import com.zc.efounder.JEnterprise.mapper.partition.PartitionMapper;
import com.zc.efounder.JEnterprise.service.partition.PartitionService;
import com.zc.common.core.model.DataReception;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 表分区
 */
@Service
public class PartitionServiceImpl implements PartitionService {

    @Resource
    public PartitionMapper partitionMapper;

    /**
     * 创建分区
     * @param partitionParamModel
     * @return
     */
    @Override
    public DataReception createPartition(PartitionParamModel partitionParamModel) {

        DataReception returnObject = new DataReception();

        if (partitionParamModel == null) {
            returnObject.setCode("0");
            returnObject.setMsg("参数错误！");
            return returnObject;
        }

        String tableName = partitionParamModel.getTableName();
        String rangeField = partitionParamModel.getRangeField();
        List<PartitionSingleModel> partitionSingleList = partitionParamModel.getPartitionSingleList();

        if (!StringUtils.hasText(tableName)
                || !StringUtils.hasText(rangeField)
                || partitionSingleList == null
                || partitionSingleList.isEmpty()
        ) {
            returnObject.setCode("0");
            returnObject.setMsg("参数错误！");
            return returnObject;
        }

        try {
            partitionMapper.createPartition(partitionParamModel);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode("0");
            returnObject.setMsg(e.getMessage());
            return returnObject;
        }

        returnObject.setCode("1");
        return returnObject;

    }

    /**
     * 添加分区
     * @param partitionParamModel
     * @return
     */
    @Override
    public DataReception addPartition(PartitionParamModel partitionParamModel) {

        DataReception returnObject = new DataReception();

        if (partitionParamModel == null) {
            returnObject.setCode("0");
            returnObject.setMsg("参数错误！");
            return returnObject;
        }

        String tableName = partitionParamModel.getTableName();
        List<PartitionSingleModel> partitionSingleList = partitionParamModel.getPartitionSingleList();

        if (!StringUtils.hasText(tableName)
                || partitionSingleList == null
                || partitionSingleList.isEmpty()
        ) {
            returnObject.setCode("0");
            returnObject.setMsg("参数错误！");
            return returnObject;
        }

        try {
            partitionMapper.addPartition(partitionParamModel);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode("0");
            returnObject.setMsg(e.getMessage());
            return returnObject;
        }

        returnObject.setCode("1");
        return returnObject;

    }

    /**
     * 查询表分区
     * @param tableName
     * @param partitionName
     * @return
     */
    @Override
    public DataReception queryPartition(String tableName, String partitionName) {

        DataReception returnObject = new DataReception();


        if (!StringUtils.hasText(tableName)) {
            returnObject.setCode("0");
            returnObject.setMsg("参数错误！");
            return returnObject;
        }

        try {
            returnObject.setData(partitionMapper.queryPartition(tableName, partitionName));
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode("0");
            returnObject.setMsg(e.getMessage());
            return returnObject;
        }

        returnObject.setCode("1");
        return returnObject;

    }

    /**
     * 删除表分区
     * @param tableName
     * @return
     */
    @Override
    public DataReception deletePartition(String tableName) {

        DataReception returnObject = new DataReception();


        if (!StringUtils.hasText(tableName)) {
            returnObject.setCode("0");
            returnObject.setMsg("参数错误！");
            return returnObject;
        }

        try {
            if (partitionMapper.deletePartition(tableName)) {
                returnObject.setCode("1");
            }else {
                returnObject.setCode("0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode("0");
            returnObject.setMsg(e.getMessage());
            return returnObject;
        }

        returnObject.setCode("1");
        return returnObject;

    }
}
