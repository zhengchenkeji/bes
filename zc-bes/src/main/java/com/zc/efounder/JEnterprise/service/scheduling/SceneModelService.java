package com.zc.efounder.JEnterprise.service.scheduling;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneModelControl;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneModelPointControl;

import java.util.List;

/**
 * @Author:gaojikun
 * @Date:2022-11-08 11:12
 * @Description:
 */
public interface SceneModelService {

    /**
     * @param sceneModelControl
     * @Description: 获取场景模式列表
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult getSceneModelList(SceneModelControl sceneModelControl);

    /**
     * @param sceneModelControl
     * @Description: 获取场景模式列表
     * @auther: gaojikun
     * @return: List<SceneModelControl>
     */
    List<SceneModelControl> getSceneModelListReturnList(SceneModelControl sceneModelControl);

    /**
     * @param sceneModelControl
     * @Description: 获取场景模式信息
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult getSceneModel(SceneModelControl sceneModelControl);

    /**
     * @param sceneModelControl
     * @Description: 新增场景模式
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult addSceneModel(SceneModelControl sceneModelControl);

    /**
     * @param sceneModelControl
     * @Description: 修改场景模式
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult updateSceneModel(SceneModelControl sceneModelControl);

    /**
     * @param ids
     * @Description: 删除场景模式
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult deleteSceneModel(Long[] ids);

    /**
     * @Description: 获取场景模式点位列表
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    AjaxResult getSceneModelPoint(SceneModelPointControl sceneModelPointControl);

    /**
     * @Description: 添加场景模式点位
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    AjaxResult addSceneModelPoint(SceneModelPointControl sceneModelPointControl);

    /**
     * @Description: 场景模式同步
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    AjaxResult modelPointSync(SceneModelControl sceneModelControl);

    /**
     * @Description: 模式数据对比
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    AjaxResult modelPointContrast(SceneModelControl sceneModelControl);
}
