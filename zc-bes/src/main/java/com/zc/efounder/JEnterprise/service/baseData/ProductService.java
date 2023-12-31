package com.zc.efounder.JEnterprise.service.baseData;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.baseData.Agreement;
import com.zc.efounder.JEnterprise.domain.baseData.Product;
import com.zc.efounder.JEnterprise.domain.baseData.ProductFunction;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.deviceTree.PointControlCommand;

import java.util.List;
import java.util.Map;

/**
 * 产品定义Service接口
 *
 * @author gaojikun
 * @date 2023-03-07
 */
public interface ProductService {
    /**
     * 查询产品定义
     *
     * @param id 产品定义主键
     * @return AjaxResult
     */
    public AjaxResult selectAthenaBesProductById(Long id);

    /**
     * 查询产品数据项
     *
     * @param id 产品数据项主键
     * @return AjaxResult
     */
    public AjaxResult selectAthenaBesProductItemDataById(Long id);

    /**
     * 查询产品定义列表
     *
     * @param product 产品定义
     * @return 产品定义集合
     */
    List<Product> selectAthenaBesProductList(Product product);

    /**
     * @Author:gaojikun
     * @Date:2023-03-08 16:03
     * @Description:查询所有产品
     * @Return:AjaxResult
     */
    AjaxResult selectAllProductList(Product product);

    /**
     * 查询产品数据项列表
     *
     * @param productItemData 产品数据项
     * @return 产品数据项集合
     */
    List<ProductItemData> selectAthenaBesProductItemDataList(ProductItemData productItemData);

    /**根据设备id查询数据项*/
    List<ProductItemData> getProductItemDataListByEqId(Long ModId);

    /**下发值*/
    AjaxResult debugpoint(PointControlCommand pointControlCommand);



    /**下发值*/
    AjaxResult debugDevice(Long  deviceId,String  funCode);
    /**
     * 新增产品定义
     *
     * @param product 产品定义
     * @return AjaxResult
     */
    AjaxResult insertAthenaBesProduct(Product product);

    /**
     * 新增产品数据项
     *
     * @param productItemData 产品数据项
     * @return AjaxResult
     */
    AjaxResult insertAthenaBesProductItemData(ProductItemData productItemData);

    /**
     * 修改产品定义
     *
     * @param product 产品定义
     * @return AjaxResult
     */
    AjaxResult updateAthenaBesProduct(Product product);

    /**
     * 修改产品配置-数据项
     *
     * @param productItemData 产品配置-数据项
     * @return AjaxResult
     */
    AjaxResult updateAthenaBesProductItemData(ProductItemData productItemData);

    /**
     * 批量删除产品定义
     *
     * @param ids 需要删除的产品定义主键集合
     * @return AjaxResult
     */
    AjaxResult deleteAthenaBesProductByIds(Long[] ids);

    /**
     * 批量删除产品配置-数据项
     *
     * @param ids 需要删除的产品配置-数据项主键集合
     * @return AjaxResult
     */
    AjaxResult deleteAthenaBesProductItemDataByIds(Long[] ids);

    /**
     * 查询数据类型list
     *
     * @return List<Map < String, Object>>
     */
    List<Map<String, Object>> getAllIotTypeList(String dictType, String id);

    /**
     * 查询物联类型/消息协议list
     */
    List<Map<String, Object>> getAllMessageIdList(String dictType, String id);

    /**
     * 查询对应的消息协议list
     */
    AjaxResult getMessageListById(String id);

    /**
     * 查询产品配置-功能定义
     *
     * @param id 产品配置-功能定义主键
     * @return AjaxResult
     */
    public AjaxResult selectAthenaBesProductFunctionById(Long id);

    /**
     * 查询产品配置-功能定义列表
     *
     * @param productFunction 产品配置-功能定义
     * @return 产品配置-功能定义集合
     */
    List<ProductFunction> selectAthenaBesProductFunctionList(ProductFunction productFunction);

    /**
     * 新增产品配置-功能定义
     *
     * @param productFunction 产品配置-功能定义
     * @return AjaxResult
     */
    AjaxResult insertAthenaBesProductFunction(ProductFunction productFunction);

    /**
     * 修改产品配置-功能定义
     *
     * @param productFunction 产品配置-功能定义
     * @return AjaxResult
     */
    AjaxResult updateAthenaBesProductFunction(ProductFunction productFunction);

    /**
     * 批量删除产品配置-功能定义
     *
     * @param ids 需要删除的产品配置-功能定义主键集合
     * @return AjaxResult
     */
    AjaxResult deleteAthenaBesProductFunctionByIds(Long[] ids);

    /**
     * @description:根据产品id查询消息协议详情
     * @author: sunshangeng
     * @date: 2023/3/15 15:03
     * @param: [产品id]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult getAgreeMentByProductId(Long productId);

    /*
    查询所有能源类型
    */
    AjaxResult energyTypeList();

    /**
     * @Description:查询数据项及相应参数配置
     * @Author:gaojikun
     * @Return: AjaxResult
     */
    AjaxResult selectItemDataList(PointControlCommand pointControlCommand);

    /**
     * @Description:点位数据项是否含有读功能
     * @Author:gaojikun
     * @Return: AjaxResult
     */
    AjaxResult queryItemDataInfoByFunctionId(PointControlCommand pointControlCommand);
}
