package com.zc.efounder.JEnterprise.service.energyCollection;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;

import java.util.List;

/**
 * 采集参数定义Service接口
 *
 * @author gaojikun
 * @date 2022-09-07
 */
public interface ElectricParamsService
{
    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:id
     * @Description:获取采集参数详细信息
     * @Return:ElectricParams
     */
    public ElectricParams selectElectricParamsById(Long id);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:electricParams
     * @Description:查询采集参数列表
     * @Return:List<ElectricParams>
     */
    List<ElectricParams> selectElectricParamsList(ElectricParams electricParams);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:electricParams
     * @Description:新增采集参数
     * @Return:AjaxResult
     */
    AjaxResult insertElectricParams(ElectricParams electricParams);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:electricParams
     * @Description:修改采集参数
     * @Return:AjaxResult
     */
    AjaxResult updateElectricParams(ElectricParams electricParams);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:ids
     * @Description:删除采集参数
     * @Return:AjaxResult
     */
    AjaxResult deleteElectricParamsByIds(Long[] ids);



    /**
     * 查询采集参数定义字典
     *
     * @return 采集参数定义列表
     */
    public List<ElectricParams> selectElectricParamsListbymeterid(String meterid);
}
