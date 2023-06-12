import request from '@/utils/request'

/******************************查询电类型定义列表***************************/
export function listMeterType(query) {
  return request({
    url: '/basicData/meterType/list',
    method: 'get',
    params: query
  })
}

/******************************查询电类型定义详细***************************/
export function getMeterType(id) {
  return request({
    url: '/basicData/meterType/' + id,
    method: 'get'
  })
}

/******************************新增电类型定义***************************/
export function addMeterType(data) {
  return request({
    url: '/basicData/meterType',
    method: 'post',
    data: data
  })
}

/******************************修改电类型定义***************************/
export function updateMeterType(data) {
  return request({
    url: '/basicData/meterType',
    method: 'put',
    data: data
  })
}

/******************************删除电类型定义***************************/
export function delMeterType(id) {
  return request({
    url: '/basicData/meterType/' + id,
    method: 'delete'
  })
}

/******************************导出电类型定义***************************/
export function exportMeterType(query) {
  return request({
    url: '/basicData/meterType/export',
    method: 'get',
    params: query
  })
}
