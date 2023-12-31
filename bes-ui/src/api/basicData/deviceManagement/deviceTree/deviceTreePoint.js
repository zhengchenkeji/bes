import request from '@/utils/request'

/******************************gaojikun***************************/
/******************************添加树节点***************************/
export function insertDeviceTreee(query) {
  return request({
    url: '/basicData/point/insertDeviceTreee',
    method: 'post',
    params: query
  })
}

/******************************修改树节点***************************/
export function updateDeviceTreee(query) {
  return request({
    url: '/basicData/point/updateDeviceTreee',
    method: 'post',
    params: query
  })
}

/******************************添加点位***************************/
export function insertPoint(query) {
  return request({
    url: '/basicData/point/insertPoint',
    method: 'post',
    params: query
  })
}

/******************************修改点位***************************/
export function updatePoint(query) {
  return request({
    url: '/basicData/point/updatePoint',
    method: 'post',
    params: query
  })
}

/******************************调试点位***************************/
export function debuggerPoint(data) {
  return request({
    url: '/basicData/point/debugPointInfo',
    method: 'get',
    params: data
  })
}

/******************************点值配置***************************/
export function debuggerEditPointValue(data) {
  return request({
    url: '/basicData/point/debuggerEditPointValue',
    method: 'get',
    params: data
  })
}

/******************************查询点值配置***************************/
export function selectEditPointValue(data) {
  return request({
    url: '/basicData/point/selectEditPointValue',
    method: 'get',
    params: data
  })
}

/******************************能源类型列表***************************/
export function listEnergy(query) {
  return request({
    url: '/basicData/energyType/allEnergyTypeList',
    method: 'get',
    params: query
  })
}

/******************************能源类型列表***************************/
export function listVpoint(query) {
  return request({
    url: '/basicData/point/listVpoint',
    method: 'get',
    params: query
  })
}

/******************************新增模块***************************/
export function insertModule(query) {
  return request({
    url: '/deviceTree/module/insertModule',
    method: 'post',
    params: query
  })
}

/******************************修改模块***************************/
export function updateModule(query) {
  return request({
    url: '/deviceTree/module/updateModule',
    method: 'get',
    params: query
  })
}

/******************************模块类型列表***************************/
export function listModuleType(query) {
  return request({
    url: '/basicData/moduleType/listModuleType',
    method: 'get',
    params: query
  })
}

/***************************************数据对比*************************************/
export function getDataInfoParam(data) {
  return request({
    url: '/deviceTree/module/getDataInfoParam',
    method: 'get',
    params: data
  })
}

/***************************************模块同步数据*************************************/
export function synchronizeModel(data) {
  return request({
    url: '/deviceTree/module/synchronizeModule',
    method: 'get',
    params: data
  })
}

/***************************************虚点同步数据*************************************/
export function synchronizeVpoint(data) {
  return request({
    url: '/deviceTree/module/synVirtualPoint',
    method: 'get',
    params: data
  })
}

/***************************************模块点同步数据*************************************/
export function synchronizeModelPoint(data) {
  return request({
    url: '/deviceTree/module/synchronizePoint',
    method: 'get',
    params: data
  })
}

/******************************照明添加子节点***************************/
export function insertLightingTree(query) {
  return request({
    url: '/basicData/point/insertLightingTree',
    method: 'post',
    params: query
  })
}

/******************************修改照明子节点***************************/
export function updateLightingTree(query) {
  return request({
    url: '/basicData/point/updateLightingTree',
    method: 'post',
    params: query
  })
}

/******************************点位批量控制***************************/
export function debugPointListInfo(query) {
  return request({
    url: '/basicData/point/debugPointListInfo',
    method: 'post',
    data:query
  })
}

/******************************点位获取实时值***************************/
export function debugPointRealTimeInfo(query) {
  return request({
    url: '/basicData/point/debugPointRealTimeInfo',
    method: 'get',
    params:query
  })
}

export function debugPointListRealTimeInfo(data) {
  return request({
    url: '/basicData/point/debugPointListRealTimeInfo',
    method: 'post',
    data:data
  })

}
