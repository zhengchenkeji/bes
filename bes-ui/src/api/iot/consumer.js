import request from '@/utils/request'

// 查询消费者列表
export function listConsumer(query) {
  return request({
    url: '/iot/consumer/list',
    method: 'get',
    params: query
  })
}

// 查询消费者详细
export function getConsumer(id) {
  return request({
    url: '/iot/consumer/' + id,
    method: 'get'
  })
}

// 新增消费者
export function addConsumer(data) {
  return request({
    url: '/iot/consumer',
    method: 'post',
    data: data
  })
}

// 修改消费者
export function updateConsumer(data) {
  return request({
    url: '/iot/consumer',
    method: 'put',
    data: data
  })
}

// 删除消费者
export function delConsumer(id) {
  return request({
    url: '/iot/consumer/' + id,
    method: 'delete'
  })
}
