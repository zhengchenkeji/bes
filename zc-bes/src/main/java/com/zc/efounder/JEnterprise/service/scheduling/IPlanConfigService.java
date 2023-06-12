package com.zc.efounder.JEnterprise.service.scheduling;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.scheduling.PlanCollection;
import com.zc.efounder.JEnterprise.domain.scheduling.PlanConfig;
import com.zc.efounder.JEnterprise.domain.scheduling.PlanController;

import java.util.List;

/**
 * 计划编排Service接口
 * 
 * @author gaojikun
 * @date 2022-11-10
 */
public interface IPlanConfigService
{

    /**
     * @Description: 左侧计划树
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    AjaxResult getAllPlanConfigListInfo(PlanConfig planConfig);

    /**
     * @Description: 查询计划信息
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    AjaxResult getPlanConfigInfo(PlanConfig planConfig);

    /**
     * @Description: 新增计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    AjaxResult addPlanConfig(PlanConfig planConfig);

    /**
     * @Description: 修改计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    AjaxResult editPlanConfig(PlanConfig planConfig);

    /**
     * @Description: 删除计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    AjaxResult delPlanConfig(Long[] ids);

    /**
     * 查询控制/采集计划列表
     * 
     * @param planConfig 计划编排
     * @return 计划编排集合
     */
    List<PlanController> selectPlanControlList(PlanController planConfig);

    List<PlanCollection> selectPlanCollectList(PlanCollection planCollection);

    /**
     * 查询控制计划详情
     *
     * @param planController 查询控制计划详情主键
     * @return 控制计划详情
     */
    AjaxResult selectPlanControllerById(PlanController planController);

    /**
     * 新增控制计划
     * 
     * @param planController 控制计划
     * @return 结果
     */
    AjaxResult insertPlanController(PlanController planController);

    /**
     * 修改控制计划
     * 
     * @param planConfig 控制计划
     * @return 结果
     */
    AjaxResult updatePlanController(PlanController planConfig);

    /**
     * 批量删除控制计划
     * 
     * @param ids 需要删除的控制计划主键集合
     * @return 结果
     */
    AjaxResult deletePlanControllerByIds(Long[] ids);

    /**
     * 删除控制/采集计划信息
     * 
     * @param id 控制/采集计划主键
     * @return 结果
     */
    AjaxResult deletePlanConfigById(Long id);

    /**
     * @Description: 控制计划-模式点位同步
     * @auther: gaojikun
     * @param: planController
     * @return: AjaxResult
     */
    AjaxResult modelPointSync(PlanController planController);

    /**
     * @Description: 控制计划-计划数据对比
     * @auther: gaojikun
     * @param: planController
     * @return: AjaxResult
     */
    AjaxResult planPointContrast(PlanController planController);
}
