package com.zc.iot.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.zc.iot.domain.IotConsumerGroup;
import com.zc.iot.mapper.IotConsumerGroupMapper;
import com.zc.iot.service.IIotConsumerGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消费组Service业务层处理
 * 
 * @author Athena-gongfanfei
 * @date 2021-10-27
 */
@Service
public class IotConsumerGroupServiceImpl implements IIotConsumerGroupService
{
    @Autowired
    private IotConsumerGroupMapper iotConsumerGroupMapper;

    /**
     * 查询消费组
     * 
     * @param id 消费组主键
     * @return 消费组
     */
    @Override
    public IotConsumerGroup selectIotConsumerGroupById(Long id)
    {
        return iotConsumerGroupMapper.selectIotConsumerGroupById(id);
    }

    /**
     * 查询消费组列表
     * 
     * @param iotConsumerGroup 消费组
     * @return 消费组
     */
    @Override
    public List<IotConsumerGroup> selectIotConsumerGroupList(IotConsumerGroup iotConsumerGroup)
    {
        return iotConsumerGroupMapper.selectIotConsumerGroupList(iotConsumerGroup);
    }

    /**
     * 新增消费组
     * 
     * @param iotConsumerGroup 消费组
     * @return 结果
     */
    @Override
    public int insertIotConsumerGroup(IotConsumerGroup iotConsumerGroup)
    {
        iotConsumerGroup.setCreateTime(DateUtils.getNowDate());
        return iotConsumerGroupMapper.insertIotConsumerGroup(iotConsumerGroup);
    }

    /**
     * 修改消费组
     * 
     * @param iotConsumerGroup 消费组
     * @return 结果
     */
    @Override
    public int updateIotConsumerGroup(IotConsumerGroup iotConsumerGroup)
    {
        iotConsumerGroup.setUpdateTime(DateUtils.getNowDate());
        return iotConsumerGroupMapper.updateIotConsumerGroup(iotConsumerGroup);
    }

    /**
     * 批量删除消费组
     * 
     * @param ids 需要删除的消费组主键
     * @return 结果
     */
    @Override
    public int deleteIotConsumerGroupByIds(Long[] ids)
    {
        return iotConsumerGroupMapper.deleteIotConsumerGroupByIds(ids);
    }

    /**
     * 删除消费组信息
     * 
     * @param id 消费组主键
     * @return 结果
     */
    @Override
    public int deleteIotConsumerGroupById(Long id)
    {
        return iotConsumerGroupMapper.deleteIotConsumerGroupById(id);
    }
}
