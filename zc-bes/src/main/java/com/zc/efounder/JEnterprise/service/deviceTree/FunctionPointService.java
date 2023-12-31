package com.zc.efounder.JEnterprise.service.deviceTree;

import com.zc.efounder.JEnterprise.domain.deviceTree.FunctionPoint;

import java.util.List;

/**
 * 功能点Service接口
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
public interface FunctionPointService
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
     * 批量删除功能点
     *
     * @param ids 需要删除的功能点主键集合
     * @return 结果
     */
    int deleteFunctionPointByIds(Long[] ids);

    /**
     * 删除功能点信息
     *
     * @param id 功能点主键
     * @return 结果
     */
    int deleteFunctionPointById(Long id);

}
