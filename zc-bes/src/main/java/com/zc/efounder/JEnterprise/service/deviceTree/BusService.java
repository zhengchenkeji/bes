package com.zc.efounder.JEnterprise.service.deviceTree;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.deviceTree.Bus;

/**
 * 总线Service接口
 *
 * @author ruoyi
 * @date 2022-09-15
 */
public interface BusService
{
    /**
     * 查询总线
     *
     * @param deviceTreeId 总线主键
     * @return 总线
     */
    public Bus selectBusByDeviceTreeId(String deviceTreeId);

    /**
     * 查询总线列表
     *
     * @param bus 总线
     * @return 总线集合
     */
    List<Bus> selectBusList(Bus bus);

    /**
     * 新增总线
     *
     * @param bus 总线
     * @return 结果
     */
    AjaxResult insertBus(Bus bus);

    /**
     * 修改总线
     *
     * @param bus 总线
     * @return 结果
     */
    AjaxResult updateBus(Bus bus);

    /**
     * 批量删除总线
     *
     * @param deviceTreeIds 需要删除的总线主键集合
     * @return 结果
     */
    int deleteBusByDeviceTreeIds(String[] deviceTreeIds);

    /**
     * 删除总线信息
     *
     * @param deviceTreeId 总线主键
     * @return 结果
     */
    AjaxResult deleteBusByDeviceTreeId(String deviceTreeId);

    /**
     *
     * @Description: 刷新总线缓存
     *
     * @auther: wanghongjie
     * @date: 16:39 2022/11/18
     * @param:
     * @return:
     *
     */
    void addBusCache();
}
