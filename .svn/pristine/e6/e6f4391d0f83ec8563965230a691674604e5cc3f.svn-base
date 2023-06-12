package com.zc.efounder.JEnterprise.domain.deviceTreeNode;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;

/**
 * 树节点定义对象 athena_bes_device_node
 *
 * @author qindehua
 */
@ApiModel(value = "AthenaDeviceNode",description = "树节点定义对象")
public class AthenaDeviceNode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    @ApiModelProperty(value = "节点ID",required = true)
    private Long deviceNodeId;

    /** 节点编号 */
    @Excel(name = "节点编号")
    @NotBlank(message = "节点编号不允许为空!")
    @ApiModelProperty(value = "节点编号",required = true)
    private String deviceNodeCode;

    /** 是否节点 */
    @Excel(name = "是否节点",readConverterExp = "1=是,2=否")
    @NotBlank(message = "是否节点不允许为空!")
    @ApiModelProperty(value = "是否节点",required = true)
    private String deviceNodeIsNode;

    /** 节点名称 */
    @Excel(name = "节点名称")
    @NotBlank(message = "节点名称不允许为空!")
    @ApiModelProperty(value = "节点名称",required = true)
    private String deviceNodeName;

    /** 节点功能名称（以，隔开） */
    @Excel(name = "节点功能名称")
    @ApiModelProperty(value = "节点功能名称（以，隔开）")
    private String deviceNodeFunName;

    /** 新增节点类型（以，隔开） */
    @Excel(name = "新增节点类型")
    @ApiModelProperty(value = "新增节点类型（以，隔开）")
    private String deviceNodeFunType;

    /** 在线图片路径 */
    @Excel(name = "在线图片路径")
    @ApiModelProperty(value = "在线图片路径")
    private String onlineIcon;

    /** 离线图片路径 */
    @Excel(name = "离线图片路径")
    @ApiModelProperty(value = "离线图片路径")
    private String offlineIcon;

    /** 维护url */
    @Excel(name = "维护url")
    @ApiModelProperty(value = "维护url")
    private String url;



    public void setDeviceNodeId(Long deviceNodeId)
    {
        this.deviceNodeId = deviceNodeId;
    }

    public Long getDeviceNodeId()
    {
        return deviceNodeId;
    }
    public void setDeviceNodeCode(String deviceNodeCode)
    {
        this.deviceNodeCode = deviceNodeCode;
    }

    public String getDeviceNodeCode()
    {
        return deviceNodeCode;
    }
    public void setDeviceNodeIsNode(String deviceNodeIsNode)
    {
        this.deviceNodeIsNode = deviceNodeIsNode;
    }

    public String getDeviceNodeIsNode()
    {
        return deviceNodeIsNode;
    }
    public void setDeviceNodeName(String deviceNodeName)
    {
        this.deviceNodeName = deviceNodeName;
    }

    public String getDeviceNodeName()
    {
        return deviceNodeName;
    }
    public void setDeviceNodeFunName(String deviceNodeFunName)
    {
        this.deviceNodeFunName = deviceNodeFunName;
    }

    public String getDeviceNodeFunName()
    {
        return deviceNodeFunName;
    }
    public void setDeviceNodeFunType(String deviceNodeFunType)
    {
        this.deviceNodeFunType = deviceNodeFunType;
    }

    public String getDeviceNodeFunType()
    {
        return deviceNodeFunType;
    }
    public void setOnlineIcon(String onlineIcon)
    {
        this.onlineIcon = onlineIcon;
    }

    public String getOnlineIcon()
    {
        return onlineIcon;
    }
    public void setOfflineIcon(String offlineIcon)
    {
        this.offlineIcon = offlineIcon;
    }

    public String getOfflineIcon()
    {
        return offlineIcon;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("deviceNodeId", getDeviceNodeId())
            .append("deviceNodeCode", getDeviceNodeCode())
            .append("deviceNodeIsNode", getDeviceNodeIsNode())
            .append("deviceNodeName", getDeviceNodeName())
            .append("deviceNodeFunName", getDeviceNodeFunName())
            .append("deviceNodeFunType", getDeviceNodeFunType())
            .append("onlineIcon", getOnlineIcon())
            .append("offlineIcon", getOfflineIcon())
            .append("url", getUrl())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
