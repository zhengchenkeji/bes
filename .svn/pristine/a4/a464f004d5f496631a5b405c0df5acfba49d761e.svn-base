package com.zc.efounder.JEnterprise.service.systemSetting;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.systemSetting.ParameterConfiguration;
import com.zc.efounder.JEnterprise.domain.systemSetting.ParamsConfiguration;

/**
 * 主采集参数Service接口
 * 
 * @author gaojikun
 * @date 2022-11-30
 */
public interface ParameterConfigurationService
{
    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:id
     * @Description:获取主采集参数详细信息
     * @Return:ParameterConfiguration
     */
    public ParameterConfiguration selectAthenaBesParamsById(Long id);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:parameterConfiguration
     * @Description:查询主采集参数列表
     * @Return:TableDataInfo
     */
    List<ParameterConfiguration> selectAthenaBesParamsList(ParameterConfiguration parameterConfiguration);

    /**
     * 新增主采集参数
     * 
     * @param parameterConfiguration 主采集参数
     * @return 结果
     */
    AjaxResult insertAthenaBesParams(ParameterConfiguration parameterConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:parameterConfiguration
     * @Description:修改主采集参数
     * @Return:AjaxResult
     */
    AjaxResult updateAthenaBesParams(ParameterConfiguration parameterConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:ids
     * @Description:删除主采集参数
     * @Return:AjaxResult
     */
    AjaxResult deleteAthenaBesParamsByIds(Long[] ids);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:ids
     * @Description:删除主采集参数
     * @Return:int
     */
    int deleteAthenaBesParamsById(Long id);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:查询选择采集参数列表
     * @Return:List<ElectricParams>
     */
    List<ElectricParams> listCheckList(ParamsConfiguration paramsConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:查询未选择采集参数列表
     * @Return:List<ElectricParams>
     */
    List<ElectricParams> listNoCheckList(ParamsConfiguration paramsConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:全部选中采集参数
     * @Return:AjaxResult
     */
    AjaxResult allCheckList(ParamsConfiguration paramsConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:全部取消
     * @Return:AjaxResult
     */
    AjaxResult allNoCheckList(ParamsConfiguration paramsConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:47
     * @Param:paramsConfiguration
     * @Description:添加一条采集参数
     * @Return:AjaxResult
     */
    AjaxResult insertParamConfigRlgl(ParamsConfiguration paramsConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:取消一条采集参数
     * @Return:AjaxResult
     */
    AjaxResult delParamConfigRlgl(ParamsConfiguration paramsConfiguration);
}
