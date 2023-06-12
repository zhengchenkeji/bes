package com.zc.efounder.JEnterprise.mapper.sceneLink;

import java.util.List;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.zc.efounder.JEnterprise.domain.sceneLink.Scene;
import com.zc.efounder.JEnterprise.domain.sceneLink.SceneLog;

/**
 * 场景联动-场景Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-28
 */
public interface SceneMapper
{
    /**
     * 查询场景联动-场景
     *
     * @param id 场景联动-场景主键
     * @return 场景联动-场景
     */
    public Scene selectSceneById(Long id);


    /**
     * 查询场景联动-场景列表
     *
     * @param athenaBesScene 场景联动-场景
     * @return 场景联动-场景集合
     */
    List<Scene> selectSceneList(Scene athenaBesScene);

    /**
     * 新增场景联动-场景
     *
     * @param athenaBesScene 场景联动-场景
     * @return 结果
     */
    Boolean insertScene(Scene athenaBesScene);

    /**
     * 修改场景联动-场景
     *
     * @param athenaBesScene 场景联动-场景
     * @return 结果
     */
    Boolean updateScene(Scene athenaBesScene);

    /**
     * 删除场景联动-场景
     *
     * @param id 场景联动-场景主键
     * @return 结果
     */
    Boolean deleteSceneById(Long id);

    /**
     * 批量删除场景联动-场景
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteSceneByIds(Long[] ids);



    /**
     * @description:根据ID查询场景联动日志
     * @author: sunshangeng
     * @date: 2023/5/8 9:27
     * @param: [id]
     * @return: com.zc.efounder.JEnterprise.domain.sceneLink.SceneLog
     **/
    SceneLog selectSceneLogById(Long id);

    /**
     * @description:查询场景联动日志集合
     * @author: sunshangeng
     * @date: 2023/5/8 9:27
     * @param: [sceneLog]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.sceneLink.Scene>
     **/
    List<SceneLog> selectSceneLogList(SceneLog sceneLog);

    /**
     * @description:添加场景联动日志
     * @author: sunshangeng
     * @date: 2023/5/8 9:28
     * @param: [sceneLog]
     * @return: java.lang.Boolean
     **/
    Boolean insertSceneLog(SceneLog sceneLog);

    /**
     * @description:修改场景联动日志
     * @author: sunshangeng
     * @date: 2023/5/8 9:28
     * @param: [log]
     * @return: java.lang.Boolean
     **/
    Boolean updateSceneLog(SceneLog log);
    /**
     * @description:删除场景联动日志
     * @author: sunshangeng
     * @date: 2023/5/8 9:28
     * @param: [id]
     * @return: java.lang.Boolean
     **/
    Boolean deleteSceneLogById(Long  id );

}
