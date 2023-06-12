package com.zc.efounder.JEnterprise.service.baseData.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.domain.baseData.Agreement;
import com.zc.efounder.JEnterprise.domain.baseData.Product;
import com.zc.efounder.JEnterprise.mapper.baseData.AgreementMapper;
import com.zc.efounder.JEnterprise.mapper.baseData.ProductMapper;
import com.zc.efounder.JEnterprise.service.baseData.AgreementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 设备协议Service业务层处理
 *
 * @author sunshangeng
 * @date 2023-03-14
 */
@Service
public class AgreementServiceImpl  implements AgreementService {
    @Resource
    private AgreementMapper agreementMapper;

    @Resource
    private RedisCache redisCache;


    @Resource
    private ProductMapper productMapper;
    /**
     * 查询设备协议
     *
     * @param id 设备协议主键
     * @return 设备协议
     */

    /**
     * @description:初始化缓存
     * @author: sunshangeng
     * @date: 2023/3/14 15:04
     * @param: []
     * @return: void
     **/
//    @PostConstruct
//    public void init(){
//        /**清除缓存*/
//        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_Agreement);
//        /**存储缓存*/
//        List<Agreement> agreements = agreementMapper.selectAgreementList(null);
//        agreements.forEach(item->{
//            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Agreement,item.getId(),item);
//        });
//    }
    @Override
    public Agreement selectAgreementById(Long id) {
        return agreementMapper.selectAgreementById(id);
    }

    /**
     * 查询设备协议列表
     *
     * @param agreement 设备协议
     * @return 设备协议
     */
    @Override
    public List<Agreement> selectAgreementList(Agreement agreement) {
        return agreementMapper.selectAgreementList(agreement);
    }

    /**
     * 新增设备协议
     *
     * @param agreement 设备协议
     * @return 结果
     */
    @Override
    @Transactional
    public AjaxResult insertAgreement(Agreement agreement) {
        agreement.setCreateTime(DateUtils.getNowDate());
        Boolean save = agreementMapper.insertAgreement(agreement);
        /**处理缓存*/
        if (save) {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Agreement, agreement.getId(), agreement);
        }
        return AjaxResult.success();

    }

    /**
     * 修改设备协议
     *
     * @param agreement 设备协议
     * @return 结果
     */
    @Override
    public AjaxResult updateAgreement(Agreement agreement) {
        agreement.setUpdateTime(DateUtils.getNowDate());
        Boolean update = agreementMapper.updateAgreement(agreement);
        /**处理缓存*/
        if (update) {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Agreement, agreement.getId(), agreement);
        }
        return AjaxResult.success();
    }

    /**
     * 批量删除设备协议
     *
     * @param ids 需要删除的设备协议主键
     * @return 结果
     */
    @Override
    @Transactional
    public AjaxResult deleteAgreementByIds(Long[] ids, Integer check) {

        List<Product> productList=new ArrayList<>();
        for (Long id : ids) {
            Agreement agreement = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Agreement, id);
            if (agreement.getAgreementType() == 1) {
                /**匹配是否有使用中的*/
                Map<String, Product> productCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product);
                productList = productCache.values().stream()
                        .filter(item -> id.equals(item.getMessageProtocol()))
                        .collect(Collectors.toList());

                if (productList != null && productList.size() > 0) {
                    /**返回二次弹窗*/
                    if(check==0){
                        return AjaxResult.error(HttpStatus.ASSOCIATED_DATA, agreement.getAgreementName());
                    }
//                    else{
//
//                        productList.forEach(item->{
//                            /**删除产品*/
//                            boolean delproduct = productMapper.deleteAthenaBesProductById(item.getId());
//                            /**删除数据项*/
//                            boolean delproduct = productMapper.del
//
//                            /**删除功能定义*/
//                            boolean delproduct = productMapper.deleteAthenaBesProductById(item.getId());
//
//                        });
//
//                    }
                }
            }
        }


//        if (1 == 1) {
//            return AjaxResult.success("匹配成功！");
//        }


        int delnum = agreementMapper.deleteAgreementByIds(ids);
        /**处理缓存*/
        if (delnum > 0) {
            for (Long id : ids) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Agreement, id);
            }
        }
        return AjaxResult.success();
    }

    /**
     * 删除设备协议信息
     *
     * @param id 设备协议主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteAgreementById(Long id) {
        return deleteAgreementByIds(new Long[]{id},null);
    }
}
