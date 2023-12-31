package com.zc.efounder.JEnterprise.mapper.scheduling;

import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneModelControl;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneModelPointControl;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author: gaojikun
 * @Description:
 */
@Mapper
public interface SceneModelMapper {

    /**
     * @param sceneModelControl
     * @Description: 获取场景模式列表
     * @auther: gaojikun
     * @return: List<SceneModelControl>
     */
    List<SceneModelControl> getSceneModelList(SceneModelControl sceneModelControl);

    /**
     * @param sceneModelControl
     * @Description: 场景模式查重
     * @auther: gaojikun
     * @return: SceneModelControl
     */
    SceneModelControl getsceneModelCheck(SceneModelControl sceneModelControl);

    /**
     * @param sceneModelControl
     * @Description: 新增场景模式
     * @auther: gaojikun
     * @return: boolean
     */
    boolean addSceneModel(SceneModelControl sceneModelControl);

    /**
     * @param sceneModelControl
     * @Description: 修改场景模式
     * @auther: gaojikun
     * @return: boolean
     */
    boolean updateSceneModel(SceneModelControl sceneModelControl);

    /**
     * @param point
     * @Description: 修改场景模式点位
     * @auther: gaojikun
     * @return: boolean
     */
    boolean updateSceneModelPoint(Point point);

    /**
     * @param id
     * @Description: 删除场景模式
     * @auther: gaojikun
     * @return: int
     */
    int deleteSceneModel(Long id);

    /**
     * @param sceneModelControl
     * @Description: 获取场景模式信息
     * @auther: gaojikun
     * @return: SceneModelControl
     */
    SceneModelControl getSceneModel(SceneModelControl sceneModelControl);

    /**
     * @Description: 获取场景模式点位列表
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: List<SceneModelPointControl>
     */
    List<SceneModelPointControl> getSceneModelPoint(SceneModelPointControl sceneModelPointControl);

    /**
     * @Description: 场景模式数据对比
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: Map<String,String>
     */
    Map<String,Object> getSceneModelContrast(SceneModelPointControl sceneModelPointControl);

    /**
     * @Description: 添加场景模式点位
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    boolean addSceneModelPoint(SceneModelPointControl sceneModelPointControl);

    /**
     * @Description: 删除场景模式点位
     * @auther: gaojikun
     * @param: id
     * @return: int
     */
    int delSceneModelPoint(Integer id);

    /**
     * @Description: 根据模式查询场景下所有模式
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: List<Map<String,Object>>
     */
    List<Map<String,Object>> selectAllModelByModel(Long id);

    /**
     * @Description: 根据模式Id查询场景下所有模式
     * @auther: gaojikun
     * @param: sceneModelControl
     * @return: List<SceneModelControl>
     */
    List<SceneModelControl> selectModelCountBySecne(SceneModelControl sceneModelControl);
}
