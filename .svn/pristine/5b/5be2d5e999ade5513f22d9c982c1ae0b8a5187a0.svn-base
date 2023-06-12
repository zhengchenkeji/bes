package com.zc.efounder.JEnterprise.service.systemSetting.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSeason;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceTime;
import com.zc.efounder.JEnterprise.mapper.systemSetting.ElectricityPriceSeasonMapper;
import com.zc.efounder.JEnterprise.service.systemSetting.ElectricityPriceSeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * 电价-季节范围Service业务层处理
 *
 * @author liuwenge
 * @date 2023-02-20
 */
@Service
public class ElectricityPriceSeasonServiceImpl implements ElectricityPriceSeasonService
{
    @Autowired
    private ElectricityPriceSeasonMapper electricityPriceSeasonMapper;
    @Resource
    private RedisCache redisCache;

    @PostConstruct
    public void init() {
        /**
         * 添加季节数据到 redis 缓存
         */
        addSeasonCache();

        /**
         * 添加时间数据到 redis 缓存
         */
        addTimeCache();


    }

    /**
     * 添加季节缓存
     */
    public void addSeasonCache() {
        // 获取全部数据
        List<ElectricityPriceSeason> electricityPriceSeasonList = electricityPriceSeasonMapper.selectAllSeason();
        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceSeason);

        if (electricityPriceSeasonList == null || electricityPriceSeasonList.isEmpty()) {

            return;
        }

        // 添加 redis 缓存数据
        electricityPriceSeasonList.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceSeason, val.getId(), val);
        });
    }
    /**
     * 添加时间缓存
     */
    public void addTimeCache() {
        // 获取全部数据
        List<ElectricityPriceTime> electricityPriceTimeList = electricityPriceSeasonMapper.selectAllTime();
        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceTime);

        if (electricityPriceTimeList == null || electricityPriceTimeList.isEmpty()) {

            return;
        }

        // 添加 redis 缓存数据
        electricityPriceTimeList.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceTime, val.getId(), val);
        });
    }

    /**
     * 查询电价-季节范围
     *
     * @author liuwenge
     * @param id 电价-季节范围主键
     * @return 电价-季节范围
     */
    @Override
    public ElectricityPriceSeason selectElectricityPriceSeasonById(Long id)
    {
        return redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceSeason,id);
    }

    /**
     * 查询电价-季节范围列表
     *
     * @author liuwenge
     * @param electricityPriceSeason 电价-季节范围
     * @return 电价-季节范围
     */
    @Override
    public List<ElectricityPriceSeason> selectElectricityPriceSeasonList(ElectricityPriceSeason electricityPriceSeason)
    {
        return electricityPriceSeasonMapper.selectElectricityPriceSeasonList(electricityPriceSeason);
    }

    /**
     * 新增电价-季节范围
     *
     * @author liuwenge
     * @param electricityPriceSeason 电价-季节范围
     * @return 结果
     */
    @Override
    public AjaxResult insertElectricityPriceSeason(ElectricityPriceSeason electricityPriceSeason)
    {
        String name = electricityPriceSeason.getName();
        String startDate = electricityPriceSeason.getStartDate().toString();
        String endDate = electricityPriceSeason.getEndDate().toString();
        if (!StringUtils.hasText(name) || !StringUtils.hasText(startDate) || !StringUtils.hasText(endDate)){
            return AjaxResult.error("新增失败,参数错误");
        }
        int rows = electricityPriceSeasonMapper.insertElectricityPriceSeason(electricityPriceSeason);
        if (rows > 0){
            //放入缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceSeason,electricityPriceSeason.getId(),electricityPriceSeason);

            return AjaxResult.success("新增成功");
        } else {
            return AjaxResult.error("新增失败");
        }
    }

    /**
     * 修改电价-季节范围
     *
     * @author liuwenge
     * @param electricityPriceSeason 电价-季节范围
     * @return 结果
     */
    @Override
    public AjaxResult updateElectricityPriceSeason(ElectricityPriceSeason electricityPriceSeason)
    {
        String id = electricityPriceSeason.getId().toString();
        String name = electricityPriceSeason.getName();
        String startDate = electricityPriceSeason.getStartDate().toString();
        String endDate = electricityPriceSeason.getEndDate().toString();
        if (!StringUtils.hasText(id)
                || !StringUtils.hasText(name)
                || !StringUtils.hasText(startDate)
                || !StringUtils.hasText(endDate)){
            return AjaxResult.error("修改失败,参数错误");
        }
        int rows = electricityPriceSeasonMapper.updateElectricityPriceSeason(electricityPriceSeason);
        if (rows > 0){

            //更新缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceSeason,electricityPriceSeason.getId(),electricityPriceSeason);

            return AjaxResult.success("修改成功");
        } else {
            return AjaxResult.error("修改失败");
        }
    }

    /**
     * 批量删除电价-季节范围
     *
     * @author liuwenge
     * @param ids 需要删除的电价-季节范围主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteElectricityPriceSeasonByIds(Long[] ids)
    {
        if (ids.length < 1){
            return AjaxResult.error("ID不允许为空！");
        }

        int rows = electricityPriceSeasonMapper.deleteElectricityPriceSeasonByIds(ids);
        if (rows > 0){

            //删除出该季节下面的分时电价
            electricityPriceSeasonMapper.deleteTimeOfUsePrice(ids);

            //删除缓存
            for (Long id : ids) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceSeason,id);
            }

            return AjaxResult.success("删除成功");
        } else {
            return AjaxResult.error("删除失败");
        }
    }

    /**
     * 删除电价-季节范围信息
     *
     * @author liuwenge
     * @param id 电价-季节范围主键
     * @return 结果
     */
    @Override
    public int deleteElectricityPriceSeasonById(Long id)
    {
        int rows = electricityPriceSeasonMapper.deleteElectricityPriceSeasonById(id);
        //删除缓存
        if (rows > 0){
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceSeason,id);
        }
        return rows;
    }
}
