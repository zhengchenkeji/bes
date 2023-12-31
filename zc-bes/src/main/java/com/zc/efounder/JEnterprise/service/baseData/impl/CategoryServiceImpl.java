package com.zc.efounder.JEnterprise.service.baseData.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.domain.baseData.Category;
import com.zc.efounder.JEnterprise.domain.baseData.Product;
import com.zc.efounder.JEnterprise.mapper.baseData.CategoryMapper;
import com.zc.efounder.JEnterprise.service.baseData.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 品类Service业务层处理
 *
 * @author sunshangeng
 * @date 2023-03-06
 */
@Service
public class CategoryServiceImpl implements CategoryService
{
    @Resource
    private CategoryMapper CategoryMapper;


    @Resource
    private RedisCache redisCache;

    /**
     * 查询品类
     *
     * @param id 品类主键
     * @return 品类
     */
    @Override
    public Category selectCategoryById(Long id)
    {
        return CategoryMapper.selectCategoryById(id);
    }

    /**
     * 查询品类列表
     *
     * @param Category 品类
     * @return 品类
     */
    @Override
    public List<Category> selectCategoryList(Category Category)
    {
        return CategoryMapper.selectCategoryList(Category);
    }

    /**
     * 新增品类
     *
     * @param Category 品类
     * @return 结果
     */
    @Override
    public AjaxResult insertCategory(Category Category)
    {

        int num=CategoryMapper.selectCategoryByCategoryName(Category.getCategoryName(),null);
        if(num>0){
            return  AjaxResult.error("新增失败,品类名称不能重复");

        }
        num=CategoryMapper.selectCategoryByCategoryMark(Category.getCategoryMark(),null);
        if(num>0){
            return  AjaxResult.error("新增失败,品类标识不能重复");

        }
        /**获取当前用户名*/
        String username = SecurityUtils.getUsername();
        Category.setCreateBy(username);
        Category.setCreateTime(DateUtils.getNowDate());
        Boolean insertCategory = CategoryMapper.insertCategory(Category);
        if(!insertCategory){
            return  AjaxResult.error("新增失败");
        }
        return AjaxResult.success("新增成功");
    }

    /**
     * 修改品类
     *
     * @param Category 品类
     * @return 结果
     */
    @Override
    public AjaxResult updateCategory(Category Category)
    {
        String username = SecurityUtils.getUsername();
        Category.setUpdateBy(username);

        int num=CategoryMapper.selectCategoryByCategoryName(Category.getCategoryName(),Category.getId());
        if(num>0){
            return  AjaxResult.error("修改失败,品类名称不能重复");

        }
        num=CategoryMapper.selectCategoryByCategoryMark(Category.getCategoryMark(),Category.getId());
        if(num>0){
            return  AjaxResult.error("修改失败,品类标识不能重复");

        }
        Category.setUpdateTime(DateUtils.getNowDate());
        Boolean updateCategory = CategoryMapper.updateCategory(Category);

        if(!updateCategory){
            return  AjaxResult.error("修改失败,品类标识不能重复");
        }
        return AjaxResult.success("修改成功");
    }

    /**
     * 批量删除品类
     *
     * @param ids 需要删除的品类主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteCategoryByIds(Long[] ids)
    {

        Map<String, Product> productCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product);
        List<Long> list = Arrays.asList(ids);

        List<Product> products = productCache.values().stream().filter(item -> list.contains(item.getCategoryId())).collect(Collectors.toList());
        if(products==null||products.size()>0){

            return  AjaxResult.error("请删除关联的产品信息！");
        }else{
            Boolean del = CategoryMapper.deleteCategoryByIds(ids);
            if(!del){
                return AjaxResult.error("删除失败");
            }
        }
        return  AjaxResult.success();
    }

    /**
     * 删除品类信息
     *
     * @param id 品类主键
     * @return 结果
     */
    @Override
    public int deleteCategoryById(Long id)
    {
        return CategoryMapper.deleteCategoryById(id);
    }
}
