package com.zc.efounder.JEnterprise.mapper.scheduling;

import com.zc.efounder.JEnterprise.domain.scheduling.PlanCollection;
import com.zc.efounder.JEnterprise.domain.scheduling.PlanConfig;
import com.zc.efounder.JEnterprise.domain.scheduling.PlanController;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 计划编排Mapper接口
 * 
 * @author gaojikun
 * @date 2022-11-10
 */
@Mapper
public interface PlanConfigMapper
{
    /**
     * @Description: 左侧计划树
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: List<PlanConfig>
     */
    List<PlanConfig> getAllPlanConfigListInfo(PlanConfig planConfig);

    /**
     * @Description: 查询计划信息
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: PlanConfig
     */
    PlanConfig getPlanConfigInfo(PlanConfig planConfig);

    /**
     * @Description: 计划查重
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: PlanConfig
     */
    PlanConfig getPlanConfigCheck(PlanConfig planConfig);

    /**
     * @Description: 新增计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: boolean
     */
    boolean addPlanConfig(PlanConfig planConfig);

    /**
     * @Description: 根据id修改计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: boolean
     */
    boolean editPlanConfig(PlanConfig planConfig);

    /**
     * @Description: 根据名称修改计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: boolean
     */
    boolean editPlanConfigId(PlanConfig planConfig);

    /**
     * @Description: 删除计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: int
     */
    int delPlanConfig(Long id);

    /**
     * 查询控制计划列表
     * @param planController
     * @return List<PlanController>
     */
    List<PlanController> selectPlanControllerList(PlanController planController);

    /**
     * 查询控制计划列表
     * @param planController
     * @return List<PlanController>
     */
    PlanController getPlanControllerCheck(PlanController planController);

    /**
     * 查询控制计划列表
     * @param planController
     * @return List<PlanController>
     */
    List<PlanController> selectAllPlanControllerList(PlanController planController);

    /**
     * 查询采集计划列表
     * @param planCollection
     * @return List<PlanCollection>
     */
    List<PlanCollection> selectPlanCollectionList(PlanCollection planCollection);



    /**
     * 查询控制及计划信息
     * @param planController
     * @return PlanController
     */
    PlanController selectPlanControllerById(PlanController planController);

    /**
     * 新增控制计划
     * @param planController 控制计划
     * @return boolean
     */
    boolean insertPlanController(PlanController planController);

    /**
     * 修改控制计划
     * @param planController 控制计划
     * @return boolean
     */
    boolean updatePlanController(PlanController planController);

    /**
     * 删除控制计划
     * @param id 计划编排主键
     * @return int
     */
    int deletePlanConfigById(Long id);

    /**
     * 批量删除控制计划
     * @param ids 需要删除的数据主键集合
     * @return int
     */
    int deletePlanConfigByIds(Long[] ids);

    /**
     * 控制计划数据对比上位机信息
     * @param planController
     * @return Map<String,Object>
     */
    Map<String,Object> planPointContrast(PlanController planController);

    /**
     * 控制计划采集计划查询最大id
     * @param
     * @return Long
     */
    Long selectMaxId();

}
