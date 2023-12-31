package com.zc.efounder.JEnterprise.service.energyInfo.impl;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.Product;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBranchMeterLinkMapper;
import com.zc.efounder.JEnterprise.service.energyInfo.AthenaBranchMeterLinkService;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.awt.image.ImageWatched;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.FetchProfile;
import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支路电表关联Service业务层处理
 *
 * @author qindehua
 */
@Service
public class AthenaBranchMeterLinkServiceImpl implements AthenaBranchMeterLinkService {
    @Resource
    private AthenaBranchMeterLinkMapper athenaBranchMeterLinkMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ModuleAndPointCache moduleAndPointCache;


    @PostConstruct
    public void init() {
        /**
         * 添加数据到 redis 缓存
         */
        addCache();
        addEneryCache();
    }

    /**
     * 添加数据到 redis 缓存
     */
    public void addCache() {

        // 获取全部设备列表数据
        List<AthenaBranchMeterLink> linkList = athenaBranchMeterLinkMapper.selectAll();
        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);

        if (linkList == null || linkList.isEmpty()) {

            return;
        }

        // 添加 redis 缓存数据
        linkList.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, val.getId(), val);
        });
    }


    /**
     * 添加启动时读取支路能耗数据到 redis 缓存
     */
    public void addEneryCache() {
        // 获取全部设备列表数据
        Map<String, AthenaBranchMeterLink> branchMeterLinkMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);

        branchMeterLinkMap.values().stream()
                .filter(link->link.getDeviceType().equals("1"))
                .forEach(link->{
                    String[] params = link.getElectricParam().split(",");
                    Map<String, Map<String, Object>>  eneryCacheMap= redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData, link.getMeterId());

                    for (int i = 0; i < params.length; i++) {
                        Map<String ,Object> map=new HashMap<>();


                        if(eneryCacheMap==null){
                            /**如果当前设备下无缓存，进行添加*/
                            eneryCacheMap=new HashMap<>();
                            /**从数据库中获取最后一次更新的数据值*/
                            Map otherEnergy = athenaBranchMeterLinkMapper.selectBranchEneryCache(link.getMeterId() + "", params[i]);
                            if(otherEnergy!=null){
                                /**将数据项的值放入在当前设备器的缓存中*/
                                map.put("data",otherEnergy.get("data_value").toString());
                                eneryCacheMap.put(params[i]+"",map);
                            }
                        }else{
                            /**如果当前设备下有缓存，判断是否有当前点位缓存*/
                            /**获取数据项缓存*/
                             map = eneryCacheMap.get(params[i]);
                            if(map==null){
                                /**如果缓存中不存在当前点位*/
                                map=new HashMap<>();
                                /**从数据库中获取最后一次更新的数据值*/
                                Map otherEnergy = athenaBranchMeterLinkMapper.selectBranchEneryCache(link.getMeterId() + "", params[i]);
                                /**将数据项的值放入在当前设备器的缓存中*/
                                map.put("data", otherEnergy.get("data_value").toString());
                                eneryCacheMap.put(params[i]+"",map);

                            }


                        }

                    }
                    /**填充完毕的缓存重新加入缓存*/
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData,link.getMeterId(),eneryCacheMap);
                });
    }

    /**
     * 查询支路电表关联
     *
     * @param id 支路电表关联主键
     * @return 支路电表关联
     */
    @Override
    public AthenaBranchMeterLink selectAthenaBranchMeterLinkById(Long id) {
        return athenaBranchMeterLinkMapper.selectAthenaBranchMeterLinkById(id);
    }

    /**
     * 查询当前支路下电表列表
     *
     * @param branchId   支路ID
     * @param energyCode 能源编号
     * @return 支路电表关联
     */
    @Override
    public List<AthenaBranchMeterLink> selectAthenaBranchMeterLinkList(Long branchId, String energyCode) {
        //采集方案数组
        List<Long> collMethodCollection = new ArrayList<>();
        //采集方案下的采集参数
        Map<Long, List<ElectricParams>> map = new HashMap<>();
        //结果数据
        List<AthenaBranchMeterLink> athenaBranchMeterLinks = new ArrayList<>();


        /**********获取符合条件的   采集方案数据Ids*********/
        redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod).values().forEach(item -> {
            CollMethod collMethod = (CollMethod) item;
            if (collMethod.getEnergyCode().equals(energyCode)) {
                collMethodCollection.add((long) collMethod.getId());
            }
        });

        /*********获取符合条件的   采集方案及采集参数*********/
        collMethodCollection.forEach(data -> {
            // 采集方案下的采集参数
            List<ElectricParams> list = new ArrayList<>();

            redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl).values().forEach(item -> {
                ElectricCollRlgl electricCollRlgl = (ElectricCollRlgl) item;
                if (electricCollRlgl.getCollId() == data && "1".equals(electricCollRlgl.getStatisticalParam())) {
                    list.add(redisCache.getCacheMapValue(
                            RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, (long) electricCollRlgl.getElectricId()));
                }
            });
            //将采集方案 及放下采集参数 存入map
            map.put(data, list);
        });

        /**********根据采集方案数据  获取相应电表*********/
        redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink).values().forEach(item -> {
            AthenaBranchMeterLink athenaBranchMeterLink = (AthenaBranchMeterLink) item;
            if (athenaBranchMeterLink.getBranchId().equals(branchId)) {

                /**sunshangeng
                 * 处理第三方设备和点位以及电表的采集方案*/
                /**bes设备**/
                if ("0".equals(athenaBranchMeterLink.getDeviceType())) {
                    //点位
                    if ("1".equals(athenaBranchMeterLink.getType())) {
                        Point point = moduleAndPointCache.getPointByDeviceId(athenaBranchMeterLink.getDeviceTreeId());

                        if (point.getEnergyCode().equals(energyCode)) {
                            List<ElectricParams> list = new ArrayList<>();
                            ElectricParams e = new ElectricParams();
                            e.setCode(PointPowerParam.Point_Meter_Code);
                            e.setName(PointPowerParam.Point_Meter_Name);
                            list.add(e);
                            athenaBranchMeterLink.setElectricParamsList(list);
                            athenaBranchMeterLinks.add(athenaBranchMeterLink);
                        }
                    }
                    //电表
                    else {
                        for (Long data : map.keySet()) {
                            if (athenaBranchMeterLink.getCollectionMethodCode().equals(data)) {
                                athenaBranchMeterLink.setElectricParamsList(map.get(data));
                                athenaBranchMeterLinks.add(athenaBranchMeterLink);
                                continue;
                            }
                        }
                    }
                } else {
                    /**第三方设备**/
                    Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, athenaBranchMeterLink.getMeterId());
                    Long productId = equipment.getProductId();
                    Map<String, ProductItemData> itemList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);
                    List<ElectricParams> list = new ArrayList<>();

                    itemList.values().stream()
                            //过滤掉其他产品的数据项
                            .filter(itemData -> itemData.getProductId() == productId)
                            //过滤掉不需要保存的数据项
                            .filter(itemData -> "1".equals(itemData.getPreserveType()))
                            //过滤掉其他能源类型
                            .filter(itemData -> energyCode.equals(itemData.getEnergyCode()))
                            .forEach(itemData -> {
                                ElectricParams e = new ElectricParams();
//                                e.setId(itemData.getId());
                                e.setCode(itemData.getId() + "");
                                e.setName(itemData.getName());
                                list.add(e);

                            });
                    /**添加数据*/
                    athenaBranchMeterLink.setElectricParamsList(list);
                    /**添加第三方设备*/
                    athenaBranchMeterLink.setSysName(equipment.getId()+"");
                    athenaBranchMeterLink.setAlias(equipment.getName()+"");
                    athenaBranchMeterLinks.add(athenaBranchMeterLink);

                }
            }
        });
        return athenaBranchMeterLinks;
    }

    /**
     * 新增支路电表关联
     *
     * @param athenaBranchMeterLink 支路电表关联
     * @return 结果
     */
    @Override
    public int insertAthenaBranchMeterLink(AthenaBranchMeterLink athenaBranchMeterLink) {
        return athenaBranchMeterLinkMapper.insertAthenaBranchMeterLink(athenaBranchMeterLink);
    }

    /**
     * 修改支路电表关联
     *
     * @param athenaBranchMeterLink 支路电表关联
     * @return 结果
     */
    @Override
    public int updateAthenaBranchMeterLink(AthenaBranchMeterLink athenaBranchMeterLink) {
        return athenaBranchMeterLinkMapper.updateAthenaBranchMeterLink(athenaBranchMeterLink);
    }

    /**
     * 批量删除支路电表关联
     *
     * @param ids 需要删除的支路电表关联主键
     * @return 结果
     */
    @Override
    public boolean deleteAthenaBranchMeterLinkByIds(Long[] ids) {
        return athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkByIds(ids);
    }

    /**
     * 删除支路电表关联信息
     *
     * @param id 支路电表关联主键
     * @return 结果
     */
    @Override
    public int deleteAthenaBranchMeterLinkById(Long id) {
        return athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkById(id);
    }
}
