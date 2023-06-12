import request from '@/utils/request'

// 查询功能定义列表
export function listFunction(query) {
  return request({
    url: '/iot/function/list',
    method: 'get',
    params: query
  })
}

// 查询功能定义详细
export function getFunction(id) {
  return request({
    url: '/iot/function/' + id,
    method: 'get'
  })
}

// 新增功能定义
export function addFunction(data) {
  return request({
    url: '/iot/function',
    method: 'post',
    data: data
  })
}

// 修改功能定义
export function updateFunction(data) {
  return request({
    url: '/iot/function',
    method: 'put',
    data: data
  })
}

// 删除功能定义
export function delFunction(id) {
  return request({
    url: '/iot/function/' + id,
    method: 'delete'
  })
}

