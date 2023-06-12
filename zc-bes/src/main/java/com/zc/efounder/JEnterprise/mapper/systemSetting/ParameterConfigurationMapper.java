package com.zc.efounder.JEnterprise.mapper.systemSetting;

import java.util.List;

import com.zc.efounder.JEnterprise.domain.systemSetting.ParameterConfiguration;
import com.zc.efounder.JEnterprise.domain.systemSetting.ParamsConfiguration;

/**
 * 主采集参数Mapper接口
 *
 * @author gaojikun
 * @date 2022-11-30
 */
public interface ParameterConfigurationMapper {
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
     * @Return:List<ParameterConfiguration>
     */
    List<ParameterConfiguration> selectAthenaBesParamsList(ParameterConfiguration parameterConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:parameterConfiguration
     * @Description:新增主采集参数
     * @Return:boolean
     */
    boolean insertAthenaBesParams(ParameterConfiguration parameterConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:parameterConfiguration
     * @Description:修改主采集参数
     * @Return:boolean
     */
    boolean updateAthenaBesParams(ParameterConfiguration parameterConfiguration);

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
     * @Param:ids
     * @Description:删除主采集参数
     * @Return:int
     */
    int deleteAthenaBesParamsByIds(Long[] ids);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:查询选择采集参数列表
     * @Return:List<ElectricParams>
     */
    List<ParamsConfiguration> listCheckList(ParamsConfiguration paramsConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:全部取消采集参数
     * @Return:AjaxResult
     */
    boolean delAllConfig(ParamsConfiguration paramsConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:取消一条采集参数
     * @Return:AjaxResult
     */
    boolean delParamConfig(ParamsConfiguration paramsConfiguration);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:添加一条采集参数
     * @Return:AjaxResult
     */
    boolean insertConfig(ParamsConfiguration paramsConfiguration);

}
