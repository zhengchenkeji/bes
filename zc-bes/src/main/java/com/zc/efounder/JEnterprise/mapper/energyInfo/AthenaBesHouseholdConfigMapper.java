package com.zc.efounder.JEnterprise.mapper.energyInfo;

import com.ruoyi.common.core.domain.entity.AthenaBesHouseholdConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分户计量拓扑配置Mapper接口
 *
 * @author qindehua
 * @date 2022-09-19
 */
public interface AthenaBesHouseholdConfigMapper
{

    /**
     * 查询分户 父节点
     *
     * @return 分户集合
     */
    List<AthenaBesHouseholdConfig> selectAthenaBesHouseholdConfigListFather(@Param("parkCode") String parkCode,
                                                                            @Param("energyCode")String energyCode);

    /**
     * 查询分户计量拓扑配置
     *
     * @param id 分户计量拓扑配置主键
     * @return 分户计量拓扑配置
     */
    public AthenaBesHouseholdConfig selectAthenaBesHouseholdConfigById(Long id);

    /**
     * 查询分户计量拓扑配置列表
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @return 分户计量拓扑配置集合
     */
    List<AthenaBesHouseholdConfig> selectAthenaBesHouseholdConfigList(AthenaBesHouseholdConfig athenaBesHouseholdConfig);

    /**
     * 查询分户计量拓扑配置列表及下面子节点
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @param code               查询标识
     * @return 分户计量拓扑配置集合
     */
    List<AthenaBesHouseholdConfig> selectAthenaBesHouseholdConfigListSun(
            @Param("householdConfig") AthenaBesHouseholdConfig athenaBesHouseholdConfig,
            @Param("code")String code);

    /**
     * 新增 修改分户拓扑配置时  查询最大编号位
     *
     * @param parkCode   园区code
     * @param energyCode 能源code
     * @param length     截取长度
     * @return 结果
     */
    Integer selectHouseholdConfigCodeNum(@Param("parkCode")String parkCode,
                                        @Param("energyCode")String energyCode,
                                        @Param("length")Integer length);


    /**
     * 新增时查看 是否有重复分户名称
     *
     * @param athenaBesHouseholdConfig 分户拓扑配置
     * @return 分户拓扑配置
     */
    public AthenaBesHouseholdConfig selectAthenaBesHouseholdConfigRepeat(AthenaBesHouseholdConfig athenaBesHouseholdConfig);

    /**
     * 新增分户计量拓扑配置
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @return 结果
     */
    Boolean insertAthenaBesHouseholdConfig(AthenaBesHouseholdConfig athenaBesHouseholdConfig);

    /**
     * 修改分户计量拓扑配置
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @return 结果
     */
    Boolean updateAthenaBesHouseholdConfig(AthenaBesHouseholdConfig athenaBesHouseholdConfig);

    /**
     * 删除分户计量拓扑配置
     *
     * @param id 分户计量拓扑配置主键
     * @return 结果
     */
    int deleteAthenaBesHouseholdConfigById(Long id);


    /**
     * 删除时查看 当前节点下是否有子节点
     *
     * @param householdIds 分户拓扑配置主键集合
     * @return 分户拓扑配置
     */
    public AthenaBesHouseholdConfig selectAthenaBesHouseholdConfigSun(@Param("householdIds")Long[] householdIds);

    /**
     * 批量删除分户计量拓扑配置
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAthenaBesHouseholdConfigByIds(Long[] ids);
}
