package com.zc.datareported.model.energy;

import com.zc.datareported.model.build.CommonModel;
import com.zc.common.framework.xmlprocessor.annotation.AttrXml;
import com.zc.common.framework.xmlprocessor.annotation.RootXml;

/**
 * 能耗信息
 * @author xiepufeng
 * @date 2020/11/10 10:21
 */
@RootXml("root")
public class EnergyXmlModel
{
    /**
     * 建筑属性通用字段
     */
    @AttrXml(isInnerAttr = true)
    private CommonModel common;

    /**
     * 建筑能耗信息
     */
    @AttrXml(isInnerAttr = true)
    private EnergyDataModel data;

    public CommonModel getCommon()
    {
        return common;
    }

    public void setCommon(CommonModel common)
    {
        this.common = common;
    }

    public EnergyDataModel getData()
    {
        return data;
    }

    public void setData(EnergyDataModel data)
    {
        this.data = data;
    }
}
