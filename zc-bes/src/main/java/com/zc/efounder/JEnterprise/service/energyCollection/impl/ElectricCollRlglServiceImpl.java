package com.zc.efounder.JEnterprise.service.energyCollection.impl;

import java.util.*;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.mapper.energyCollection.ElectricParamsMapper;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.efounder.JEnterprise.mapper.energyCollection.CollMethodMapper;
import com.zc.efounder.JEnterprise.mapper.energyCollection.ElectricCollRlglMapper;
import com.zc.efounder.JEnterprise.service.energyCollection.ElectricCollRlglService;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBranchMeterLinkMapper;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;
import org.apache.commons.collections.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static com.ruoyi.common.core.domain.AjaxResult.error;

/**
 * 电能参数采集方案关系Service业务层处理
 *
 * @author gaojikun
 * @date 2022-09-09
 */
@Service
public class ElectricCollRlglServiceImpl implements ElectricCollRlglService {
    @Autowired
    private ElectricCollRlglMapper electricCollRlglMapper;

    @Autowired
    private AthenaBranchMeterLinkMapper athenaBranchMeterLinkMapper;

    @Autowired
    private CollMethodMapper collMethodMapper;

    @Autowired
    private ElectricParamsMapper electricParamsMapper;

    @Resource
    private RedisCache redisCache;

    @PostConstruct
    public void init() {
        /*
          添加数据到 redis 缓存
         */
        addElectricCollRlglCache();
    }

    /**
     * 添加数据到 redis 缓存
     */
    public void addElectricCollRlglCache() {
        // 获取全部采集参数定义列表数据
        List<ElectricCollRlgl> ElectricCollRlgls = selectElectricCollRlglList(null);
        // 清楚 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl);

        if (ElectricCollRlgls == null || ElectricCollRlgls.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        ElectricCollRlgls.forEach(val -> redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl, val.getId(), val));
    }

    /**
     * 查询电能参数采集方案关系
     *
     * @param electricCode 电能参数采集方案关系主键
     * @return 电能参数采集方案关系
     */
    @Override
    public ElectricCollRlgl selectElectricCollRlglByElectricCode(String electricCode) {
        return electricCollRlglMapper.selectElectricCollRlglByElectricCode(electricCode);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:查询电能参数采集方案关系列表
     * @Return:List<ElectricCollRlgl>
     */
    @Override
    public List<ElectricCollRlgl> selectElectricCollRlglList(ElectricCollRlgl electricCollRlgl) {
        return electricCollRlglMapper.selectElectricCollRlglList(electricCollRlgl);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:查询取消参数列表
     * @Return:List<ElectricParams>
     */
    @Override
    public List<ElectricParams> rlglNoCheckList(ElectricCollRlgl electricCollRlgl) {
        List<ElectricParams> EPList = new ArrayList<>();
        List<ElectricCollRlgl> ECRList = new ArrayList<>();
        //查询采集参数采集方案关联信息缓存
        Map<String, ElectricCollRlgl> ElectricCollRlglCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl);
        Collection<ElectricCollRlgl> list = ElectricCollRlglCacheMap.values();
        for (ElectricCollRlgl e : list) {
            if (e.getCollId() == electricCollRlgl.getCollId()) {
                ECRList.add(e);
            }
        }
        //通过采集方案编号，获取该条数据
        CollMethod CollM = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod, (long) electricCollRlgl.getCollId());
        String fNybh = CollM.getEnergyCode();
        //查询采集参数缓存
        Map<String, ElectricParams> ElectricParamsCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams);
        Collection<ElectricParams> ElectricParamss = ElectricParamsCacheMap.values();
        for (ElectricParams e : ElectricParamss) {
            //电能参数跳过
            if (e.getCode().equals(PointPowerParam.Point_Meter_Code)) {
                continue;
            }
            //是本园区 && 是该能源类型
            if (e.getParkCode().equals(electricCollRlgl.getParkCode()) && e.getEnergyCode().equals(fNybh)) {
                EPList.add(e);
            }
        }
        for (ElectricCollRlgl e1 : ECRList) {
//            for (ElectricParams e2 : EPList) {
            Iterator<ElectricParams> iterator = EPList.iterator();
            while (iterator.hasNext()) {
                ElectricParams item = iterator.next();
                //选中的
                if (item.getCode().equals(e1.getElectricCode())) {
                    //不包含关键词 || 不全等关键词
                    if (electricCollRlgl.getKeywords() != null && !"".equals(electricCollRlgl.getKeywords())) {
                        String Keywords = electricCollRlgl.getKeywords();
                        if ((!item.getName().contains(Keywords) || !item.getName().equals(Keywords))) {
                            iterator.remove();
                        }
                    } else {
                        iterator.remove();
                    }

                }
            }
//            }
        }
        return EPList;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:查询选中参数列表
     * @Return:List<ElectricParams>
     */
    @Override
    public List<ElectricParams> rlglCheckList(ElectricCollRlgl electricCollRlgl) {
        List<ElectricParams> EpList = new ArrayList<>();
        List<ElectricParams> newEpList = new ArrayList<>();
        //通过采集方案编号，获取该条数据
        CollMethod CollM = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod, (long) electricCollRlgl.getCollId());
        String fNybh = CollM.getEnergyCode();

        List<ElectricCollRlgl> ECRList = new ArrayList<>();
        List<ElectricParams> EpListResult = new ArrayList<>();
        //查询采集参数采集方案关联信息缓存
        Map<String, ElectricCollRlgl> ElectricCollRlglCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl);
        Collection<ElectricCollRlgl> list = ElectricCollRlglCacheMap.values();
        for (ElectricCollRlgl e : list) {
            if (e.getCollId() == electricCollRlgl.getCollId()) {
                ECRList.add(e);
            }
        }

        //查询采集参数缓存
        Map<String, ElectricParams> ElectricParamsCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams);
        Collection<ElectricParams> ElectricParamss = ElectricParamsCacheMap.values();
        a:
        for (int i = 0; i < ECRList.size(); i++) {
            ElectricParams EP = new ElectricParams();
            b:
            for (ElectricParams e : ElectricParamss) {
                //电能参数跳过
                if (e.getCode().equals(PointPowerParam.Point_Meter_Code)) {
                    continue b;
                }
                //属于该能源类型 && 属于该园区 && 属于该方案编号
                if (e.getEnergyCode().equals(fNybh) && e.getCode().equals(ECRList.get(i).getElectricCode())) {
                    if (e.getParkCode().equals(electricCollRlgl.getParkCode())) {
                        //关键字
                        if (electricCollRlgl.getKeywords() != null && !"".equals(electricCollRlgl.getKeywords())) {
                            //包含关键词 || 全等关键词
                            if (e.getName().contains(electricCollRlgl.getKeywords()) || e.getName().equals(electricCollRlgl.getKeywords())) {
                                EP = e;
                                break b;
                            } else {
                                continue a;
                            }
                        } else {
                            EP = e;
                            break b;
                        }
                    } else {
                        continue a;
                    }
                }
            }
            EP.setStatisticalParam(ECRList.get(i).getStatisticalParam());
            EP.setIsRate(ECRList.get(i).getIsRate());
            EP.setId(Long.parseLong(ECRList.get(i).getId()));
            EpList.add(EP);
        }
        return EpList;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:全部选中
     * @Return:boolean
     */
    @Override
    public boolean allCheckList(ElectricCollRlgl electricCollRlgl) {//全部添加
        Map<String, ElectricCollRlgl> ElectricCollRlglCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl);
        Collection<ElectricCollRlgl> allList = new ArrayList<>();
        if (ElectricCollRlglCacheMap.size() > 0) {
            allList = ElectricCollRlglCacheMap.values();
        }

        String Id = "0";
        //设置Id
        if (allList.size() > 0) {
            Id = getMaxBh(allList);
        }
        List<ElectricCollRlgl> ECRList = electricCollRlglMapper.selectElectricCollRlglList(electricCollRlgl);
        //通过采集方案编号，获取该条数据
        CollMethod CollM = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod, (long)electricCollRlgl.getCollId());
        String fNybh = CollM.getEnergyCode();
        //查询所有能耗参数
        ElectricParams electricParams = new ElectricParams();
        electricParams.setEnergyCode(fNybh);
        List<ElectricParams> EPList = electricParamsMapper.selectElectricParamsList(electricParams);

        for (int i = 0; i < ECRList.size(); i++) {
            for (int j = 0; j < EPList.size(); j++) {
                if (EPList.get(j).getCode().equals(ECRList.get(i).getElectricCode())) {
                    EPList.remove(j);
                }
            }
        }

        int collId = electricCollRlgl.getCollId();

        for (int i = 0; i < EPList.size(); i++) {
            Id = Integer.parseInt(Id) + 1 + "";
            electricCollRlgl.setId(Id);
            electricCollRlgl.setCollId(collId);
            electricCollRlgl.setElectricId(EPList.get(i).getId().intValue());
            electricCollRlgl.setElectricCode(EPList.get(i).getCode());
            electricCollRlgl.setStatisticalParam("1");
            electricCollRlgl.setCreateTime(DateUtils.getNowDate());
            electricCollRlgl.setUpdateTime(DateUtils.getNowDate());

            int isInsertElectricCollRlgl = electricCollRlglMapper.insertElectricCollRlgl(electricCollRlgl);
            electricCollRlgl.setId(Id);
            if (isInsertElectricCollRlgl > 0) {
                electricCollRlgl = electricCollRlglMapper.selectElectricCollRlglByCode(electricCollRlgl);
                // 添加到缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl, electricCollRlgl.getId(), electricCollRlgl);
            }
        }
        return true;
    }

    // 获取最大编号
    private String getMaxBh(Collection<ElectricCollRlgl> list) {
        int maxnybh = 0;
        for (ElectricCollRlgl e : list) {
            String sBh = e.getId();
            int iBh = Integer.parseInt(sBh);
            if (maxnybh < iBh) {
                maxnybh = iBh;
            }
        }

        return String.valueOf(maxnybh);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:全部取消
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult allNoCheckList(ElectricCollRlgl electricCollRlgl) {//全部删除
        if (electricCollRlgl.getCollCode() == null || StringUtils.isEmpty(electricCollRlgl.getCollCode())) {
            return AjaxResult.error("参数错误！");
        }
        List<ElectricCollRlgl> comList = new ArrayList<>();
        List<ElectricCollRlgl> isDeleteList = new ArrayList<>();

        //获取采集方案缓存
        Map<String, ElectricCollRlgl> ElectricCollRlglCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl);
        Collection<ElectricCollRlgl> electricCollRlglList = ElectricCollRlglCacheMap.values();
        for (ElectricCollRlgl e : electricCollRlglList) {
            if (e.getCollId() == electricCollRlgl.getCollId()) {
                comList.add(e);
            }
        }

        //获取支路电表缓存
        Map<String, AthenaBranchMeterLink> AthenaBranchMeterLinkCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
        Collection<AthenaBranchMeterLink> list = AthenaBranchMeterLinkCacheMap.values();

        //判断支路是否关联采集参数
        b:
        for (ElectricCollRlgl e : comList) {
            boolean isReal = false;
            a:
            for (AthenaBranchMeterLink a : list) {
                if (a.getElectricParam().equals(e.getElectricCode())) {
                    isReal = true;
                    break a;
                }
            }
            if (!isReal) {
                isDeleteList.add(e);
            }
        }
        if (isDeleteList.size() > 0) {
            List<String> strList = new ArrayStack();
            for (ElectricCollRlgl el : isDeleteList) {
                strList.add(el.getId());
            }
            String[] ids = strList.toArray(new String[]{});
            electricCollRlglMapper.removeAllById(ids);
            // 删除缓存根据采集方案编号
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl, ids);
        }
        return AjaxResult.success("取消成功！");

    }


    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:新增电能参数采集方案关系
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult insertElectricCollRlgl(ElectricCollRlgl electricCollRlgl) {
        Map<String, ElectricCollRlgl> ElectricCollRlglCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl);
        Collection<ElectricCollRlgl> allList = new ArrayList<>();
        if (ElectricCollRlglCacheMap.size() > 0) {
            allList = ElectricCollRlglCacheMap.values();
        }

        String Id = "0";
        //设置Id
        if (allList.size() > 0) {
            Id = Integer.parseInt(getMaxBh(allList)) + 1 + "";
        }
        electricCollRlgl.setId(Id);
        electricCollRlgl.setCreateTime(DateUtils.getNowDate());
        int isInsertElectricCollRlgl = electricCollRlglMapper.insertElectricCollRlgl(electricCollRlgl);
        if (isInsertElectricCollRlgl > 0) {
            // 添加到缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl, electricCollRlgl.getId(), electricCollRlgl);
        }
        return AjaxResult.success("新增成功！");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:删除电能参数
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult deleteElectricCollRlgl(ElectricCollRlgl electricCollRlgl) {
        if (electricCollRlgl.getElectricCode() == null || StringUtils.isEmpty(electricCollRlgl.getElectricCode())) {
            return AjaxResult.error("参数错误！");
        }
        //判断支路是否关联采集参数
        Map<String, AthenaBranchMeterLink> AthenaBranchMeterLinkCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
        Collection<AthenaBranchMeterLink> list = AthenaBranchMeterLinkCacheMap.values();
        for (AthenaBranchMeterLink a : list) {
            if (a.getElectricParam().equals(electricCollRlgl.getElectricCode())) {
                return AjaxResult.error("移除失败,该采集方案已被支路关联");
            }
        }
        int isDeleteElectricCollRlgl = electricCollRlglMapper.deleteElectricCollRlgl(electricCollRlgl);
        if (isDeleteElectricCollRlgl > 0) {
            // 删除当前记录缓存
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl, electricCollRlgl.getId());
            return AjaxResult.success("移除成功");
        }
        return AjaxResult.error("移除失败");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:修改电能参数
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult changeRlglVlaue(ElectricCollRlgl electricCollRlgl) {
        if (electricCollRlgl.getElectricCode() == null || StringUtils.isEmpty(electricCollRlgl.getElectricCode())) {
            return AjaxResult.error("参数错误！");
        }
        //判断支路是否关联采集参数
        Map<String, AthenaBranchMeterLink> AthenaBranchMeterLinkCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
        Collection<AthenaBranchMeterLink> list = AthenaBranchMeterLinkCacheMap.values();
        for (AthenaBranchMeterLink a : list) {
            if (a.getElectricParam().equals(electricCollRlgl.getElectricCode())) {
                return AjaxResult.error("修改失败,该采集方案已被支路关联");
            }
        }
        electricCollRlgl.setUpdateTime(DateUtils.getNowDate());
        ElectricCollRlgl electricCollRlglUpdate = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl, electricCollRlgl.getId());
        electricCollRlglUpdate.setStatisticalParam(electricCollRlgl.getStatisticalParam());
        electricCollRlglUpdate.setIsRate(electricCollRlgl.getIsRate());
        if ("0".equals(electricCollRlgl.getStatisticalParam())) {
            //获取电表支路关联ID
            AthenaBranchMeterLink link = new AthenaBranchMeterLink();
            Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink).values();
            for (Object obj : values) {
                AthenaBranchMeterLink itemLink = (AthenaBranchMeterLink) obj;
                if (itemLink.getCollectionMethodCode() == null) {
                    continue;
                } else if (itemLink.getCollectionMethodCode().intValue() == electricCollRlgl.getCollId()
                        && itemLink.getElectricParam().equals(electricCollRlgl.getElectricCode())) {
                    link = itemLink;
                    break;
                }
            }

            if (link.getId() != null) {
                //删除电表支路关联
                athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkById(link.getId());
                //删除缓存
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, link.getId());
            }
        }


        boolean isUpdateElectricCollRlgl = electricCollRlglMapper.updateElectricCollRlgl(electricCollRlglUpdate);
        if (isUpdateElectricCollRlgl) {
            // 更新当前记录缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl, electricCollRlglUpdate.getId(), electricCollRlglUpdate);
            return AjaxResult.success("修改成功");
        }
        return AjaxResult.error("修改失败");
    }
}
