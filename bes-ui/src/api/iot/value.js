import request from '@/utils/request'

// 查询功能值定义列表
export function listValue(query) {
  return request({
    url: '/iot/value/list',
    method: 'get',
    params: query
  })
}

// 查询功能值定义详细
export function getValue(id) {
  return request({
    url: '/iot/value/' + id,
    method: 'get'
  })
}

// 新增功能值定义
export function addValue(data) {
  return request({
    url: '/iot/value',
    method: 'post',
    data: data
  })
}

// 修改功能值定义
export function updateValue(data) {
  return request({
    url: '/iot/value',
    method: 'put',
    data: data
  })
}

// 删除功能值定义
export function delValue(id) {
  return request({
    url: '/iot/value/' + id,
    method: 'delete'
  })
}

export function saveFvData(data) {
  return request({
    url: '/iot/value/saveFvData',
    method: 'post',
    data: data
  })
}
