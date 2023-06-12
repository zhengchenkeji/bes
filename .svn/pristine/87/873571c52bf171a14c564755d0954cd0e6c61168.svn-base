package com.zc.iot.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.zc.iot.domain.IotConsumer;
import com.zc.iot.mapper.IotConsumerMapper;
import com.zc.iot.service.IIotConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消费者Service业务层处理
 * 
 * @author Athena-gongfanfei
 * @date 2021-10-27
 */
@Service
public class IotConsumerServiceImpl implements IIotConsumerService
{
    @Autowired
    private IotConsumerMapper iotConsumerMapper;

    /**
     * 查询消费者
     * 
     * @param id 消费者主键
     * @return 消费者
     */
    @Override
    public IotConsumer selectIotConsumerById(Long id)
    {
        return iotConsumerMapper.selectIotConsumerById(id);
    }

    /**
     * 查询消费者列表
     * 
     * @param iotConsumer 消费者
     * @return 消费者
     */
    @Override
    public List<IotConsumer> selectIotConsumerList(IotConsumer iotConsumer)
    {
        return iotConsumerMapper.selectIotConsumerList(iotConsumer);
    }

    /**
     * 新增消费者
     * 
     * @param iotConsumer 消费者
     * @return 结果
     */
    @Override
    public int insertIotConsumer(IotConsumer iotConsumer)
    {
        iotConsumer.setCreateTime(DateUtils.getNowDate());
        return iotConsumerMapper.insertIotConsumer(iotConsumer);
    }

    /**
     * 修改消费者
     * 
     * @param iotConsumer 消费者
     * @return 结果
     */
    @Override
    public int updateIotConsumer(IotConsumer iotConsumer)
    {
        iotConsumer.setUpdateTime(DateUtils.getNowDate());
        return iotConsumerMapper.updateIotConsumer(iotConsumer);
    }

    /**
     * 批量删除消费者
     * 
     * @param ids 需要删除的消费者主键
     * @return 结果
     */
    @Override
    public int deleteIotConsumerByIds(Long[] ids)
    {
        return iotConsumerMapper.deleteIotConsumerByIds(ids);
    }

    /**
     * 删除消费者信息
     * 
     * @param id 消费者主键
     * @return 结果
     */
    @Override
    public int deleteIotConsumerById(Long id)
    {
        return iotConsumerMapper.deleteIotConsumerById(id);
    }
}
