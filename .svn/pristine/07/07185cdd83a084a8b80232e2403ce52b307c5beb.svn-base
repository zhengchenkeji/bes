package com.zc.efounder.JEnterprise.mapper.sceneLink;

import java.util.List;
import com.zc.efounder.JEnterprise.domain.sceneLink.SceneTrigger;
import org.apache.ibatis.annotations.Param;

/**
 * 场景联动-场景触发器Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-28
 */
public interface SceneTriggerMapper
{
    /**
     * 查询场景联动-场景触发器
     *
     * @param id 场景联动-场景触发器主键
     * @return 场景联动-场景触发器
     */
    public SceneTrigger selectSceneTriggerById(Long id);

    /**
     * 查询场景联动-场景触发器列表
     *
     * @param SceneActuator 场景联动-场景触发器
     * @return 场景联动-场景触发器集合
     */
    List<SceneTrigger> selectSceneTriggerList(SceneTrigger SceneActuator);

    /**
     * 新增场景联动-场景触发器
     *
     * @param SceneActuator 场景联动-场景触发器
     * @return 结果
     */
    Boolean insertSceneTrigger(SceneTrigger SceneActuator);

    /**
     * 修改场景联动-场景触发器
     *
     * @param SceneActuator 场景联动-场景触发器
     * @return 结果
     */
    Boolean updateSceneTrigger(SceneTrigger SceneActuator);

    /**
     * 删除场景联动-场景触发器
     *
     * @param id 场景联动-场景触发器主键
     * @return 结果
     */
    int deleteSceneTriggerById(Long id);

    /**
     * 批量删除场景联动-场景触发器
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteSceneTriggerByIds(Long[] ids);

    /**
     * @description:获取所有触发器
     * @author: sunshangeng
     * @date: 2023/3/1 15:33
     * @param: [SceneActuator]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.sceneLink.SceneTrigger>
     **/
    List<SceneTrigger> selectSceneTriggerAll();

    /***
     * @description:根据场景id删除当前场景下的所有触发器
     * @author: sunshangeng
     * @date: 2023/3/3 8:57
     * @param: [场景id]
     * @return: boolean
     **/
    boolean deleteBySceneIdBoolean(@Param("sceneId") Long sceneId);


}
