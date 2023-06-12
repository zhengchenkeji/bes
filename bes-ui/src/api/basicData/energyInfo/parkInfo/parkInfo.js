import request from '@/utils/request'

/******************************查询园区列表***************************/
export function listPark(query) {
  return request({
    url: '/basicData/parkInfo/list',
    method: 'get',
    params: query
  })
}

/******************************查询园区详细***************************/
export function getPark(code) {
  return request({
    url: '/basicData/parkInfo/' + code,
    method: 'get'
  })
}

/******************************新增园区***************************/
export function addPark(data) {
  return request({
    url: '/basicData/parkInfo',
    method: 'post',
    data: data
  })
}

/******************************修改园区***************************/
export function updatePark(data) {
  return request({
    url: '/basicData/parkInfo',
    method: 'put',
    data: data
  })
}

/******************************删除园区***************************/
export function delPark(code) {
  return request({
    url: '/basicData/parkInfo/' + code,
    method: 'delete'
  })
}

/******************************导出园区***************************/
export function exportPark(query) {
  return request({
    url: '/basicData/parkInfo/export',
    method: 'get',
    params: query
  })
}

/******************************用户列表***************************/
export function listUser(query) {
  return request({
    url: '/basicData/parkInfo/listUser',
    method: 'get',
    params: query
  })
}

/******************************组织机构列表***************************/
export function listOrganization(query) {
  return request({
    url: '/basicData/parkInfo/listOrganization',
    method: 'get',
    params: query
  })
}

