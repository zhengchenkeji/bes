import request from '@/utils/request'

/******************************查询支路拓扑配置列表***************************/

export function listConfig(query) {
  return request({
    url: '/branchConfig/config/list',
    method: 'get',
    params: query
  })
}

/******************************根据能源，园区编号查询支路列表***************************/

export function nodeListBranch(query) {
  return request({
    url: '/branchConfig/config/branchList',
    method: 'get',
    params: query
  })
}
/******************************查询能源类型列表***************************/

export function listType(params) {
  return request({
    url: '/basicData/energyType/energyTypeList',
    method: 'get',
    params: params,
  })
}
/******************************查询园区列表***************************/

export function listPark() {
  return request({
    url: '/basicData/parkInfo/parkList',
    method: 'get',
  })
}
/******************************查询建筑列表***************************/

export function buildingList(query) {
  return request({
    url: '/branchConfig/config/buildingList',
    method: 'get',
    params: query
  })
}
/******************************查询支路拓扑配置详细***************************/

export function getConfig(branchId) {
  return request({
    url: '/branchConfig/config/' + branchId,
    method: 'get'
  })
}
/******************************新增支路拓扑配置***************************/

export function addConfig(data) {
  return request({
    url: '/branchConfig/config',
    method: 'post',
    data: data
  })
}
/******************************修改支路拓扑配置***************************/

export function updateConfig(data) {
  return request({
    url: '/branchConfig/config',
    method: 'put',
    data: data
  })
}
/******************************删除支路拓扑配置***************************/

export function delConfig(branchId) {
  return request({
    url: '/branchConfig/config/' + branchId,
    method: 'delete'
  })
}
/******************************删除支路拓扑配置时  查询是否关联分项及分户***************************/

export function getMessage(branchId) {
  return request({
    url: '/branchConfig/config/getMessage/' + branchId,
    method: 'get'
  })
}
/******************************查询支路拓扑配置下拉树结构***************************/

export function treeSelect(query) {
  return request({
    url: '/branchConfig/config/treeSelect',
    method: 'get',
    params: query
  })
}
/******************************查询所有bes电表***************************/

export function listMeter(query) {
  return request({
    url: '/deviceTree/meter/listInfo',
    method: 'get',
    params: query
  })
}

/******************************查询所有第三方电表***************************/

export function getEquipmentListByBranch(query) {
  return request({
    url: '/baseData/equipment/getEquipmentListByBranch',
    method: 'get',
    params: query
  })
}
/******************************查询当前节点下电表***************************/

export function nodeListMeter(param) {
  return request({
    url: '/branchConfig/config/meterList',
    method: 'get',
    params: param
  })
}
/******************************包含设备***************************/

export function saveNodeListMeter(data) {
  return request({
    url: '/branchConfig/config/meter',
    method: 'put',
    data: data
  })
}

