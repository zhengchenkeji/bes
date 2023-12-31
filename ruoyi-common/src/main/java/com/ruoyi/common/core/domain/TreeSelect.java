package com.ruoyi.common.core.domain;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.core.domain.entity.*;

/**
 * Treeselect树结构实体类
 *
 */
public class TreeSelect<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private T id;

    /** 节点名称 */
    private String label;

    /** 节点类型*/
    private String type;

    private Boolean disabled;

    private T controllerId;

    private T Pid;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect()
    {

    }

    public TreeSelect(T id, String label,Boolean disabled,T pid) {
        this.id = id;
        this.label = label;
        this.disabled=disabled;
        this.Pid=pid;
    }

    public TreeSelect(T id, String label, Boolean disabled, T controllerId,T pid) {
        this.id = id;
        this.label = label;
        this.disabled = disabled;
        this.controllerId = controllerId;
        this.Pid=pid;
    }

    public TreeSelect(SysDept dept)
    {
        this.id = (T) dept.getDeptId();
        this.label = dept.getDeptName();
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(SysMenu menu)
    {
        this.id = (T)menu.getMenuId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(AthenaBranchConfig branchConfig)
    {
        this.id = (T) branchConfig.getBranchId();
        this.label = branchConfig.getBranchName();
        this.children = branchConfig.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(AthenaBesHouseholdConfig athenaBesHouseholdConfig)
    {
        this.id = (T) athenaBesHouseholdConfig.getHouseholdId();
        this.label = athenaBesHouseholdConfig.getHouseholdName();
        this.children = athenaBesHouseholdConfig.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }
    public TreeSelect(SubitemConfig subitemConfig)
    {
        this.id = (T) subitemConfig.getSubitemId();
        this.label = subitemConfig.getSubitemName();
        this.children = subitemConfig.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public T getId()
    {
        return id;
    }

    public void setId(T id)
    {
        this.id = id;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public List<TreeSelect> getChildren()
    {
        return children;
    }

    public void setChildren(List<TreeSelect> children)
    {
        this.children = children;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public T getPid() {
        return Pid;
    }

    public void setPid(T pid) {
        Pid = pid;
    }

    public T getControllerId() {
        return controllerId;
    }

    public void setControllerId(T controllerId) {
        this.controllerId = controllerId;
    }
}
