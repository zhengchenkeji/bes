package com.zc.efounder.JEnterprise.service.deviceTree;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;

/**
 * 控制器Service接口
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
public interface ControllerService
{
    /**
     * 查询控制器
     *
     * @param id 控制器主键
     * @return 控制器
     */
    public Controller selectControllerById(Long id);

    /**
     * 查询控制器列表
     *
     * @param controller 控制器
     * @return 控制器集合
     */
    List<Controller> selectControllerList(Controller controller);

    /**
     * 新增控制器
     *
     * @param controller 控制器
     * @return 结果
     */
    AjaxResult insertController(Controller controller);

    /**
     * 修改控制器
     *
     * @param controller 控制器
     * @return 结果
     */
    AjaxResult updateController(Controller controller);


    /**
     * 设置时间,ddc
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    AjaxResult setTimeDDC(Long deviceTreeId,Integer type);

    /**
     * 获取时间,ddc
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    AjaxResult getTimeDDC(Long deviceTreeId,Integer type);

    /**
     * 重启DDC
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    AjaxResult restartDDC(Long deviceTreeId, Integer type);

    /**
     * 重置DDC
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    AjaxResult resetDDC(Long deviceTreeId, Integer type);

    /**
     * 同步DDC
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @param synchronize  是否批量同步
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/10/10
     **/
    AjaxResult synchronizeDDC(Long deviceTreeId, Integer type,boolean synchronize);

    /**
     * DDC远程升级
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    AjaxResult remoteUpgradeDdc(Long deviceTreeId, Integer type);
    /**
     * 参数对比
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/23
     **/
    AjaxResult getDDCInfoParam(Long deviceTreeId, Integer type);

    /**
     *
     * @Description: 刷新缓存信息
     *
     * @auther: wanghongjie
     * @date: 16:31 2022/11/18
     * @param:
     * @return:
     *
     */
    void initControllerCache();
}
