package com.zc.efounder.JEnterprise.mapper.energyInfo;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

/**
 * 支路拓扑配置Mapper接口
 * @author qindehua
 */
public interface AthenaBranchConfigMapper
{


    /**
     * 新增时查看 是否有重复支路名称
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @return 支路拓扑配置
     */
    public AthenaBranchConfig selectAthenaBranchConfigRepeat(AthenaBranchConfig athenaBranchConfig);
    /**
     * 删除时查看 当前节点下是否有子节点
     *
     * @param branchIds 支路拓扑配置主键集合
     * @return 支路拓扑配置
     */
    public AthenaBranchConfig selectAthenaBranchConfigSun(@Param("branchIds") Long[] branchIds);

    /**
     * 查询支路拓扑配置
     *
     * @param branchId 支路拓扑配置主键
     * @return 支路拓扑配置
     */
    public AthenaBranchConfig selectAthenaBranchConfigByBranchId(Long branchId);

    /**
     * 查询建筑列表
     * @param parkCode 园区code
     * @return map值
     */
    @MapKey("id")
    List<Map>  selectBuildingList(@Param("parkCode") String parkCode);

    /**
     * 查询支路拓扑配置列表
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @return 支路拓扑配置集合
     */
    List<AthenaBranchConfig> selectAthenaBranchConfigList(AthenaBranchConfig athenaBranchConfig);

    /**
     * 查询支路   父节点
     *
     * @return 支路集合
     */
    List<AthenaBranchConfig> selectAthenaBranchConfigListFather(@Param("parkCode") String parkCode,@Param("energyCode")String energyCode);

    /**
     * 询支路拓扑配置列表 查询 自身及子数据
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @return 支路拓扑配置集合
     */
    List<AthenaBranchConfig> selectAthenaBranchConfigListSun(@Param("athenaBranchConfig")AthenaBranchConfig athenaBranchConfig,@Param("code") String code);

    /**
     * 新增支路拓扑配置
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @return 结果
     */
    boolean insertAthenaBranchConfig(AthenaBranchConfig athenaBranchConfig);

    /**
     *
     * 新增 修改支路拓扑配置时  查询最大编号位
     *
     * @param parkCode   园区code
     * @param energyCode 能源code
     * @param length     截取长度
     * @return 结果
     */
    Integer selectAthenaBranchConfigCodeNum(@Param("parkCode")String parkCode,
                                            @Param("energyCode")String energyCode,
                                            @Param("length")Integer length);

    /**
     * 修改支路拓扑配置
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @return 结果
     */
    boolean updateAthenaBranchConfig(AthenaBranchConfig athenaBranchConfig);

    /**
     * 删除支路拓扑配置
     *
     * @param branchId 支路拓扑配置主键
     * @return 结果
     */
    int deleteAthenaBranchConfigByBranchId(Long branchId);

    /**
     * 批量删除支路拓扑配置
     *
     * @param branchIds 需要删除的数据主键集合
     * @return 结果
     */
    boolean deleteAthenaBranchConfigByBranchIds(Long[] branchIds);
}
