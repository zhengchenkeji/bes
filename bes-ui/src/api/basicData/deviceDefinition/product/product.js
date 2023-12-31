import request from '@/utils/request'

/**************************************查询产品定义列表********************************************/
export function listProduct(query) {
  return request({
    url: '/baseData/product/list',
    method: 'get',
    params: query
  })
}

export function listAllProduct(query) {
  return request({
    url: '/baseData/product/getAllProduct',
    method: 'get',
    params: query
  })
}


/**************************************查询产品数据项列表********************************************/
export function listProductItemData(query) {
  return request({
    url: '/baseData/product/listItemData',
    method: 'get',
    params: query
  })
}
//不分页
export function listProductItemDataNoPaging(query) {
  return request({
    url: '/baseData/product/listItemDataNoPaging',
    method: 'get',
    params: query
  })
}

/**************************************查询产品配置-功能定义列表********************************************/
export function listProductFunction(query) {
  return request({
    url: '/baseData/product/listFunction',
    method: 'get',
    params: query
  })
}

/***************************************查询品类定义列表*******************************************/
export function listCategory(query) {
  return request({
    url: '/baseData/category/getAllCategorylist',
    method: 'get',
    params: query
  })
}

/**************************************查询数据类型列表********************************************/
export function listIotType(query) {
  return request({
    url: '/baseData/product/getAllIotTypeList',
    method: 'get',
    params: query
  })
}
export function listIotTypeId(query) {
  return request({
    url: '/baseData/product/getAllIotTypeIdList',
    method: 'get',
    params: query
  })
}

/**************************************查询通讯协议/设备类型列表********************************************/
export function getAllMessageIdList(query) {
  return request({
    url: '/baseData/product/getAllMessageIdList',
    method: 'get',
    params: query
  })
}
export function getAllMessageList(query) {
  return request({
    url: '/baseData/product/getAllMessageList',
    method: 'get',
    params: query
  })
}

/**************************************查询消息协议列表********************************************/
export function getMessageListById(query) {
  return request({
    url: '/baseData/product/getMessageListById',
    method: 'get',
    params: query
  })
}

/*****************************************查询产品定义详细*****************************************/
export function getProduct(id) {
  return request({
    url: '/baseData/product/' + id,
    method: 'get'
  })
}

/*****************************************查询产品数据项详细*****************************************/
export function getProductItemData(id) {
  return request({
    url: '/baseData/product/ItemData/' + id,
    method: 'get'
  })
}

/*****************************************查询产品配置-功能定义详细*****************************************/
export function getProductFunction(id) {
  return request({
    url: '/baseData/product/function/' + id,
    method: 'get'
  })
}

/*****************************************新增产品定义*****************************************/
export function addProduct(data) {
  return request({
    url: '/baseData/product',
    method: 'post',
    data: data
  })
}

/*****************************************新增产品数据项*****************************************/
export function addProductItemData(data) {
  return request({
    url: '/baseData/product/ItemData',
    method: 'post',
    data: data
  })
}

/*****************************************新增产品配置-功能定义*****************************************/
export function addProductFunction(data) {
  return request({
    url: '/baseData/product/function',
    method: 'post',
    data: data
  })
}

/****************************************修改产品定义******************************************/
export function updateProduct(data) {
  return request({
    url: '/baseData/product',
    method: 'put',
    data: data
  })
}

/****************************************修改产品数据项******************************************/
export function updateProductItemData(data) {
  return request({
    url: '/baseData/product/ItemData',
    method: 'put',
    data: data
  })
}

/****************************************修改产品配置-功能定义******************************************/
export function updateProductFunction(data) {
  return request({
    url: '/baseData/product/function',
    method: 'put',
    data: data
  })
}

/*****************************************删除产品定义*****************************************/
export function delProduct(id) {
  return request({
    url: '/baseData/product/' + id,
    method: 'delete'
  })
}

/*****************************************删除产品数据项*****************************************/
export function delProductItemData(id) {
  return request({
    url: '/baseData/product/ItemData/' + id,
    method: 'delete'
  })
}

/*****************************************删除产品配置-功能定义*****************************************/
export function delProductFunction(id) {
  return request({
    url: '/baseData/product/function/' + id,
    method: 'delete'
  })
}

/*****************************************删除产品数据项参数*****************************************/
export function delParamsDetail(id) {
  return request({
    url: '/baseData/product/delParamsDetail/' + id,
    method: 'delete'
  })
}

/*****************************************删除产品数据项枚举参数*****************************************/
export function delEnumParamsDetail(id) {
  return request({
    url: '/baseData/product/delEnumParamsDetail/' + id,
    method: 'delete'
  })
}

/*****************************************导出产品定义*****************************************/
export function exportProduct(query) {
  return request({
    url: '/baseData/product/export',
    method: 'get',
    params: query
  })
}

/*****************************************查询协议详情*****************************************/
export function getAgreeMentByProductId(ProductId) {
  return request({
    url: '/baseData/product/getAgreeMentByProductId?ProductId='+ ProductId,
    method: 'get'
  })
}

/*****************************************能源类型列表*****************************************/
export function listEnergy(query) {
  return request({
    url: '/baseData/product/energyTypeList',
    method: 'get',
    params: query
  })
}

/*****************************************查询按钮绑定的功能数据项*****************************************/
export function selectItemDataList(query) {
  return request({
    url: '/baseData/product/selectItemDataList',
    method: 'get',
    params: query
  })
}

/******************************点位数据项是否含有读功能***************************/
export function queryItemDataInfoByFunctionId(query){
  return request({
    url: '/baseData/product/queryItemDataInfoByFunctionId',
    method: 'get',
    params:query
  })
}


