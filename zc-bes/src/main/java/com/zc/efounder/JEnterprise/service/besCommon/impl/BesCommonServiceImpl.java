package com.zc.efounder.JEnterprise.service.besCommon.impl;

import com.ruoyi.common.core.tree.ISSPTreeBuilder;
import com.ruoyi.common.core.tree.ISSPTreeNode;
import com.zc.efounder.JEnterprise.domain.besCommon.BESEnergyType;
import com.zc.efounder.JEnterprise.mapper.besCommon.BesCommonMapper;
import com.zc.efounder.JEnterprise.service.besCommon.BesCommonService;
import com.zc.efounder.JEnterprise.mapper.energyCollection.CollMethodMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.efounder.JEnterprise.mapper.energyInfo.ParkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BesCommonServiceImpl implements BesCommonService {

    @Autowired
    private BesCommonMapper besCommonMapper;

    @Autowired
    private ParkMapper parkMapper;

    @Autowired
    private CollMethodMapper collMethodMapper;

    /**
     * 查询采集参数定义左侧树
     */
    @Override
    public List<ISSPTreeNode> leftTree() {
        List<ISSPTreeNode> tree = getTreeJson("");
        return tree;
    }

    private List<ISSPTreeNode> getTreeJson(String yqbh) {
        List<BESEnergyType> energyTypelist = besCommonMapper.getEnergyTypeList(null);// 从数据库获取所有资源
        List<ISSPTreeNode> nodes = new ArrayList<ISSPTreeNode>();// 把所有资源转换成树模型的节点集合，此容器用于保存所有节点
        for (BESEnergyType energyType : energyTypelist) {
            ISSPTreeNode node = new ISSPTreeNode();
            node.setNodeTreeId(energyType.getCode());
            node.setId(energyType.getCode());
            node.setPid("");
            node.setText(energyType.getName());
            nodes.add(node);// 添加到节点容器
        }
        List<ISSPTreeNode> buildTree = ISSPTreeBuilder.buildTree(nodes);
        return buildTree;
    }

    /**
     * 园区能源配置左侧树
     */
    @Override
    public List<ISSPTreeNode> energyConfigLeftTree() {
        List<ISSPTreeNode> tree = getTreeJsonPark();
        return tree;
    }

    private List<ISSPTreeNode> getTreeJsonPark() {
        List<Park> parklist = parkMapper.findAllPark();// 从数据库获取所有资源
        List<ISSPTreeNode> nodes = new ArrayList<ISSPTreeNode>();// 把所有资源转换成树模型的节点集合，此容器用于保存所有节点
        for (Park park : parklist) {
            ISSPTreeNode node = new ISSPTreeNode();
            node.setNodeTreeId(park.getCode());
            node.setId(park.getCode());
            node.setPid("");
            node.setText(park.getName());
            nodes.add(node);// 添加到节点容器
        }
        List<ISSPTreeNode> buildTree = ISSPTreeBuilder.buildTree(nodes);
        return buildTree;
    }

    /**
     * 采集方案定义左侧树
     */
    @Override
    public List<ISSPTreeNode> collMethodLeftTree() {
        List<ISSPTreeNode> tree = getTreeJsoncollMethod();
        return tree;
    }

    private List<ISSPTreeNode> getTreeJsoncollMethod() {
        List<Map<String, String>> parklist = collMethodMapper.findPark_EnergyType("", "true");// 从数据库获取所有资源
        List<ISSPTreeNode> nodes = new ArrayList<>();// 把所有资源转换成树模型的节点集合，此容器用于保存所有节点

        //添加园区为父节点
        if (parklist != null && parklist.size() > 0) {
            for (Map<String, String> map : parklist) {
                ISSPTreeNode node = new ISSPTreeNode();
                node.setNodeTreeId("yq_" + map.get("F_YQBH"));
                node.setId("yq_" + map.get("F_YQBH"));
                node.setPid("");
                node.setText(map.get("F_YQMC"));
                //添加能源类型为子节点
                List<Map<String, String>> energyTypelist = collMethodMapper.findPark_EnergyType(map.get("F_YQBH"), "");
                List<ISSPTreeNode> childList = new ArrayList<>();
                if (energyTypelist != null && energyTypelist.size() > 0) {
                    for (Map<String, String> energyType : energyTypelist) {
                        ISSPTreeNode childnode = new ISSPTreeNode();
                        childnode.setNodeTreeId(energyType.get("F_NYBH"));
                        childnode.setId(energyType.get("F_NYBH"));
                        childnode.setPid("yq_" + energyType.get("F_YQBH"));
                        childnode.setText(energyType.get("F_NYMC"));
                        childList.add(childnode);
                    }
                    //添加子节点
                    node.setNodes(childList);
                    node.setIsChildNodeExist("1");
                    //node.setJs(subitemconf.getfJs());
                    nodes.add(node);// 添加到节点容器
                }
            }
        }

        return nodes;
    }
}
