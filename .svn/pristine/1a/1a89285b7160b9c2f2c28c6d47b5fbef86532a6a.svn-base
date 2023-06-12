package com.zc.iot.mapper;

import java.util.List;
import com.zc.iot.domain.IotConsumer;

/**
 * 消费者Mapper接口
 * 
 * @author Athena-gongfanfei
 * @date 2021-10-27
 */
public interface IotConsumerMapper
{
    /**
     * 查询消费者
     * 
     * @param id 消费者主键
     * @return 消费者
     */
    public IotConsumer selectIotConsumerById(Long id);

    /**
     * 查询消费者列表
     * 
     * @param iotConsumer 消费者
     * @return 消费者集合
     */
    public List<IotConsumer> selectIotConsumerList(IotConsumer iotConsumer);

    /**
     * 新增消费者
     * 
     * @param iotConsumer 消费者
     * @return 结果
     */
    public int insertIotConsumer(IotConsumer iotConsumer);

    /**
     * 修改消费者
     * 
     * @param iotConsumer 消费者
     * @return 结果
     */
    public int updateIotConsumer(IotConsumer iotConsumer);

    /**
     * 删除消费者
     * 
     * @param id 消费者主键
     * @return 结果
     */
    public int deleteIotConsumerById(Long id);

    /**
     * 批量删除消费者
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIotConsumerByIds(Long[] ids);
}
