package com.zc.efounder.JEnterprise.service.energyInfo;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.vo.MeterDataVo;

/**
 * 支路拓扑配置Service接口
 * @author qindehua
 */
public interface AthenaBranchConfigService
{

    /**
     * 包含电表 保存操作
     *
     * @param meterDataVo 电表数据
     * @return 是否成功
     */
    public boolean saveAthenaBranchConfigMeter(MeterDataVo meterDataVo);

    /**
     * 查询建筑列表
     *
     * @param parkCode 园区code
     * @return map值
     */
    List<Map>  selectBuildingList( String parkCode);

    /**
     * 查询支路拓扑配置
     *
     * @param branchId 支路拓扑配置主键
     * @return 支路拓扑配置
     */
    public AthenaBranchConfig selectAthenaBranchConfigByBranchId(Long branchId);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param list 支路拓扑配置列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildTreeSelect(List<AthenaBranchConfig> list);


    /**
     * 构建前端所需要树结构
     *
     * @param list 支路拓扑配置列表
     * @return 树结构列表
     */
    public List<AthenaBranchConfig> buildTree(List<AthenaBranchConfig> list);


    /**
     * 查询支路拓扑配置列表
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @return 支路拓扑配置集合
     */
    List<AthenaBranchConfig> selectAthenaBranchConfigList(AthenaBranchConfig athenaBranchConfig);

    /**
     * 查询支路拓扑配置列表 查询 自身及子数据
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @param code               查询标识
     * @return 支路拓扑配置集合
     */
    List<AthenaBranchConfig> selectAthenaBranchConfigListSun(AthenaBranchConfig athenaBranchConfig,String code);

    /**
     * 新增支路拓扑配置
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @return 结果
     */
    AjaxResult insertAthenaBranchConfig(AthenaBranchConfig athenaBranchConfig);

    /**
     * 修改支路拓扑配置
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @return 结果
     */
    AjaxResult updateAthenaBranchConfig(AthenaBranchConfig athenaBranchConfig);

    /**
     * 批量删除支路拓扑配置
     *
     * @param branchIds 需要删除的支路拓扑配置主键集合
     * @return 结果
     */
    AjaxResult deleteAthenaBranchConfigByBranchIds(Long[] branchIds);

    /**
     * @description:获取所有支路数据
     * @author: sunshangeng
     * @date: 2022/11/14 15:47
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult SelectBranchTreeList();


    /**
     * 删除时 查看是否关联分户及分项
     *
     * @param branchIds 支路id
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/12/16
     **/
    AjaxResult getMessage(Long[] branchIds);
}
