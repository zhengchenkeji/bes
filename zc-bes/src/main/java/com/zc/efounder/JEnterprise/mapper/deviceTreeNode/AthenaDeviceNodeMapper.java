package com.zc.efounder.JEnterprise.mapper.deviceTreeNode;

import java.util.List;
import com.zc.efounder.JEnterprise.domain.deviceTreeNode.AthenaDeviceNode;
import org.apache.ibatis.annotations.Param;

/**
 * 树节点定义Mapper接口
 *
 * @author qindehua
 * @date 2022-09-06
 */
public interface AthenaDeviceNodeMapper
{
    /**
     * 查询树节点定义
     *
     * @param deviceNodeId 树节点定义主键
     * @return 树节点定义
     */
    public AthenaDeviceNode selectAthenaDeviceNodeByDeviceNodeId(Long deviceNodeId);


    /**
     * 替换字符串
     *
     * @param oldFunName 旧功能名称
     * @param newFunName 新功能名称
     * @return {@code Boolean }
     * @Author qindehua
     * @Date 2022/12/13
     **/
    public Boolean updateStringByFunName(@Param("oldFunName") String oldFunName,
                                         @Param("newFunName") String newFunName);

    /**
     * 查询树节点定义
     *
     * @param deviceNodeCode 树节点定义Code
     * @return 树节点定义
     */
    public AthenaDeviceNode selectAthenaDeviceNodeByDeviceNodeCode(String deviceNodeCode);

    /**
     * 查询树节点定义列表
     *
     * @param athenaDeviceNode 树节点定义
     * @return 树节点定义集合
     */
    List<AthenaDeviceNode> selectAthenaDeviceNodeList(AthenaDeviceNode athenaDeviceNode);



    /**
     * 根据名称和编号查询数量
     *
     * @param athenaDeviceNode 树节点定义
     * @return 树节点定义数量
     */
    int selectAthenaDeviceNodeCount(AthenaDeviceNode athenaDeviceNode);


    /**
     * 删除时 非主键集合
     *
     * @param deviceNodeIds id集合
     * @return 树节点定义数量
     */
    List<AthenaDeviceNode> selectAthenaDeviceNodeDelete(@Param("deviceNodeIds") Long[] deviceNodeIds);

    /**
     * 删除时 主键集合
     *
     * @param deviceNodeIds id集合
     * @return 树节点定义数量
     */
    List<AthenaDeviceNode> selectAthenaDeviceNodeDeleteList(@Param("deviceNodeIds") Long[] deviceNodeIds);

    /**
     * 新增树节点定义
     *
     * @param athenaDeviceNode 树节点定义
     * @return 结果
     */
    boolean insertAthenaDeviceNode(AthenaDeviceNode athenaDeviceNode);

    /**
     * 修改树节点定义
     *
     * @param athenaDeviceNode 树节点定义
     * @return 结果
     */
    int updateAthenaDeviceNode(AthenaDeviceNode athenaDeviceNode);

    /**
     * 删除树节点定义
     *
     * @param deviceNodeId 树节点定义主键
     * @return 结果
     */
    int deleteAthenaDeviceNodeByDeviceNodeId(Long deviceNodeId);

    /**
     * 批量删除树节点定义
     *
     * @param deviceNodeIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAthenaDeviceNodeByDeviceNodeIds(Long[] deviceNodeIds);
}
