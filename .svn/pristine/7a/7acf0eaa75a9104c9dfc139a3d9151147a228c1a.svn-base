package com.zc.iot.mapper;

import java.util.List;
import com.zc.iot.domain.IotGroup;

/**
 * 分组Mapper接口
 * 
 * @author Athena-xiepufeng
 * @date 2021-11-16
 */
public interface IotGroupMapper 
{
    /**
     * 查询分组
     * 
     * @param id 分组主键
     * @return 分组
     */
    public IotGroup selectIotGroupById(Long id);

    /**
     * 查询分组列表
     * 
     * @param iotGroup 分组
     * @return 分组集合
     */
    public List<IotGroup> selectIotGroupList(IotGroup iotGroup);

    /**
     * 新增分组
     * 
     * @param iotGroup 分组
     * @return 结果
     */
    public int insertIotGroup(IotGroup iotGroup);

    /**
     * 修改分组
     * 
     * @param iotGroup 分组
     * @return 结果
     */
    public int updateIotGroup(IotGroup iotGroup);

    /**
     * 删除分组
     * 
     * @param id 分组主键
     * @return 结果
     */
    public int deleteIotGroupById(Long id);

    /**
     * 批量删除分组
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIotGroupByIds(Long[] ids);
}
