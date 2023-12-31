package com.zc.efounder.JEnterprise.mapper.energyCollection;

import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;

import java.util.List;

/**
 * 采集参数定义Mapper接口
 * 
 * @author ruoyi
 * @date 2022-09-07
 */
public interface ElectricParamsMapper 
{
    /**
     * 查询采集参数定义
     * 
     * @param id 采集参数定义主键
     * @return 采集参数定义
     */
    public ElectricParams selectElectricParamsById(Long id);

    /**
     * 根据code查询采集参数
     */
    ElectricParams selectElectricParamsByCode(String code);

    List<ElectricParams> selectElectricParamsCheck(ElectricParams electricParams);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:id
     * @Description:查询采集参数列表
     * @Return:List<ElectricParams>
     */
    List<ElectricParams> selectElectricParamsList(ElectricParams electricParams);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:id
     * @Description:新增采集参数
     * @Return:int
     */
    int insertElectricParams(ElectricParams electricParams);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:id
     * @Description:修改采集参数
     * @Return:int
     */
    int updateElectricParams(ElectricParams electricParams);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:id
     * @Description:删除采集参数
     * @Return:int
     */
    int deleteElectricParamsById(Long id);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:ids
     * @Description:删除采集参数
     * @Return:int
     */
    int deleteElectricParamsByIds(Long[] ids);

    /**
     * 获取所有采集参数数据
     * @return
     */
    List<ElectricParams> loadAll();
}
