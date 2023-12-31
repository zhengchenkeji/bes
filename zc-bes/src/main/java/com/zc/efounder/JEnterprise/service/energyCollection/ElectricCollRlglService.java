package com.zc.efounder.JEnterprise.service.energyCollection;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;

import java.util.List;

/**
 * 电能参数采集方案关系Service接口
 *
 * @author gaojikun
 * @date 2022-09-09
 */
public interface ElectricCollRlglService
{
    /**
     * 查询电能参数采集方案关系
     *
     * @param electricCode 电能参数采集方案关系主键
     * @return 电能参数采集方案关系
     */
    public ElectricCollRlgl selectElectricCollRlglByElectricCode(String electricCode);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:查询电能参数采集方案关系列表
     * @Return:List<ElectricCollRlgl>
     */
    List<ElectricCollRlgl> selectElectricCollRlglList(ElectricCollRlgl electricCollRlgl);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:查询取消参数列表
     * @Return:List<ElectricParams>
     */
    List<ElectricParams> rlglNoCheckList(ElectricCollRlgl electricCollRlgl);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:查询选中参数列表
     * @Return:List<ElectricParams>
     */
    List<ElectricParams> rlglCheckList(ElectricCollRlgl electricCollRlgl);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:全部选中
     * @Return:boolean
     */
    boolean allCheckList(ElectricCollRlgl electricCollRlgl);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:全部取消
     * @Return:AjaxResult
     */
    AjaxResult allNoCheckList(ElectricCollRlgl electricCollRlgl);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:新增电能参数采集方案关系
     * @Return:AjaxResult
     */
    AjaxResult insertElectricCollRlgl(ElectricCollRlgl electricCollRlgl);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:删除电能参数
     * @Return:AjaxResult
     */
    AjaxResult deleteElectricCollRlgl(ElectricCollRlgl electricCollRlgl);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:修改电能参数
     * @Return:AjaxResult
     */
    AjaxResult changeRlglVlaue(ElectricCollRlgl electricCollRlgl);
}
