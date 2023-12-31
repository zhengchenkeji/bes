package com.zc.efounder.JEnterprise.service.deviceTreeNode;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.deviceTreeNode.AthenaDeviceNode;

/**
 * 树节点定义Service接口
 *
 * @author qindehua
 * @date 2022-09-06
 */
public interface AthenaDeviceNodeService {
    /**
     * 查询树节点定义
     *
     * @param deviceNodeId 树节点定义主键
     * @return 树节点定义
     */
    public AthenaDeviceNode selectAthenaDeviceNodeByDeviceNodeId(Long deviceNodeId);

    /**
     * 查询树节点定义列表
     *
     * @param athenaDeviceNode 树节点定义
     * @return 树节点定义集合
     */
    List<AthenaDeviceNode> selectAthenaDeviceNodeList(AthenaDeviceNode athenaDeviceNode);

    /**
     * 新增树节点定义
     *
     * @param athenaDeviceNode 树节点定义
     * @return 结果
     */
    AjaxResult insertAthenaDeviceNode(AthenaDeviceNode athenaDeviceNode);

    /**
     * 修改树节点定义
     *
     * @param athenaDeviceNode 树节点定义
     * @return 结果
     */
    AjaxResult updateAthenaDeviceNode(AthenaDeviceNode athenaDeviceNode);

    /**
     * 批量删除树节点定义
     *
     * @param deviceNodeIds 需要删除的树节点定义主键集合
     * @return 结果
     */
    AjaxResult deleteAthenaDeviceNodeByDeviceNodeIds(Long[] deviceNodeIds);

    /**
     * 删除树节点定义信息
     *
     * @param deviceNodeId 树节点定义主键
     * @return 结果
     */
    int deleteAthenaDeviceNodeByDeviceNodeId(Long deviceNodeId);
}
