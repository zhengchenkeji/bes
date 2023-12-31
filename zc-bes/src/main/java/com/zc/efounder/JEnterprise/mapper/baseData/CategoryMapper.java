package com.zc.efounder.JEnterprise.mapper.baseData;

import com.zc.efounder.JEnterprise.domain.baseData.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 品类Mapper接口
 *
 * @author sunshangeng
 * @date 2023-03-06
 */
public interface CategoryMapper
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
    Boolean insertCategory(Category Category);

    /**
     * 修改品类
     *
     * @param Category 品类
     * @return 结果
     */
    Boolean updateCategory(Category Category);

    /**
     * 删除品类
     *
     * @param id 品类主键
     * @return 结果
     */
    int deleteCategoryById(Long id);

    /**
     * 批量删除品类
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    Boolean deleteCategoryByIds(Long[] ids);
    /**
     * @description:根据标识名称查询是否重复
     * @author: sunshangeng
     * @date: 2023/3/7 11:19
     * @param: [categoryName]
     * @return: int
     **/
    int selectCategoryByCategoryName(@Param("categoryName") String categoryName,@Param("categoryId")Long categoryId);
    /**
     * @description:根据品类标识查询是否重复
     * @author: sunshangeng
     * @date: 2023/3/7 11:19
     * @param: [categoryMark]
     * @return: int
     **/
    int selectCategoryByCategoryMark(@Param("categoryMark") String categoryMark,@Param("categoryId")Long categoryId);

}
