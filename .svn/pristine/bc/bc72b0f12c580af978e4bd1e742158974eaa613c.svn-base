import request from '@/utils/request'

// 查询建筑基本项数据列表
export function listBuildBaseInfo(query) {
  return request({
    url: '/basicData/buildBaseInfo/list',
    method: 'get',
    params: query
  })
}

// 查询建筑基本项数据详细
export function getBuildBaseInfo(id) {
  return request({
    url: '/basicData/buildBaseInfo/' + id,
    method: 'get'
  })
}

// 新增建筑基本项数据
export function addBuildBaseInfo(data) {
  return request({
    url: '/basicData/buildBaseInfo',
    method: 'post',
    data: data
  })
}

// 修改建筑基本项数据
export function updateBuildBaseInfo(data) {
  return request({
    url: '/basicData/buildBaseInfo',
    method: 'put',
    data: data
  })
}

// 删除建筑基本项数据
export function delBuildBaseInfo(id) {
  return request({
    url: '/basicData/buildBaseInfo/' + id,
    method: 'delete'
  })
}

// 导出建筑基本项数据
export function exportBuildBaseInfo(query) {
  return request({
    url: '/basicData/buildBaseInfo/export',
    method: 'get',
    params: query
  })
}

// 查询所有数据中心
export function getAllDataCenterBaseInfo() {
  return request({
    url: '/basicData/buildBaseInfo/getAllDataCenterBaseInfo',
    method: 'get',
  })
}

//查询所有建筑群
export function getAllBuildGroup() {
  return request({
    url: '/basicData/buildBaseInfo/getAllBuildGroup',
    method: 'get',
  })
}

//查询所有园区
export function getAllPark() {
  return request({
    url: '/basicData/buildBaseInfo/getAllPark',
    method: 'get',
  })
}
