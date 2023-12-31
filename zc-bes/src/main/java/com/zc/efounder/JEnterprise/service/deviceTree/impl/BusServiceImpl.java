package com.zc.efounder.JEnterprise.service.deviceTree.impl;

import java.util.List;
import java.util.Map;

import com.zc.ApplicationContextProvider;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.service.deviceTree.AthenaElectricMeterService;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Service;
import com.zc.efounder.JEnterprise.mapper.deviceTree.BusMapper;
import com.zc.efounder.JEnterprise.domain.deviceTree.Bus;
import com.zc.efounder.JEnterprise.service.deviceTree.BusService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 总线Service业务层处理
 *
 * @author ruoyi
 * @date 2022-09-15
 */
@Service
public class BusServiceImpl implements BusService
{
    @Resource
    private BusMapper busMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private AthenaElectricMeterService meterService;

    // 设备树缓存定义
    private DeviceTreeCache deviceTreeCache = ApplicationContextProvider.getBean(DeviceTreeCache.class);


    @PostConstruct
    public void init()
    {
        /**
         * 添加数据到 redis 缓存
         */
        addBusCache();
    }

    /**
     * 添加数据到 redis 缓存
     */
    @Override
    public void addBusCache()
    {
        // 获取全部设备列表数据
        List<Bus> busList = selectBusList(null);
        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_DeviceTree_Bus);

        if (busList == null || busList.isEmpty())
        {
            return;
        }


        // 添加 redis 缓存数据
        busList.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus, val.getDeviceTreeId(), val);
        });
    }

    /**
     * 查询总线
     *
     * @param deviceTreeId 总线主键
     * @return 总线
     */
    @Override
    public Bus selectBusByDeviceTreeId(String deviceTreeId)
    {
        return busMapper.selectBusByDeviceTreeId(deviceTreeId);
    }

    /**
     * 查询总线列表
     *
     * @param bus 总线
     * @return 总线
     */
    @Override
    public List<Bus> selectBusList(Bus bus)
    {
        return busMapper.selectBusList(bus);
    }

    /**
     * 新增总线
     *
     * @param bus 总线
     * @return 结果
     */
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public AjaxResult insertBus(Bus bus)
    {
        bus.setCreateTime(DateUtils.getNowDate());
        String alias = bus.getAlias();//别名
        String port = bus.getPort();//端口
        String deviceNodeId = bus.getDeviceNodeId();//所属节点类
        String deviceType = bus.getDeviceType();//设备类型 1:楼控 2:照明  3:采集器
        String deviceTreeFatherId = bus.getDeviceTreeFatherId();//父节点id
        if (!StringUtils.hasText(alias)
                || !StringUtils.hasText(port)
                || !StringUtils.hasText(deviceNodeId)
                || !StringUtils.hasText(deviceType)
                || !StringUtils.hasText(deviceTreeFatherId)){
            return AjaxResult.error("添加失败,参数错误");
        }

        //验证端口是否占用
        String repeatPort = busMapper.queryRepeatPort(bus);
        if (StringUtils.hasText(repeatPort)){
            return AjaxResult.error("添加失败,端口已占用!");
        }

        //查询父节点名称
        Map<String,Object> fatherInfo = busMapper.queryFatherSysName(bus.getDeviceTreeFatherId());
        String fatherSysName = fatherInfo.get("sysName").toString();
        //根据父节点id查出该节点下 最大的总线名称
        String maxBusName = busMapper.queryMaxBusName(bus.getDeviceTreeFatherId());
        String busName = "";
        if (maxBusName != null && !"".equals(maxBusName)){
            int max = Integer.parseInt(maxBusName.substring(maxBusName.length()-1))+1;
            busName = fatherSysName + "_zongxian" + max;
        } else {
            busName = fatherSysName + "_zongxian1";
        }
        bus.setSysName(busName);
        bus.setPark(fatherInfo.get("park").toString());

        try{
            //添加设备树表
            boolean addDeviceTree = busMapper.insertDeviceTree(bus);
            if (!addDeviceTree){
                throw new Exception();
            }

            //添加到主线表
            boolean addBus = busMapper.insertBus(bus);
            if (!addBus){
                throw new Exception();
            }
        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("添加失败");
        }

        //重新加载缓存
        //总线缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus,bus.getDeviceTreeId(),bus);

        //从表中查出树信息
        DeviceTree deviceTreeNode = busMapper.selectDeviceTreeByDeviceTreeId(bus.getDeviceTreeId().toString());

        //获取设备树缓存中的采集器状态, 采集器在线则总线在线
        DeviceTree controller = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree,bus.getDeviceTreeFatherId());
        deviceTreeNode.setDeviceTreeStatus(controller.getDeviceTreeStatus());
        //设备树缓存
        int deviceTreeId = deviceTreeNode.getDeviceTreeId(); //设备树id
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree,(long) deviceTreeId,deviceTreeNode);

        return AjaxResult.success("添加成功",deviceTreeNode);
    }

    /**
     * 修改总线
     *
     * @param bus 总线
     * @return 结果
     */
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public AjaxResult updateBus(Bus bus){
        String deviceTreeId = bus.getDeviceTreeId().toString(); //设备树id
        String alias = bus.getAlias();//别名
        String port = bus.getPort();//端口
        String deviceNodeId = bus.getDeviceNodeId();//所属节点类
        String deviceType = bus.getDeviceType();//设备类型 1:楼控 2:照明  3:采集器
        String deviceTreeFatherId = bus.getDeviceTreeFatherId();//父节点id
        if (!StringUtils.hasText(deviceTreeId)
                || !StringUtils.hasText(alias)
                || !StringUtils.hasText(port)
                || !StringUtils.hasText(deviceNodeId)
                || !StringUtils.hasText(deviceType)
                || !StringUtils.hasText(deviceTreeFatherId)){
            return AjaxResult.error("修改失败,参数错误");
        }

        //验证端口是否占用
        String repeatPort = busMapper.queryRepeatPort(bus);
        if (StringUtils.hasText(repeatPort)){
            return AjaxResult.error("修改失败,端口已占用!");
        }
        bus.setUpdateTime(DateUtils.getNowDate());

        //查询总线下面的所有电表
//        List<DeviceTree> meterList = deviceTreeCache.getCascadeSubordinate(Integer.parseInt(deviceTreeId));
        List<AthenaElectricMeter> meterList = busMapper.selectMeterByBus(deviceTreeId);

        try {
            //修改总线表信息
            boolean busFlag = busMapper.updateBus(bus);
            if (!busFlag){
                throw new Exception();
            }

            if(meterList.size() > 0){
                //修改电表端口
                boolean meterFlag = busMapper.updateMeterPort(meterList,port);
                if (!meterFlag){
                    throw new Exception();
                }
            }
        } catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("修改失败");
        }

        //总线缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus,bus.getDeviceTreeId(),bus);

        //电表缓存 同步下发
        if (meterList.size() > 0){

            meterList.forEach(item ->{
                //修改端口号
                item.setCommPort(Long.parseLong(port));

                //重新加载缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter,item.getDeviceTreeId(),item);
                //电表同步下发
                meterService.syncMeter(item);
            });
        }

        //从表中查出树信息
        DeviceTree deviceTreeNode = busMapper.selectDeviceTreeByDeviceTreeId(bus.getDeviceTreeId().toString());


        return AjaxResult.success("修改成功",deviceTreeNode);

    }

    /**
     * 批量删除总线
     *
     * @param deviceTreeIds 需要删除的总线主键
     * @return 结果
     */
    @Override
    public int deleteBusByDeviceTreeIds(String[] deviceTreeIds)
    {
        return busMapper.deleteBusByDeviceTreeIds(deviceTreeIds);
    }

    /**
     * 删除总线信息
     *
     * @param deviceTreeId 总线主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteBusByDeviceTreeId(String deviceTreeId)
    {
        //查询下面有无电表
        List<DeviceTree> meterList = busMapper.queryMeterList(deviceTreeId);
        if (meterList.size() > 0){
            return AjaxResult.error("该总线下已配置电表,请先删除电表!");
        }
        //从设备树表中删除
        busMapper.deleteDeviceTree(deviceTreeId);
        //从总线表中删除
        boolean flag = busMapper.deleteBusByDeviceTreeId(deviceTreeId);
        if (flag){
            //重载缓存
            addBusCache();
        }
        return AjaxResult.success("删除成功");
    }
}
