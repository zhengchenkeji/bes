import request from '@/utils/request'
/******************************查询分项拓扑配置列表***************************/
export function listConfig(query) {
  return request({
    url: '/subitemConfig/config/list',
    method: 'get',
    params: query
  })
}
/******************************查询分项拓扑配置详细***************************/
export function getConfig(subitemId) {
  return request({
    url: '/subitemConfig/config/' + subitemId,
    method: 'get'
  })
}
/******************************新增分项拓扑配置***************************/
export function addConfig(data) {
  return request({
    url: '/subitemConfig/config',
    method: 'post',
    data: data
  })
}
/******************************新增分项拓扑配置***************************/
export function addConfigBatch(data) {
  return request({
    url: '/subitemConfig/config/addBatch',
    method: 'post',
    data: data
  })
}
/******************************修改分项拓扑配置***************************/
export function updateConfig(data) {
  return request({
    url: '/subitemConfig/config',
    method: 'put',
    data: data
  })
}
/******************************删除分项拓扑配置***************************/
export function delConfig(subitemId) {
  return request({
    url: '/subitemConfig/config/' + subitemId,
    method: 'delete'
  })
}

// 导出分项拓扑配置
// export function exportConfig(query) {
//   return request({
//     url: '/subitemConfig/config/export',
//     method: 'get',
//     params: query
//   })
// }

/******************************查询分项拓扑配置下拉树结构***************************/

export function treeSelect(query) {
  return request({
    url: '/subitemConfig/config/treeSelect',
    method: 'get',
    params: query
  })
}
/******************************查询该分项下支路列表***************************/

export function nodeListBranchById(query) {
  return request({
    url: '/subitemConfig/config/listBranchById',
    method: 'get',
    params: query
  })
}

/******************************包含支路***************************/

export function saveNodeListBranch(data) {
  return request({
    url: '/subitemConfig/config/branch',
    method: 'put',
    data: data
  })
}
