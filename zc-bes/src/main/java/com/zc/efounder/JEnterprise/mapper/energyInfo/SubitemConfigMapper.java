package com.zc.efounder.JEnterprise.mapper.energyInfo;

import com.ruoyi.common.core.domain.entity.SubitemConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分项拓扑配置Mapper接口
 *
 * @author qindehua
 * @date 2022-09-20
 */
public interface SubitemConfigMapper
{
    /**
     * 查询分项拓扑配置
     *
     * @param subitemId 分项拓扑配置主键
     * @return 分项拓扑配置
     */
    public SubitemConfig selectSubitemConfigBySubitemId(String subitemId);

    /**
     * 查询分项拓扑配置列表
     *
     * @param subitemConfig 分项拓扑配置
     * @return 分项拓扑配置集合
     */
    List<SubitemConfig> selectSubitemConfigList(SubitemConfig subitemConfig);

    /**
     * 新增分项拓扑配置
     *
     * @param subitemConfig 分项拓扑配置
     * @return 结果
     */
    Boolean insertSubitemConfig(SubitemConfig subitemConfig);

    /**
     * 批量新增分项拓扑配置
     *
     * @param subitemConfig 分项拓扑配置
     * @return 结果
     */
    boolean insertSubitemBatch(@Param("list") List<SubitemConfig> subitemConfig);


    /**
     * 修改分项拓扑配置
     *
     * @param subitemConfig 分项拓扑配置
     * @return 结果
     */
    Boolean updateSubitemConfig(SubitemConfig subitemConfig);

    /**
     * 删除分项拓扑配置
     *
     * @param subitemId 分项拓扑配置主键
     * @return 结果
     */
    int deleteSubitemConfigBySubitemId(String subitemId);

    /**
     * 批量删除分项拓扑配置
     *
     * @param subitemIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteSubitemConfigBySubitemIds(String[] subitemIds);

    /**
     * 删除分项配置
     *
     * @param subitemConfig 分项配置
     * @return int
     * @Author qindehua
     * @Date 2022/11/14
     **/
    int deleteSubitemConfig(SubitemConfig subitemConfig);


    /**
     * 新增 修改分项拓扑配置时  查询最大编号位
     *
     * @param parkCode   园区code
     * @param energyCode 能源code
     * @param length     截取长度
     * @return 结果
     */
    Integer selectSubitemConfigCodeNum(@Param("parkCode")String parkCode,
                                         @Param("energyCode")String energyCode,
                                         @Param("length")Integer length);


    /**
     * 新增时查看 是否有重复分项名称
     *
     * @param subitemConfig 分项拓扑配置
     * @return 分项拓扑配置
     */
    SubitemConfig selectSubitemConfigRepeat(SubitemConfig subitemConfig);




    /**
     * 删除时查看 当前节点下是否有子节点
     *
     * @param subitemIds 分项拓扑配置主键集合
     * @return 分项拓扑配置
     */
     SubitemConfig selectSubitemConfigSun(@Param("subitemIds")String[] subitemIds);




    /**
     * 查询分项计量拓扑配置列表及下面子节点
     *
     * @param subitemConfig 分项计量拓扑配置
     * @param code               查询标识
     * @return 分项计量拓扑配置集合
     */
    List<SubitemConfig> selectSubitemConfigListSun(
            @Param("subitemConfig") SubitemConfig subitemConfig,
            @Param("code")String code);

    /**
     * 查询分项 父节点
     *
     * @return 分项集合
     */
    List<SubitemConfig> selectSubitemConfigListFather(
            @Param("parkCode") String parkCode,
            @Param("energyCode")String energyCode);
}
