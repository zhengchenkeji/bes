package com.zc.efounder.JEnterprise.service.deviceTreeNode.impl;

import java.io.IOException;
import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.mapper.deviceTreeNode.AthenaDeviceNodeMapper;
import com.zc.efounder.JEnterprise.service.deviceTreeNode.AthenaDeviceNodeService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zc.efounder.JEnterprise.domain.deviceTreeNode.AthenaDeviceNode;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.View;


/**
 * 树节点定义Service业务层处理
 *
 * @author qindehua
 * @date 2022-09-06
 */
@Service
public class AthenaDeviceNodeServiceImpl implements AthenaDeviceNodeService
{
    @Autowired
    private AthenaDeviceNodeMapper athenaDeviceNodeMapper;


    /**
     * 查询树节点定义
     *
     * @param deviceNodeId 树节点定义主键
     * @return 树节点定义
     */
    @Override
    public AthenaDeviceNode selectAthenaDeviceNodeByDeviceNodeId(Long deviceNodeId)
    {
        return athenaDeviceNodeMapper.selectAthenaDeviceNodeByDeviceNodeId(deviceNodeId);
    }

    /**
     * 查询树节点定义列表
     *
     * @param athenaDeviceNode 树节点定义
     * @return 树节点定义
     */
    @Override
    public List<AthenaDeviceNode> selectAthenaDeviceNodeList(AthenaDeviceNode athenaDeviceNode)
    {

        return athenaDeviceNodeMapper.selectAthenaDeviceNodeList(athenaDeviceNode);
    }

    /**
     * 新增树节点定义
     *
     * @param athenaDeviceNode 树节点定义
     * @return 结果
     */
    @Override
    public AjaxResult insertAthenaDeviceNode(AthenaDeviceNode athenaDeviceNode)
    {
        if (athenaDeviceNodeMapper.selectAthenaDeviceNodeCount(athenaDeviceNode)>=1){
            return AjaxResult.error("节点类型或节点名称重复！");
        }
        athenaDeviceNode.setCreateTime(DateUtils.getNowDate());
        return AjaxResult.success("新增成功！",athenaDeviceNodeMapper.insertAthenaDeviceNode(athenaDeviceNode));
    }


    /**
     * 修改树节点定义
     *
     * @param athenaDeviceNode 树节点定义
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateAthenaDeviceNode(AthenaDeviceNode athenaDeviceNode)
    {
        if (athenaDeviceNode.getDeviceNodeId()==null){
            return AjaxResult.error("树节点ID不允许为空！");
        }
        if (athenaDeviceNodeMapper.selectAthenaDeviceNodeCount(athenaDeviceNode)>=1){
            return AjaxResult.error("节点类型或节点名称重复！");
        }
        //先修改数据
        athenaDeviceNode.setUpdateTime(DateUtils.getNowDate());
        //取出之前旧数据
        AthenaDeviceNode data=athenaDeviceNodeMapper.selectAthenaDeviceNodeByDeviceNodeId(athenaDeviceNode.getDeviceNodeId());
        //先更新数据
        athenaDeviceNodeMapper.updateAthenaDeviceNode(athenaDeviceNode);
        //再修改功能点名称
        if (!data.getDeviceNodeName().equals(athenaDeviceNode.getDeviceNodeName())){
            athenaDeviceNodeMapper.updateStringByFunName(data.getDeviceNodeName(),athenaDeviceNode.getDeviceNodeName());
        }
        return AjaxResult.success("修改成功！",true);
    }

    /**
     * 批量删除树节点定义
     *
     * @param deviceNodeIds 需要删除的树节点定义主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteAthenaDeviceNodeByDeviceNodeIds(Long[] deviceNodeIds)
    {
        if (StringUtils.isEmpty(deviceNodeIds)){
            return AjaxResult.error("树节点ID不允许为空！");
        }
        for(AthenaDeviceNode data:athenaDeviceNodeMapper.selectAthenaDeviceNodeDeleteList(deviceNodeIds)){
            for(AthenaDeviceNode item:athenaDeviceNodeMapper.selectAthenaDeviceNodeDelete(deviceNodeIds)){
                if(item.getDeviceNodeFunType()==null){
                    continue;
                }
                if(item.getDeviceNodeFunType().contains(data.getDeviceNodeCode())){
                    return AjaxResult.error("该节点类型已使用，无法删除！");
                }
            }
        }
        return AjaxResult.success("删除成功！",athenaDeviceNodeMapper.deleteAthenaDeviceNodeByDeviceNodeIds(deviceNodeIds));
    }

    /**
     * 删除树节点定义信息
     *
     * @param deviceNodeId 树节点定义主键
     * @return 结果
     */
    @Override
    public int deleteAthenaDeviceNodeByDeviceNodeId(Long deviceNodeId)
    {
        return athenaDeviceNodeMapper.deleteAthenaDeviceNodeByDeviceNodeId(deviceNodeId);
    }
}
