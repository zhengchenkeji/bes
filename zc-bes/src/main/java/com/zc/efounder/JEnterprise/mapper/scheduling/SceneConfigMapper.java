package com.zc.efounder.JEnterprise.mapper.scheduling;

import com.zc.efounder.JEnterprise.domain.scheduling.SceneControl;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneModelControl;
import com.zc.efounder.JEnterprise.domain.scheduling.SchedulingArea;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:40 2022/11/5
 * @Modified By:
 */
@Mapper
public interface SceneConfigMapper {

    /**
     *
     * @Description: 获取场景区域信息
     *
     * @auther: wanghongjie
     * @date: 15:51 2022/11/5
     * @param: [sceneControl]
     * @return: void
     *
     * @param sceneControl
     */
    List<SceneControl> sceneConfigAreaListInfo(SchedulingArea sceneControl);

    /**
     *
     * @Description: 获取所有区域ID
     * @auther: gaojikun
     * @return: List<Long>
     */
    List<Long> sceneConfigAreaIdList(Long id);

    /**
     *
     * @Description: 添加区域
     *
     * @auther: wanghongjie
     * @date: 17:49 2022/11/5
     * @param: [schedulingArea]
     * @return: com.ruoyi.efounder.JEnterprise.domain.SchedulingArea
     *
     */
    boolean addSceneConfigArea(SchedulingArea schedulingArea);

    /**
     *
     * @Description: 删除区域
     *
     * @auther: wanghongjie
     * @date: 18:17 2022/11/5
     * @param: [id]
     * @return: int
     *
     */
    int deleteSceneConfigArea(Long id);

    /**
     *
     * @Description: 修改区域
     *
     * @auther: wanghongjie
     * @date: 8:48 2022/11/7
     * @param: [schedulingArea]
     * @return: int
     *
     */
    boolean updateSceneConfigArea(SchedulingArea schedulingArea);

    /**
     *
     * @Description: 根据区域id获取所有的场景
     *
     * @auther: gaojikun
     * @date: 8:52 2022/11/7
     * @param: [id]
     * @return: java.util.List<com.ruoyi.efounder.JEnterprise.domain.SceneControl>
     *
     */
    List<SceneControl> getSceneControlList(SceneControl sceneControl);

    /**
     *
     * @Description: 根据id获取场景信息
     *
     * @auther: gaojikun
     * @date: 8:52 2022/11/7
     * @param: [id]
     * @return: java.util.List<com.ruoyi.efounder.JEnterprise.domain.SceneControl>
     *
     */
    SceneControl getsceneConfig(SceneControl sceneControl);

    /**
     *
     * @Description: 根据id获取场景信息
     *
     * @auther: gaojikun
     * @date: 8:52 2022/11/7
     * @param: [id]
     * @return: java.util.List<com.ruoyi.efounder.JEnterprise.domain.SceneControl>
     *
     */
    SceneControl getsceneConfigCheck(SceneControl sceneControl);

    /**
     *
     * @Description: 根据场景id获取所有的模式
     *
     * @auther: wanghongjie
     * @date: 9:23 2022/11/7
     * @param: [id]
     * @return: java.util.List<com.ruoyi.efounder.JEnterprise.domain.SceneModelControl>
     *
     */
    List<SceneModelControl> getSceneModelControlList(Long id);


    /**
     *
     * @Description: 添加场景
     *
     * @auther: gaojikun
     * @param: [schedulingArea]
     * @return: com.ruoyi.efounder.JEnterprise.domain.SchedulingArea
     *
     */
    boolean addSceneConfig(SceneControl sceneControl);

    /**
     *
     * @Description: 删除场景
     *
     * @auther: gaojikun
     * @param: [id]
     * @return: int
     *
     */
    int deleteSceneConfig(Long id);

    /**
     *
     * @Description: 修改场景
     *
     * @auther: gaojikun
     * @param: [schedulingArea]
     * @return: int
     *
     */
    boolean updateSceneConfig(SceneControl sceneControl);
}
