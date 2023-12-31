package com.zc.efounder.JEnterprise.mapper.baseData;

import com.zc.efounder.JEnterprise.domain.baseData.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 产品定义Mapper接口
 *
 * @author gaojikun
 * @date 2023-03-07
 */
public interface ProductMapper {
    /**
     * 查询产品定义
     *
     * @param id 产品定义主键
     * @return Product
     */
    public Product selectAthenaBesProductById(Long id);

    /**
     * 查询产品数据项
     *
     * @param id
     * @return Product
     */
    public ProductItemData selectAthenaBesProductItemDataById(Long id);

    /**
     * 查询产品定义列表
     *
     * @param product 产品定义
     * @return List<Product>
     */
    List<Product> selectAthenaBesProductList(Product product);

    /**
     * @Author:gaojikun
     * @Date:2023-03-08 16:03
     * @Description:查询所有产品
     * @Return:List<Map<String,Object>>
     */
    List<Map<String, Object>> selectAllProductList(@Param("ids") List<Long> ids);

    /*查询非子设备Id*/
    List<Map<String, Object>> selectDictData(@Param("type") String type, @Param("label") String label);

    /*查询非子设备Id*/
    List<Map<String, Object>> selectDictDataOther(@Param("type") String type, @Param("label") String label);

    /**
     * 查询产品数据项列表
     *
     * @param productItemData 产品数据项
     * @return List<ProductIteamData>
     */
    List<ProductItemData> selectAthenaBesProductItemDataList(ProductItemData productItemData);

    List<ProductItemData> selectAthenaBesProductItemDataOtherParamsList(ProductItemData productItemData);

    List<ParamsItemData> selectAthenaBesProductItemDataParamsList(ProductItemData productItemData);

    /**
     * 查询产品配置-功能定义
     *
     * @param id 产品配置-功能定义主键
     * @return ProductFunction
     */
    public ProductFunction selectAthenaBesProductFunctionById(Long id);

    /**
     * 查重产品定义
     *
     * @param product 产品定义
     * @return List<Product>
     */
    List<Product> selectAthenaBesProductListCheck(Product product);

    /**
     * 查重产品数据项
     *
     * @param productItemData 产品数据项
     * @return List<ProductItemData>
     */
    List<ProductItemData> selectAthenaBesProductItemDataListCheck(ProductItemData productItemData);

    /**
     * 查重产品功能
     *
     * @param productFunction 产品数据项
     * @return List<ProductItemData>
     */
    List<ProductFunction> selectAthenaBesProductFunctionListCheck(ProductFunction productFunction);

    /**
     * 查询产品配置-功能定义列表
     *
     * @param productFunction 产品配置-功能定义
     * @return List<ProductFunction>
     */
    List<ProductFunction> selectAthenaBesProductFunctionList(ProductFunction productFunction);

    /**
     * 查询产品配置参数
     *
     * @param functionItemData 产品配置-功能定义
     * @return List<FunctionItemData>
     */
    List<FunctionItemData> selectProductFunctionParamsList(FunctionItemData functionItemData);

    /**
     * 新增产品定义
     *
     * @param product 产品定义
     * @return boolean
     */
    boolean insertAthenaBesProduct(Product product);

    /**
     * 新增产品数据项
     *
     * @param productItemData 产品数据项
     * @return boolean
     */
    boolean insertAthenaBesProductItemData(ProductItemData productItemData);

    /**
     * 新增数据项参数
     *
     * @param paramsItemData 产品数据项参数
     * @return boolean
     */
    //    boolean insertAthenaBesProductItemDataParams(@Param("lstDto") List<ParamsItemData> lstDto);
    boolean insertAthenaBesProductItemDataParams(ParamsItemData paramsItemData);

    /**
     * 修改数据项参数
     *
     * @param paramsItemData 产品数据项参数
     * @return boolean
     */
    boolean updateAthenaBesProductItemDataParams(ParamsItemData paramsItemData);

    /**
     * 新增产品配置-功能定义
     *
     * @param productFunction 产品配置-功能定义
     * @return boolean
     */
    boolean insertAthenaBesProductFunction(ProductFunction productFunction);

    /**
     * 新增产品功能参数
     *
     * @param functionItemData 产品功能参数
     * @return boolean
     */
    boolean insertAthenaBesProductFunctionParams(FunctionItemData functionItemData);

    /**
     * 修改产品功能参数
     *
     * @param functionItemData 产品功能参数
     * @return boolean
     */
    boolean updateAthenaBesProductFunctionParams(FunctionItemData functionItemData);

    /**
     * 修改产品定义
     *
     * @param product 产品定义
     * @return boolean
     */
    boolean updateAthenaBesProduct(Product product);

    /**
     * 修改产品数据项
     *
     * @param productItemData 产品数据项
     * @return boolean
     */
    boolean updateAthenaBesProductItemData(ProductItemData productItemData);

    /**
     * 修改产品配置-功能定义
     *
     * @param productFunction 产品配置-功能定义
     * @return boolean
     */
    boolean updateAthenaBesProductFunction(ProductFunction productFunction);

    /**
     * 删除产品定义
     *
     * @param id 产品定义主键
     * @return boolean
     */
    boolean deleteAthenaBesProductById(Long id);

    /**
     * 批量删除产品定义
     *
     * @param ids 需要删除的数据主键集合
     * @return boolean
     */
    boolean deleteAthenaBesProductByIds(Long[] ids);

    /**
     * 删除产品配置-数据项
     *
     * @param id 产品配置-数据项主键
     * @return boolean
     */
    boolean deleteAthenaBesProductItemDataById(Long id);

    /**
     * 批量删除产品配置-数据项
     *
     * @param ids 需要删除的数据主键集合
     * @return boolean
     */
    boolean deleteAthenaBesProductItemDataByIds(Long[] ids);

    List<FunctionItemData> selectFunctionItemDataByIds(@Param("array") List<ProductItemData> array);

    /**
     * 获取所有参数
     *
     * @param id 需要删除的数据项主键
     * @return boolean
     */
    List<ProductItemData> selectParamsItemDataById(Long id);

    List<ProductItemData>  selectParamsItemDataByIds(Long[] ids);

    /**
     * 删除产品数据项参数关联参数、枚举参数
     * @param id 数据项id
     */
    boolean deleteAthenaBesParamsItemDataById(Long id);

    /**
     * 删除产品数据项参数关联参数、枚举参数
     * @param id 数据项参数关联参数id
     */
    boolean deleteParamsItemDataById(Long id);

    /**
     * 删除产品数据项参数
     * @param id 数据项父id
     */
    boolean deleteAthenaBesParamsItemDataOtherById(Long id);

    /**
     * 删除产品数据项参数
     * @param id 数据项id
     */
    boolean deleteParamsItemDataOtherById(Long id);

    /**
     * 删除产品配置-功能定义
     *
     * @param id 产品配置-功能定义主键
     * @return boolean
     */
    boolean deleteAthenaBesProductFunctionById(Long id);

    /**
     * 批量删除产品配置-功能定义
     *
     * @param ids 需要删除的数据主键集合
     * @return boolean
     */
    boolean deleteAthenaBesProductFunctionByIds(Long[] ids);

    /**
     * 批量删除产品配置参数
     *
     * @param id 功能id
     * @return boolean
     */
    boolean deleteAthenaBesProductFunctionParamsById(Long id);

    /**
     * 删除产品配置参数
     *
     * @param id 数据项id
     * @return boolean
     */
    boolean deleteProductFunctionParamsById(Long id);

    /**
     * 删除产品配置参数
     *
     * @param id 功能参数id
     * @return boolean
     */
    boolean deleteProductFunctionParamById(Long id);

    /**
     * 查询数据类型list
     *
     * @return List<Map < String, Object>>
     */
    List<Map<String, Object>> getAllIotTypeList(@Param("dictType") String dictType, @Param("id") String id);

    /**
     * 查询物联类型/消息协议list
     */
    List<Map<String, Object>> getAllMessageIdList(@Param("dictType") String dictType, @Param("id") String id);

    List<Map<String, Object>> energyTypeList();


}
