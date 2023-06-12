import request from '@/utils/request'

// 查询消费组列表
export function listConsumerGroup(query) {
  return request({
    url: '/iot/consumerGroup/list',
    method: 'get',
    params: query
  })
}

// 查询消费组详细
export function getConsumerGroup(id) {
  return request({
    url: '/iot/consumerGroup/' + id,
    method: 'get'
  })
}

// 新增消费组
export function addConsumerGroup(data) {
  return request({
    url: '/iot/consumerGroup',
    method: 'post',
    data: data
  })
}

// 修改消费组
export function updateConsumerGroup(data) {
  return request({
    url: '/iot/consumerGroup',
    method: 'put',
    data: data
  })
}

// 删除消费组
export function delConsumerGroup(id) {
  return request({
    url: '/iot/consumerGroup/' + id,
    method: 'delete'
  })
}
