package com.zc.efounder.JEnterprise.mapper.deviceSynchronization;


import com.zc.efounder.JEnterprise.domain.deviceSynchronization.AthenaBesTimeTaskSyncSb;

import java.util.List;

/**
 * 定时同步设备Mapper接口
 *
 * @author ruoyi
 * @date 2022-11-01
 */
public interface AthenaBesTimeTaskSyncSbMapper
{
    /**
     * 查询定时同步设备
     *
     * @param id 定时同步设备主键
     * @return 定时同步设备
     */
    public AthenaBesTimeTaskSyncSb selectAthenaBesTimeTaskSyncSbById(Long id);

    /**
     * 查询定时同步设备列表
     *
     * @param athenaBesTimeTaskSyncSb 定时同步设备
     * @return 定时同步设备集合
     */
    List<AthenaBesTimeTaskSyncSb> selectAthenaBesTimeTaskSyncSbList(AthenaBesTimeTaskSyncSb athenaBesTimeTaskSyncSb);

    /**
     * 新增定时同步设备
     *
     * @param athenaBesTimeTaskSyncSb 定时同步设备
     * @return 结果
     */
    int insertAthenaBesTimeTaskSyncSb(AthenaBesTimeTaskSyncSb athenaBesTimeTaskSyncSb);

    /**
     * 修改定时同步设备
     *
     * @param athenaBesTimeTaskSyncSb 定时同步设备
     * @return 结果
     */
    int updateAthenaBesTimeTaskSyncSb(AthenaBesTimeTaskSyncSb athenaBesTimeTaskSyncSb);

    /**
     * 删除定时同步设备
     *
     * @param id 定时同步设备主键
     * @return 结果
     */
    int deleteAthenaBesTimeTaskSyncSbById(Long id);

    /**
     * 批量删除定时同步设备
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAthenaBesTimeTaskSyncSbByIds(Long[] ids);

    /**
     * @description:根据定时任务获取点位
     * @author: sunshangeng
     * @date: 2022/11/2 17:30
     * @param:
     * @return:
     **/
    List<AthenaBesTimeTaskSyncSb> selectAllBySyncId(String syncId);


    /**
     * @description:根据id获取所有选中的节点
     * @author: sunshangeng
     * @date: 2022/11/3 14:58
     **/
    List<String> selectNodeIdBySyncId(String syncId);

    /**
     * 根据id获取所有选中的节点
     *
     * @param syncId 任务id
     * @return {@code List<AthenaBesTimeTaskSyncSb> }
     * @Author qindehua
     * @Date 2023/02/08
     **/
    List<AthenaBesTimeTaskSyncSb> selectNodeBySyncId(String syncId);


    /***
     * @description:根据任务id删除数据
     * @author: sunshangeng
     * @date: 2022/11/3 17:20
     * @param:
     * @return:
     **/
    Boolean deleteBySyncIdBoolean(String syncId);


    /**
     * 根据树id删除数据
     *
     * @param treeId 树id
     * @return {@code Boolean }
     * @Author qindehua
     * @Date 2023/02/08
     **/
    int deleteByTreeIdBoolean(String treeId);



}
