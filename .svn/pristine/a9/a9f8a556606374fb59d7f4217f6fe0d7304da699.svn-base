package com.zc.efounder.JEnterprise.mapper.deviceTree;

import java.util.List;
import com.zc.efounder.JEnterprise.domain.deviceTree.FunctionPoint;

/**
 * 功能点Mapper接口
 * 
 * @author sunshangeng
 * @date 2022-09-14
 */
public interface FunctionPointMapper 
{
    /**
     * 查询功能点
     * 
     * @param id 功能点主键
     * @return 功能点
     */
    public FunctionPoint selectFunctionPointById(Long id);

    /**
     * 查询功能点列表
     * 
     * @param functionPoint 功能点
     * @return 功能点集合
     */
    List<FunctionPoint> selectFunctionPointList(FunctionPoint functionPoint);

    /**
     * 新增功能点
     * 
     * @param functionPoint 功能点
     * @return 结果
     */
    int insertFunctionPoint(FunctionPoint functionPoint);

    /**
     * 修改功能点
     * 
     * @param functionPoint 功能点
     * @return 结果
     */
    int updateFunctionPoint(FunctionPoint functionPoint);

    /**
     * 删除功能点
     * 
     * @param id 功能点主键
     * @return 结果
     */
    int deleteFunctionPointById(Long id);

    /**
     * 批量删除功能点
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteFunctionPointByIds(Long[] ids);
}
