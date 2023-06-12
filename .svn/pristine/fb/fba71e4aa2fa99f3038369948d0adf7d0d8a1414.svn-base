package com.zc.iot.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.zc.iot.domain.IotServiceSub;
import com.zc.iot.mapper.IotServiceSubMapper;
import com.zc.iot.service.IIotServiceSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务订阅Service业务层处理
 * 
 * @author Athena-gongfanfei
 * @date 2021-10-27
 */
@Service
public class IotServiceSubServiceImpl implements IIotServiceSubService
{
    @Autowired
    private IotServiceSubMapper iotServiceSubMapper;

    /**
     * 查询服务订阅
     * 
     * @param id 服务订阅主键
     * @return 服务订阅
     */
    @Override
    public IotServiceSub selectIotServiceSubById(Long id)
    {
        return iotServiceSubMapper.selectIotServiceSubById(id);
    }

    /**
     * 查询服务订阅列表
     * 
     * @param iotServiceSub 服务订阅
     * @return 服务订阅
     */
    @Override
    public List<IotServiceSub> selectIotServiceSubList(IotServiceSub iotServiceSub)
    {
        return iotServiceSubMapper.selectIotServiceSubList(iotServiceSub);
    }

    /**
     * 新增服务订阅
     * 
     * @param iotServiceSub 服务订阅
     * @return 结果
     */
    @Override
    public int insertIotServiceSub(IotServiceSub iotServiceSub)
    {
        iotServiceSub.setCreateTime(DateUtils.getNowDate());
        return iotServiceSubMapper.insertIotServiceSub(iotServiceSub);
    }

    /**
     * 修改服务订阅
     * 
     * @param iotServiceSub 服务订阅
     * @return 结果
     */
    @Override
    public int updateIotServiceSub(IotServiceSub iotServiceSub)
    {
        iotServiceSub.setUpdateTime(DateUtils.getNowDate());
        return iotServiceSubMapper.updateIotServiceSub(iotServiceSub);
    }

    /**
     * 批量删除服务订阅
     * 
     * @param ids 需要删除的服务订阅主键
     * @return 结果
     */
    @Override
    public int deleteIotServiceSubByIds(Long[] ids)
    {
        return iotServiceSubMapper.deleteIotServiceSubByIds(ids);
    }

    /**
     * 删除服务订阅信息
     * 
     * @param id 服务订阅主键
     * @return 结果
     */
    @Override
    public int deleteIotServiceSubById(Long id)
    {
        return iotServiceSubMapper.deleteIotServiceSubById(id);
    }
}
