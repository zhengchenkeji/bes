package com.zc.efounder.JEnterprise.service.energyInfo;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyConfigList;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;

import java.util.List;
import java.util.Map;

/**
 * 能源类型Service接口
 *
 * @author gaojikun
 * @date 2022-09-07
 */
public interface EnergyTypeService
{
    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:guid
     * @Description:获取能源类型详细信息
     * @Return:EnergyType
     */
    public EnergyType selectEnergyTypeByGuid(String guid);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:energyType
     * @Description:查询能源类型列表
     * @Return:List<EnergyType>
     */
    List<EnergyType> selectEnergyTypeList(EnergyType energyType);

    /**
     * 查询能耗列表
     * @Author:gaojikun
     * @param code
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> selectEnergyConfigList(String code);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:energyType
     * @Description:新增能源类型
     * @Return:AjaxResult
     */
    AjaxResult insertEnergyType(EnergyType energyType);

    /**
     * 新增能园区能耗
     * @return 结果
     */
    AjaxResult insertEnergyConfig(EnergyConfigList energyConfigList);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:energyType
     * @Description:修改能源类型
     * @Return:AjaxResult
     */
    AjaxResult updateEnergyType(EnergyType energyType);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:guids
     * @Description:删除能源类型
     * @Return:AjaxResult
     */
    AjaxResult deleteEnergyTypeByGuids(String[] guids);

    /**
     * 删除能源类型信息
     *
     * @param guid 能源类型主键
     * @return 结果
     */
    AjaxResult deleteEnergyTypeByGuid(String guid);

    /**
     * 查询该园区下所有能耗类型
     *
     * @param parkCode 园区code
     * @return {@code List<EnergyType> }
     * @Author qindehua
     * @Date 2022/12/21
     **/
    List<EnergyType> findAllByParkCode(String parkCode);
    /**
     * 查询所有能耗类型
     *
     * @return {@code List<EnergyType> }
     * @Author gaojikun
     * @Date 2022/12/21
     **/
    List<EnergyType> allEnergyTypeList(Point point);
}
