import request from '@/utils/request'

// 查询数据中心基本信息列表
export function listInfo(query) {
  return request({
    url: '/basicData/dataCenterBaseInfo/list',
    method: 'get',
    params: query
  })
}

// 查询数据中心基本信息详细
export function getInfo(id) {
  return request({
    url: '/basicData/dataCenterBaseInfo/' + id,
    method: 'get'
  })
}

// 新增数据中心基本信息
export function addInfo(data) {
  return request({
    url: '/basicData/dataCenterBaseInfo',
    method: 'post',
    data: data
  })
}

// 修改数据中心基本信息
export function updateInfo(data) {
  return request({
    url: '/basicData/dataCenterBaseInfo',
    method: 'put',
    data: data
  })
}

// 删除数据中心基本信息
export function delInfo(id) {
  return request({
    url: '/basicData/dataCenterBaseInfo/' + id,
    method: 'delete'
  })
}

// 导出数据中心基本信息
export function exportInfo(query) {
  return request({
    url: '/basicData/dataCenterBaseInfo/export',
    method: 'get',
    params: query
  })
}
