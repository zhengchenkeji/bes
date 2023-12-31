package com.zc.efounder.JEnterprise.service.sceneLink;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.job.TaskException;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.sceneLink.Scene;
import com.zc.efounder.JEnterprise.domain.sceneLink.SceneLog;
import org.quartz.SchedulerException;

import java.util.List;

public interface SceneLinkService {

    /**
     * @description:新增场景信息
     * @author: sunshangeng
     * @date: 2023/3/2 10:30
     * @param: [场景信息表]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult insertScene(Scene scene) throws TaskException, SchedulerException;

    /**
     * @description:获取场景详情
     * @author: sunshangeng
     * @date: 2023/3/2 15:22
     * @param: [id]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult getInfo(Long id);

    /**
     * @description:修改场景
     * @author: sunshangeng
     * @date: 2023/3/2 16:56
     * @param: [scene]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult edit(Scene scene) throws SchedulerException, TaskException;


    /**
     * @description:删除场景信息
     * @author: sunshangeng
     * @date: 2023/3/3 10:14
     * @param:
     * @return:
     **/
    AjaxResult del(Long sceneId) throws SchedulerException;

    /***
     * @description:查询所有场景
     * @author: sunshangeng
     * @date: 2023/3/3 14:40
     * @param: [scene]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.sceneLink.Scene>
     **/
    List<Scene> getSceneList(Scene scene);

    List<Scene> getSceneListDic(Scene scene);


    AjaxResult execute(Long id);

    AjaxResult changeStatus(Scene scene) throws TaskException, SchedulerException;

    AjaxResult getBesDeviceTree(DeviceTree deviceTree);

    List<SceneLog> getSceneLog(SceneLog sceneLog);



}
