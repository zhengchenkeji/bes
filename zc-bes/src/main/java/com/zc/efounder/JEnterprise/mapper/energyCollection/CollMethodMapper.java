package com.zc.efounder.JEnterprise.mapper.energyCollection;

import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 采集方案Mapper接口
 *
 * @author gaojikun
 * @date 2022-09-08
 */
public interface CollMethodMapper {
    /**
     * 查询采集方案
     *
     * @param code 采集方案主键
     * @return 采集方案
     */
    public CollMethod selectCollMethodByCode(@Param(value = "code") String code);

    List<CollMethod> selectCollMethodCheck(CollMethod collMethod);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:查询采集方案列表
     * @Return:List<CollMethod>
     */
    List<CollMethod> selectCollMethodList(CollMethod collMethod);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:新增采集方案
     * @Return:int
     */
    int insertCollMethod(CollMethod collMethod);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:修改采集方案
     * @Return:int
     */
    int updateCollMethod(CollMethod collMethod);

    /**
     * 删除采集方案
     *
     * @param id 采集方案主键
     * @return 结果
     */
    int deleteCollMethodByCode(Long id);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:codes
     * @Description:删除采集方案
     * @Return:boolean
     */
    boolean deleteCollMethodByCodes(Long[] ids);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:查询能耗类型
     * @Return:List<Map<String,String>>
     */
    List<Map<String, String>> findPark_EnergyType(@Param(value = "fyqbh") String fyqbh, @Param(value = "group") String group);
}
