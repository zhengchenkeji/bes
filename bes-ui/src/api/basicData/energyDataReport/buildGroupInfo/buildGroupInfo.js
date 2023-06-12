import request from '@/utils/request'

// 查询建筑群信息列表
export function listBuildGroupInfo(query) {
  return request({
    url: '/basicData/buildGroupInfo/list',
    method: 'get',
    params: query
  })
}

// 查询建筑群信息详细
export function getBuildGroupInfo(id) {
  return request({
    url: '/basicData/buildGroupInfo/' + id,
    method: 'get'
  })
}

// 新增建筑群信息
export function addBuildGroupInfo(data) {
  return request({
    url: '/basicData/buildGroupInfo',
    method: 'post',
    data: data
  })
}

// 修改建筑群信息
export function updateBuildGroupInfo(data) {
  return request({
    url: '/basicData/buildGroupInfo',
    method: 'put',
    data: data
  })
}

// 删除建筑群信息
export function delBuildGroupInfo(id) {
  return request({
    url: '/basicData/buildGroupInfo/' + id,
    method: 'delete'
  })
}

// 导出建筑群信息
export function exportBuildGroupInfo(query) {
  return request({
    url: '/basicData/buildGroupInfo/export',
    method: 'get',
    params: query
  })
}
