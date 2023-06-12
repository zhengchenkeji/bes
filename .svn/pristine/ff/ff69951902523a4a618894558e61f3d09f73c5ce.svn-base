import request from '@/utils/request'

/******************************查询采集参数列表***************************/
export function listParams(query) {
  return request({
    url: '/basicData/acquisitionParam/list',
    method: 'get',
    params: query
  })
}

/******************************查询采集参数详细***************************/
export function getParams(id) {
  return request({
    url: '/basicData/acquisitionParam/' + id,
    method: 'get'
  })
}

/******************************新增采集参数***************************/
export function addParams(data) {
  return request({
    url: '/basicData/acquisitionParam',
    method: 'post',
    data: data
  })
}

/******************************修改采集参数***************************/
export function updateParams(data) {
  return request({
    url: '/basicData/acquisitionParam',
    method: 'put',
    data: data
  })
}

/******************************删除采集参数***************************/
export function delParams(id) {
  return request({
    url: '/basicData/acquisitionParam/' + id,
    method: 'delete'
  })
}

/******************************导出采集参数***************************/
export function exportParams(query) {
  return request({
    url: '/basicData/acquisitionParam/export',
    method: 'get',
    params: query
  })
}

/******************************采集参数左侧树***************************/
export function leftTree(query) {
  return request({
    url: '/bas/common/electricParams/leftTree',
    method: 'get',
    params: query
  })
}

/******************************查询园区列表***************************/
export function getParkCodeList() {
  return request({
    url: '/basicData/parkInfo/parkList',
    method: 'get'
  })
}
