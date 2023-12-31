package com.zc.efounder.JEnterprise.service.scheduling;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneControl;
import com.zc.efounder.JEnterprise.domain.scheduling.SchedulingArea;

import java.util.List;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:38 2022/11/5
 * @Modified By:
 */
public interface SceneConfigService {
    /**
     * @param sceneControl
     * @Description: 获取场景区域信息
     * @auther: wanghongjie
     * @date: 15:46 2022/11/5
     * @param: [SchedulingArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult sceneConfigAreaListInfo(SchedulingArea sceneControl);

    /**
     * @Description: 添加区域
     * @auther: wanghongjie
     * @date: 17:43 2022/11/5
     * @param: [SchedulingArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult addSceneConfigArea(SchedulingArea schedulingArea);

    /**
     * @Description: 修改区域
     * @auther: wanghongjie
     * @date: 17:43 2022/11/5
     * @param: [SchedulingArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult updateSceneConfigArea(SchedulingArea schedulingArea);

    /**
     * @param ids
     * @Description: 删除区域
     * @auther: wanghongjie
     * @date: 17:44 2022/11/5
     * @param: [SchedulingArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult deleteSceneConfigArea(Long[] ids);

    /**
     * @param sceneControl
     * @Description: 获取场景列表
     * @auther: gaojikun
     * @param: sceneControl
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult getSceneControlList(SceneControl sceneControl);

    /**
     * @param sceneControl
     * @Description: 获取场景列表
     * @auther: gaojikun
     * @param: sceneControl
     * @return: List<SceneControl>
     */
    List<SceneControl> getSceneControlListReturnList(SceneControl sceneControl);


    /**
     * @param sceneControl
     * @Description: 根据id获取场景列表
     * @auther: gaojikun
     * @param: sceneControl
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult getsceneConfig(SceneControl sceneControl);

    /**
     * @Description: 添加场景信息
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    AjaxResult addSceneConfig(SceneControl sceneControl);

    /**
     * @param sceneControl
     * @Description: 修改场景信息
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult updateSceneConfig(SceneControl sceneControl);

    /**
     * @param ids
     * @Description: 删除场景信息
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    AjaxResult deleteSceneConfig(Long[] ids);

}
