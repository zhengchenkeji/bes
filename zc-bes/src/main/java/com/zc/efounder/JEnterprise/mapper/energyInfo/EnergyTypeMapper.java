package com.zc.efounder.JEnterprise.mapper.energyInfo;

import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 能源类型Mapper接口
 *
 * @author gaojikun
 * @date 2022-09-07
 */
public interface EnergyTypeMapper
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

    List<EnergyConfig> selectParkEnergyRlglList(EnergyConfig energyConfig);

    /**
     * 查询能源类型列表
     * @Author:gaojikun
     * @param code
     * @return 能源类型集合
     */
    List<Map<String,Object>> selectEnergyConfigList(@Param("code") String code);

    /**
     * 查询能源类型列表
     *
     * @param parkCode
     * @param energyCode
     * @return 能源类型集合
     */
    List<Map<String,Object>> selectEnergyConfigListByCode(@Param("parkCode") String parkCode,@Param("energyCode") String energyCode);

    /**
     * 查询能源类型列表
     *
     * @param energyType 能源类型
     * @return 能源类型集合
     */
    List<EnergyType> selectEnergyTypeListByCode(EnergyType energyType);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:energyType
     * @Description:新增能源类型
     * @Return:boolean
     */
    boolean insertEnergyType(EnergyType energyType);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:energyType
     * @Description:修改能源类型
     * @Return:boolean
     */
    boolean updateEnergyType(EnergyType energyType);

    /**
     * 删除能源类型
     *
     * @param guid 能源类型主键
     * @return 结果
     */
    boolean deleteEnergyTypeByGuid(String guid);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:guids
     * @Description:删除能源类型
     * @Return:boolean
     */
    boolean deleteEnergyTypeByGuids(String[] guids);

    /**
     * 查询该园区下的能耗类型
     *
     * @param energyConfig
     * @return 结果
     */
    List<Map<String, Object>> findpark_energytype(EnergyConfig energyConfig);

    /**
     * 新增园区能耗
     *
     * @param energyConfig
     * @return 结果
     */
    int addpark_energytype(EnergyConfig energyConfig);

    /**
     * 删除园区能耗
     *
     * @param energyConfig
     * @return 结果
     */
    int delpark_energytype(EnergyConfig energyConfig);

    /**
     * 查询所有能耗类型根据园区code
     * @return 结果
     */
    List<EnergyType> findAllByParkCode(@Param("parkCode") String parkCode);

    List<EnergyType> allEnergyTypeList();
}
