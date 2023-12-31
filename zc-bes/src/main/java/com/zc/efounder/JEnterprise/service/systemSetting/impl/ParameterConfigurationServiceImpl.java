package com.zc.efounder.JEnterprise.service.systemSetting.impl;

import java.util.*;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.systemSetting.ParameterConfiguration;
import com.zc.efounder.JEnterprise.domain.systemSetting.ParamsConfiguration;
import com.zc.efounder.JEnterprise.mapper.systemSetting.ParameterConfigurationMapper;
import com.zc.efounder.JEnterprise.service.systemSetting.ParameterConfigurationService;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.ruoyi.common.utils.SecurityUtils.getUsername;

/**
 * 主采集参数Service业务层处理
 *
 * @author gaojikun
 * @date 2022-11-30
 */
@Service
public class ParameterConfigurationServiceImpl implements ParameterConfigurationService {
    @Autowired
    private ParameterConfigurationMapper parameterConfigurationMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:id
     * @Description:获取主采集参数详细信息
     * @Return:ParameterConfiguration
     */
    @Override
    public ParameterConfiguration selectAthenaBesParamsById(Long id) {
        return parameterConfigurationMapper.selectAthenaBesParamsById(id);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:parameterConfiguration
     * @Description:查询主采集参数列表
     * @Return:TableDataInfo
     */
    @Override
    public List<ParameterConfiguration> selectAthenaBesParamsList(ParameterConfiguration parameterConfiguration) {
        return parameterConfigurationMapper.selectAthenaBesParamsList(parameterConfiguration);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:parameterConfiguration
     * @Description:新增主采集参数
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult insertAthenaBesParams(ParameterConfiguration parameterConfiguration) {
        parameterConfiguration.setCreateBy(getUsername());
        parameterConfiguration.setCreateTime(DateUtils.getNowDate());
        boolean isAdd = parameterConfigurationMapper.insertAthenaBesParams(parameterConfiguration);
        if (isAdd) {
            return AjaxResult.success("添加成功");
        } else {
            return AjaxResult.error("添加失败");
        }
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:parameterConfiguration
     * @Description:修改主采集参数
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult updateAthenaBesParams(ParameterConfiguration parameterConfiguration) {
        if (parameterConfiguration.getId() == null) {
            return AjaxResult.error("参数错误！");
        }
        parameterConfiguration.setUpdateBy(getUsername());
        parameterConfiguration.setUpdateTime(DateUtils.getNowDate());
        boolean isUpdate = parameterConfigurationMapper.updateAthenaBesParams(parameterConfiguration);
        if (isUpdate) {
            return AjaxResult.success("添加成功");
        } else {
            return AjaxResult.error("添加失败");
        }
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:ids
     * @Description:删除主采集参数
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult deleteAthenaBesParamsByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        int delNum = parameterConfigurationMapper.deleteAthenaBesParamsByIds(ids);
        if (delNum == 0) {
            return AjaxResult.error("删除失败");
        } else {
            return AjaxResult.success("删除成功");
        }
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:ids
     * @Description:删除主采集参数
     * @Return:int
     */
    @Override
    public int deleteAthenaBesParamsById(Long id) {
        return parameterConfigurationMapper.deleteAthenaBesParamsById(id);
    }


    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:查询选择采集参数列表
     * @Return:List<ElectricParams>
     */
    @Override
    public List<ElectricParams> listCheckList(ParamsConfiguration paramsConfiguration) {
        List<ElectricParams> returnList = new ArrayList<>();
        //查询关联表
        List<ParamsConfiguration> checkList = parameterConfigurationMapper.listCheckList(paramsConfiguration);

        if (checkList.size() > 0) {
            for (int i = 0; i < checkList.size(); i++) {
                if ("0".equals(checkList.get(i).getDeviceType())) {
                    ElectricParams electricParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, checkList.get(i).getParamsId());
                    if (paramsConfiguration.getKeywords() != null && !"".equals(paramsConfiguration.getKeywords())) {
                        if (electricParams.getName().equals(paramsConfiguration.getKeywords())) {
                            electricParams.setDeviceType("0");
                            returnList.add(electricParams);
                        }
                    } else {
                        electricParams.setDeviceType("0");
                        returnList.add(electricParams);
                    }
                } else {
                    ProductItemData p = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, checkList.get(i).getParamsId());
                    if (paramsConfiguration.getKeywords() != null && !"".equals(paramsConfiguration.getKeywords())) {
                        if (p.getItemDataId() == null && p.getName().equals(paramsConfiguration.getKeywords())) {
                            returnList.add(new ElectricParams(p.getId(), p.getName(), p.getEnergyName(), "1"));
                        }
                    } else {
                        if(p.getItemDataId() == null){
                            returnList.add(new ElectricParams(p.getId(), p.getName(), p.getEnergyName(), "1"));
                        }
                    }
                }

            }
        }


        return returnList;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:查询未选择采集参数列表
     * @Return:List<ElectricParams>
     */
    @Override
    public List<ElectricParams> listNoCheckList(ParamsConfiguration paramsConfiguration) {
        List<ElectricParams> returnList = new ArrayList<>();
        if ("0".equals(paramsConfiguration.getDeviceType())) {
            //获取缓存
            Collection<Object> values = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams).values();

            for (Object val : values) {
                ElectricParams e = (ElectricParams) val;
                //电能参数跳过
                if (e.getCode().equals(PointPowerParam.Point_Meter_Code)) {
                    continue;
                }
                if (paramsConfiguration.getKeywords() != null && !"".equals(paramsConfiguration.getKeywords())) {
                    if (e.getName().equals(paramsConfiguration.getKeywords())) {
                        e.setDeviceType("0");
                        returnList.add(e);
                    }
                } else {
                    e.setDeviceType("0");
                    returnList.add(e);
                }
            }
        } else {
            //获取数据项缓存
            Collection<Object> values = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
            for (Object val : values) {
                ProductItemData p = (ProductItemData) val;
                if (paramsConfiguration.getKeywords() != null && !"".equals(paramsConfiguration.getKeywords())) {
                    if (p.getItemDataId() == null && p.getName().equals(paramsConfiguration.getKeywords())) {
                        returnList.add(new ElectricParams(p.getId(), p.getName(), p.getEnergyName(), "1"));
                    }
                } else {
                    if (p.getItemDataId() == null) {
                        returnList.add(new ElectricParams(p.getId(), p.getName(), p.getEnergyName(), "1"));
                    }
                }
            }
        }

        //查询关联表
        List<ParamsConfiguration> checkList = parameterConfigurationMapper.listCheckList(paramsConfiguration);
        if (checkList.size() > 0 && returnList.size() > 0) {
            for (ParamsConfiguration p : checkList) {
                Iterator<ElectricParams> iterator = returnList.iterator();
                while (iterator.hasNext()) {
                    ElectricParams item = iterator.next();
                    if (item.getId().equals(p.getParamsId())) {
                        iterator.remove();
                    }
                }
            }
        }
        return returnList;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:全部选中采集参数
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult allCheckList(ParamsConfiguration paramsConfiguration) {
        //删除全部配置
        parameterConfigurationMapper.delAllConfig(paramsConfiguration);
        if ("0".equals(paramsConfiguration.getDeviceType())) {
            //全部添加
            Collection<Object> values = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams).values();

            for (Object val : values) {
                ElectricParams e = (ElectricParams) val;
                paramsConfiguration.setParamsId(e.getId());
                paramsConfiguration.setCreateBy(getUsername());
                paramsConfiguration.setCreateTime(DateUtils.getNowDate());
                boolean isAdd = parameterConfigurationMapper.insertConfig(paramsConfiguration);
                if (!isAdd) {
                    return AjaxResult.error("全选失败");
                }
            }
        } else {
            //全部添加
            Collection<Object> values = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_Product_ItemData).values();

            for (Object val : values) {
                ProductItemData p = (ProductItemData) val;
                paramsConfiguration.setParamsId(p.getId());
                paramsConfiguration.setCreateBy(getUsername());
                paramsConfiguration.setCreateTime(DateUtils.getNowDate());
                boolean isAdd = parameterConfigurationMapper.insertConfig(paramsConfiguration);
                if (!isAdd) {
                    return AjaxResult.error("全选失败");
                }
            }
        }

        return AjaxResult.success("全选成功");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:全部取消
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult allNoCheckList(ParamsConfiguration paramsConfiguration) {
        boolean isDel = parameterConfigurationMapper.delAllConfig(paramsConfiguration);
        if (isDel) {
            return AjaxResult.success("全部取消成功");
        } else {
            return AjaxResult.error("全部取消失败");
        }
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:47
     * @Param:paramsConfiguration
     * @Description:添加一条采集参数
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult insertParamConfigRlgl(ParamsConfiguration paramsConfiguration) {
        paramsConfiguration.setCreateBy(getUsername());
        paramsConfiguration.setCreateTime(DateUtils.getNowDate());
        boolean isAdd = parameterConfigurationMapper.insertConfig(paramsConfiguration);
        if (!isAdd) {
            return AjaxResult.error("添加失败");
        } else {
            return AjaxResult.success("添加成功");
        }
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:取消一条采集参数
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult delParamConfigRlgl(ParamsConfiguration paramsConfiguration) {
        if (paramsConfiguration.getParamsId() == null || paramsConfiguration.getParamId() == null) {
            return AjaxResult.error("参数错误");
        }
        boolean isDel = parameterConfigurationMapper.delParamConfig(paramsConfiguration);
        if (isDel) {
            return AjaxResult.success("取消成功");
        } else {
            return AjaxResult.error("取消失败");
        }
    }
}
