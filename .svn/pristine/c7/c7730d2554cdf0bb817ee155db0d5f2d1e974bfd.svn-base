package com.zc.efounder.JEnterprise.service.systemSetting.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceRule;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSeason;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSetting;
import com.zc.efounder.JEnterprise.mapper.systemSetting.ElectricityPriceSettingMapper;
import com.zc.efounder.JEnterprise.service.systemSetting.ElectricityPriceSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static com.ruoyi.common.utils.SecurityUtils.getUsername;

/**
 * 电价设置Service业务层处理
 *
 * @author gaojikun
 * @date 2022-11-29
 */
@Service
public class ElectricityPriceSettingServiceImpl implements ElectricityPriceSettingService {
    @Autowired
    private ElectricityPriceSettingMapper electricityPriceSettingMapper;
    @Resource
    private RedisCache redisCache;

    @PostConstruct
    public void init() {
        /**
         * 添加电价设置数据到 redis 缓存
         */
        addElectricityPriceSetting();
    }

    /**
     * 添加季节缓存
     */
    public void addElectricityPriceSetting() {
        // 获取全部数据
        List<ElectricityPriceSetting> electricityPriceSettingList = electricityPriceSettingMapper.selectElectricityPriceSettingList(null);
        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPrice);

        if (electricityPriceSettingList == null || electricityPriceSettingList.isEmpty()) {

            return;
        }

        // 添加 redis 缓存数据
        electricityPriceSettingList.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPrice, val.getId(), val);
        });
    }

    /**
     * @param id
     * @return AjaxResult
     * @Description: 查询电价设置
     * @auther: gaojikun
     */
    @Override
    public AjaxResult selectElectricityPriceSettingById(Long id) {
        if (id == null) {
            return AjaxResult.error("参数错误");
        }
        ElectricityPriceSetting electricityPriceSetting = electricityPriceSettingMapper.selectElectricityPriceSettingById(id);
        return AjaxResult.success(electricityPriceSetting);
    }

    /**
     * @param electricityPriceSetting
     * @return List<ElectricityPriceSetting>
     * @Description: 查询电价设置列表
     * @auther: gaojikun
     */
    @Override
    public List<ElectricityPriceSetting> selectElectricityPriceSettingList(ElectricityPriceSetting electricityPriceSetting) {
//        if (electricityPriceSetting.getEndTime() != null) {
//            String date = updateDateType(electricityPriceSetting.getEndTime());
//            electricityPriceSetting.setEndTimeStr(date.substring(11));
//        }
//        if (electricityPriceSetting.getStartTime() != null) {
//            String date = updateDateType(electricityPriceSetting.getStartTime());
//            electricityPriceSetting.setStartTimeStr(date.substring(11));
//        }
        if (electricityPriceSetting.getMonthDate() != null) {
            String date = updateDateType(electricityPriceSetting.getMonthDate());
            electricityPriceSetting.setMonthDateStr(date.substring(0, 10));
        }
        return electricityPriceSettingMapper.selectElectricityPriceSettingList(electricityPriceSetting);
    }

    //转换时间
    private String updateDateType(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义新的日期格式
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * @param electricityPriceSetting
     * @return AjaxResult
     * @Description: 新增电价设置
     * @auther: gaojikun
     */
    @Override
    public AjaxResult insertElectricityPriceSetting(ElectricityPriceSetting electricityPriceSetting) {
        if (electricityPriceSetting.getAppPrice() == null || electricityPriceSetting.getCctPrice() == null ||
                electricityPriceSetting.getAscpappPrice() == null || electricityPriceSetting.getEtdpPrice() == null ||
                electricityPriceSetting.getGfsPrice() == null || electricityPriceSetting.getSpikePrice() == null ||
                electricityPriceSetting.getPeakPrice() == null || electricityPriceSetting.getFlatPrice() == null ||
                electricityPriceSetting.getValleyPrice() == null || electricityPriceSetting.getTroughPrice() == null) {
            return AjaxResult.error("参数错误");
        }

        List<ElectricityPriceSetting> checkList = electricityPriceSettingMapper.CheckList(electricityPriceSetting);
        boolean isCheck = false;
        if (checkList.size() > 0) {
            for (ElectricityPriceSetting e : checkList) {
//                isCheck = checkTime(e, electricityPriceSetting);
                isCheck = checkMonth(e, electricityPriceSetting);
                if (isCheck) {
                    break;
                }
            }
        }
        if (!isCheck) {
            electricityPriceSetting.setCreateBy(getUsername());
            electricityPriceSetting.setCreateTime(DateUtils.getNowDate());
            boolean isAdd = electricityPriceSettingMapper.insertElectricityPriceSetting(electricityPriceSetting);
            if (isAdd) {
                //放入缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPrice,electricityPriceSetting.getId(),electricityPriceSetting);
                return AjaxResult.success("添加成功");
            } else {
                return AjaxResult.error("添加失败");
            }
        } else {
            return AjaxResult.error("月份重复，请检查月份");
        }

    }

    //查询时间是否重复
    private boolean checkTime(ElectricityPriceSetting Pone, ElectricityPriceSetting Ptwo) {
        if ((Pone.getStartTime().getTime() < Ptwo.getStartTime().getTime() && Pone.getEndTime().getTime() > Ptwo.getStartTime().getTime())
                || (Ptwo.getStartTime().getTime() < Pone.getStartTime().getTime() && Ptwo.getEndTime().getTime() > Pone.getStartTime().getTime())
                || (Ptwo.getStartTime().getTime() <= Pone.getStartTime().getTime() && Ptwo.getEndTime().getTime() >= Pone.getEndTime().getTime())
                || (Pone.getStartTime().getTime() < Ptwo.getStartTime().getTime() && Pone.getEndTime().getTime() > Ptwo.getEndTime().getTime())
        ) {
            //有交集
            return true;
        } else {
            //无交集
            return false;
        }
    }

    //查询月份是否重复
    private boolean checkMonth(ElectricityPriceSetting Pone, ElectricityPriceSetting Ptwo) {
        if (Pone.getMonthDate().getTime() == Ptwo.getMonthDate().getTime()) {
            //月份相同
            return true;
        } else {
            //月份不同
            return false;
        }
    }


    /**
     * @param electricityPriceSetting
     * @return AjaxResult
     * @Description: 修改电价设置
     * @auther: gaojikun
     */
    @Override
    public AjaxResult updateElectricityPriceSetting(ElectricityPriceSetting electricityPriceSetting) {
        if (electricityPriceSetting.getId() == null ||
                electricityPriceSetting.getAppPrice() == null || electricityPriceSetting.getCctPrice() == null ||
                electricityPriceSetting.getAscpappPrice() == null || electricityPriceSetting.getEtdpPrice() == null ||
                electricityPriceSetting.getGfsPrice() == null || electricityPriceSetting.getSpikePrice() == null ||
                electricityPriceSetting.getPeakPrice() == null || electricityPriceSetting.getFlatPrice() == null ||
                electricityPriceSetting.getValleyPrice() == null || electricityPriceSetting.getTroughPrice() == null) {
            return AjaxResult.error("参数错误");
        }
        List<ElectricityPriceSetting> checkList = electricityPriceSettingMapper.CheckList(electricityPriceSetting);
        boolean isCheck = false;
        if (checkList.size() > 0) {
            for (ElectricityPriceSetting e : checkList) {
//                isCheck = checkTime(e, electricityPriceSetting);
                isCheck = checkMonth(e, electricityPriceSetting);
                if (isCheck) {
                    break;
                }
            }
        }
        if (!isCheck) {
            electricityPriceSetting.setUpadteBy(getUsername());
            electricityPriceSetting.setUpdateTime(DateUtils.getNowDate());
            boolean isEdit = electricityPriceSettingMapper.updateElectricityPriceSetting(electricityPriceSetting);
            if (isEdit) {
                //更新缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPrice,electricityPriceSetting.getId(),electricityPriceSetting);
                return AjaxResult.success("修改成功");
            } else {
                return AjaxResult.error("修改失败");
            }
        } else {
            return AjaxResult.error("月份重复，请检查月份");
        }

    }

    /**
     * @param ids
     * @return AjaxResult
     * @Description: 批量删除电价设置
     * @auther: gaojikun
     */
    @Override
    public AjaxResult deleteElectricityPriceSettingByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        int delNum = electricityPriceSettingMapper.deleteElectricityPriceSettingByIds(ids);
        if (delNum == 0) {
            return AjaxResult.error("删除失败");
        }
        //删除缓存
        for (Long id : ids) {
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPrice,id);
        }
        return AjaxResult.success("删除成功");
    }

    /**
     * @param id
     * @return AjaxResult
     * @Description: 删除电价设置信息
     * @auther: gaojikun
     */
    @Override
    public AjaxResult deleteElectricityPriceSettingById(Long id) {
        if (id == null) {
            return AjaxResult.error("参数错误");
        }
        int delNum = electricityPriceSettingMapper.deleteElectricityPriceSettingById(id);
        if (delNum == 0) {
            return AjaxResult.error("删除失败");
        }
        redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPrice,id);
        return AjaxResult.success("删除成功");
    }
}
