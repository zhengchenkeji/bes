import request from '@/utils/request'

// 查询服务订阅列表
export function listServiceSub(query) {
  return request({
    url: '/iot/serviceSub/list',
    method: 'get',
    params: query
  })
}

// 查询服务订阅详细
export function getServiceSub(id) {
  return request({
    url: '/iot/serviceSub/' + id,
    method: 'get'
  })
}

// 新增服务订阅
export function addServiceSub(data) {
  return request({
    url: '/iot/serviceSub',
    method: 'post',
    data: data
  })
}

// 修改服务订阅
export function updateServiceSub(data) {
  return request({
    url: '/iot/serviceSub',
    method: 'put',
    data: data
  })
}

// 删除服务订阅
export function delServiceSub(id) {
  return request({
    url: '/iot/serviceSub/' + id,
    method: 'delete'
  })
}
