package com.zc.efounder.JEnterprise.mapper.energyInfo;

import java.util.List;
import java.util.Map;

import com.zc.efounder.JEnterprise.domain.energyInfo.Park;

/**
 * 园区Mapper接口
 * 
 * @author ruoyi
 * @date 2022-09-08
 */
public interface ParkMapper 
{
    /**
     * 查询园区
     * 
     * @param code 园区主键
     * @return 园区
     */
    public Park selectParkByCode(String code);

    /**
     * 查询园区列表
     * 
     * @param park 园区
     * @return 园区集合
     */
    List<Park> selectParkList(Park park);

    /**
     * 查询所有园区
     */
    List<Park> findAllPark();

    /**
     * 查询最大code
     */
    String findMaxYqbh();

    /**
     * 新增园区
     * 
     * @param park 园区
     * @return 结果
     */
    int insertPark(Park park);

    /**
     * 修改园区
     * 
     * @param park 园区
     * @return 结果
     */
    int updatePark(Park park);

    /**
     * 批量删除园区
     * 
     * @param codes 需要删除的数据主键集合
     * @return 结果
     */
    boolean deleteParkByCodes(String[] codes);


    /**
     * 用户列表
     * @return 结果
     */
    List<Map<String,Object>> listUser();

    /**
     * 组织机构列表
     * @return 结果
     */
    List<Map<String,Object>> listOrganization();
}
