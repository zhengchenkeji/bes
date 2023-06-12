package com.zc.efounder.JEnterprise.mapper.sceneLink;

import com.zc.efounder.JEnterprise.domain.sceneLink.SceneActuator;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 场景联动-场景执行器Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-28
 */
public interface SceneActuatorMapper
{
    /**
     * 查询场景联动-场景执行器
     *
     * @param id 场景联动-场景执行器主键
     * @return 场景联动-场景执行器
     */
    public SceneActuator selectSceneActuatorById(Long id);

    /**
     * 查询场景联动-场景执行器列表
     *
     * @param SceneActuator 场景联动-场景执行器
     * @return 场景联动-场景执行器集合
     */
    List<SceneActuator> selectSceneActuatorList(SceneActuator SceneActuator);

    /**
     * 新增场景联动-场景执行器
     *
     * @param SceneActuator 场景联动-场景执行器
     * @return 结果
     */
    boolean insertSceneActuator(SceneActuator SceneActuator);

    /**
     * 修改场景联动-场景执行器
     *
     * @param SceneActuator 场景联动-场景执行器
     * @return 结果
     */
    Boolean updateSceneActuator(SceneActuator SceneActuator);

    /**
     * 删除场景联动-场景执行器
     *
     * @param id 场景联动-场景执行器主键
     * @return 结果
     */
    int deleteSceneActuatorById(Long id);

    /**
     * 批量删除场景联动-场景执行器
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteSceneActuatorByIds(Long[] ids);

    /**
     * @description:根据场景id删除数据
     * @author: sunshangeng
     * @date: 2023/3/3 10:02
     * @param: [sceneId]
     * @return: java.lang.Boolean
     **/
    Boolean deleteBySceneIdBoolean(@Param("sceneId") Long sceneId);
}
