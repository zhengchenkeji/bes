package com.zc.efounder.JEnterprise.service.baseData;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.baseData.Category;

import java.util.List;

/**
 * 品类Service接口
 *
 * @author sunshangeng
 * @date 2023-03-06
 */
public interface CategoryService
{
    /**
     * 查询品类
     *
     * @param id 品类主键
     * @return 品类
     */
    public Category selectCategoryById(Long id);

    /**
     * 查询品类列表
     *
     * @param Category 品类
     * @return 品类集合
     */
     List<Category> selectCategoryList(Category Category);

    /**
     * 新增品类
     *
     * @param Category 品类
     * @return 结果
     */
    AjaxResult insertCategory(Category Category);

    /**
     * 修改品类
     *
     * @param Category 品类
     * @return 结果
     */
    AjaxResult updateCategory(Category Category);

    /**
     * 批量删除品类
     *
     * @param ids 需要删除的品类主键集合
     * @return 结果
     */
    AjaxResult deleteCategoryByIds(Long[] ids);

    /**
     * 删除品类信息
     *
     * @param id 品类主键
     * @return 结果
     */
    int deleteCategoryById(Long id);
}
