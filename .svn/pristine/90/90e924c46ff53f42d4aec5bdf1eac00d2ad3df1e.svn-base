import request from '@/utils/request'

/******************************查询能源类型列表***************************/
export function listType(query) {
  return request({
    url: '/basicData/energyType/list',
    method: 'get',
    params: query
  })
}

/******************************查询能源类型详细***************************/
export function getType(guid) {
  return request({
    url: '/basicData/energyType/' + guid,
    method: 'get'
  })
}

/******************************新增能源类型***************************/
export function addType(data) {
  return request({
    url: '/basicData/energyType',
    method: 'post',
    data: data
  })
}

/******************************修改能源类型***************************/
export function updateType(data) {
  return request({
    url: '/basicData/energyType',
    method: 'put',
    data: data
  })
}

/******************************删除能源类型***************************/
export function delType(guid) {
  return request({
    url: '/basicData/energyType/' + guid,
    method: 'delete'
  })
}

/******************************导出能源类型***************************/
export function exportType(query) {
  return request({
    url: '/basicData/energyType/export',
    method: 'get',
    params: query
  })
}

/******************************园区能源配置左侧树***************************/
export function leftTree(query) {
  return request({
    url: '/bas/common/energyConfig/leftTree',
    method: 'get',
    params: query
  })
}

/******************************园区能源配置列表***************************/
export function listConfig(query) {
  return request({
    url: '/basicData/energyConfig/list',
    method: 'get',
    params: query
  })
}

/******************************保存园区能源配置***************************/
export function addConfig(data) {
  return request({
    url: '/basicData/energyConfig/add',
    method: 'post',
    data: data
  })
}

/******************************保存园区能源配置***************************/
export function getAllList() {
  return request({
    url: '/basicData/energyType/energyTypeList',
    method: 'get'
  })
}

