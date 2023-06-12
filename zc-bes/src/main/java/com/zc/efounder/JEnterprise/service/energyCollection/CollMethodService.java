package com.zc.efounder.JEnterprise.service.energyCollection;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;

import java.util.List;

/**
 * 采集方案Service接口
 *
 * @author gaojikun
 * @date 2022-09-08
 */
public interface CollMethodService {
    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:code
     * @Description:获取采集方案详细信息
     * @Return:CollMethod
     */
    public CollMethod selectCollMethodByCode(Long id);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:查询采集方案列表
     * @Return:List<CollMethod>
     */
    List<CollMethod> selectCollMethodList(CollMethod collMethod);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:新增采集方案
     * @Return:AjaxResult
     */
    AjaxResult insertCollMethod(CollMethod collMethod);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:修改采集方案
     * @Return:AjaxResult
     */
    AjaxResult updateCollMethod(CollMethod collMethod);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:ids
     * @Description:删除采集方案
     * @Return:AjaxResult
     */
    AjaxResult deleteCollMethodByCodes(Long[] ids);

    /**
     * 删除采集方案信息
     *
     * @param id 采集方案主键
     * @return 结果
     */
    AjaxResult deleteCollMethodByCode(Long id);
}
