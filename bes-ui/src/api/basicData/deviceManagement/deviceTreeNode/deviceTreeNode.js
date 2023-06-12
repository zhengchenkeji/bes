import request from '@/utils/request'


/******************************查询树节点定义列表***************************/

export function listNode(query) {
  return request({
    url: '/deviceTree/node/list',
    method: 'get',
    params: query
  })
}
/******************************查询树节点定义数据***************************/

export function listData() {
  return request({
    url: '/deviceTree/node/listDate',
    method: 'get',
  })
}
/******************************查询树节点定义详细***************************/

export function getNode(deviceNodeId) {
  return request({
    url: '/deviceTree/node/' + deviceNodeId,
    method: 'get'
  })
}
/******************************新增树节点定义***************************/

export function addNode(data) {
  return request({
    url: '/deviceTree/node',
    method: 'post',
    data: data
  })
}
/******************************修改树节点定义***************************/

export function updateNode(data) {
  return request({
    url: '/deviceTree/node',
    method: 'put',
    data: data
  })
}
/******************************删除树节点定义***************************/

export function delNode(deviceNodeId) {
  return request({
    url: '/deviceTree/node/' + deviceNodeId,
    method: 'delete'
  })
}
/******************************导出树节点定义***************************/

export function exportNode(query) {
  return request({
    url: '/deviceTree/node/export',
    method: 'get',
    params: query
  })
}
