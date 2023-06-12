import request from '@/utils/request'

// 查询场景联动-执行日志列表
export function listLog(query) {
  return request({
    url: '/sceneLink/listlog',
    method: 'get',
    params: query
  })
}

// 查询场景联动-执行日志详细
export function getLog(id) {
  return request({
    url: '/system/log/' + id,
    method: 'get'
  })
}


/***************************************场景列表dic****************************************************/
export function listSceneDic(query) {
  return request({
    url: '/sceneLink/listSceneDic',
    method: 'get',
    params: query

  })
}

// 新增场景联动-执行日志
export function addLog(data) {
  return request({
    url: '/system/log',
    method: 'post',
    data: data
  })
}

// 修改场景联动-执行日志
export function updateLog(data) {
  return request({
    url: '/system/log',
    method: 'put',
    data: data
  })
}

// 删除场景联动-执行日志
export function delLog(id) {
  return request({
    url: '/system/log/' + id,
    method: 'delete'
  })
}

// 导出场景联动-执行日志
export function exportLog(query) {
  return request({
    url: '/system/log/export',
    method: 'get',
    params: query
  })
}