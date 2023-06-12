package com.zc.efounder.JEnterprise.service.deviceTree.impl;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.zc.efounder.JEnterprise.domain.deviceTree.FunctionPoint;
import com.zc.efounder.JEnterprise.mapper.deviceTree.FunctionPointMapper;
import com.zc.efounder.JEnterprise.service.deviceTree.FunctionPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 功能点Service业务层处理
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
@Service
public class FunctionPointServiceImpl implements FunctionPointService
{
    @Autowired
    private FunctionPointMapper functionPointMapper;
    @Resource
    private RedisCache redisCache;

    /**
     * 查询功能点
     *
     * @param id 功能点主键
     * @return 功能点
     */
    @Override
    public FunctionPoint selectFunctionPointById(Long id)
    {
        return functionPointMapper.selectFunctionPointById(id);
    }

    /**
     * 查询功能点列表
     *
     * @param functionPoint 功能点
     * @return 功能点
     */
    @Override
    public List<FunctionPoint> selectFunctionPointList(FunctionPoint functionPoint)
    {
        return functionPointMapper.selectFunctionPointList(functionPoint);
    }

    /**
     * 新增功能点
     *
     * @param functionPoint 功能点
     * @return 结果
     */
    @Override
    public int insertFunctionPoint(FunctionPoint functionPoint)
    {
        functionPoint.setCreateTime(DateUtils.getNowDate());
        return functionPointMapper.insertFunctionPoint(functionPoint);
    }

    /**
     * 修改功能点
     *
     * @param functionPoint 功能点
     * @return 结果
     */
    @Override
    public int updateFunctionPoint(FunctionPoint functionPoint)
    {
        functionPoint.setUpdateTime(DateUtils.getNowDate());
        return functionPointMapper.updateFunctionPoint(functionPoint);
    }

    /**
     * 批量删除功能点
     *
     * @param ids 需要删除的功能点主键
     * @return 结果
     */
    @Override
    public int deleteFunctionPointByIds(Long[] ids)
    {
        return functionPointMapper.deleteFunctionPointByIds(ids);
    }

    /**
     * 删除功能点信息
     *
     * @param id 功能点主键
     * @return 结果
     */
    @Override
    public int deleteFunctionPointById(Long id)
    {
        return functionPointMapper.deleteFunctionPointById(id);
    }


}
