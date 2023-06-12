import request from '@/utils/request'


/******************************查询分户计量拓扑配置列表***************************/

export function listConfig(query) {
  return request({
    url: '/householdConfig/config/list',
    method: 'get',
    params: query
  })
}

/******************************查询该分户下支路列表***************************/

export function nodeListBranchById(query) {
  return request({
    url: '/householdConfig/config/listBranchById',
    method: 'get',
    params: query
  })
}

/******************************查询分户计量拓扑配置下拉树结构***************************/

export function treeSelect(query) {
  return request({
    url: '/householdConfig/config/treeSelect',
    method: 'get',
    params: query
  })
}

/******************************查询分户计量拓扑配置详细***************************/

export function getConfig(id) {
  return request({
    url: '/householdConfig/config/' + id,
    method: 'get'
  })
}

/******************************新增分户计量拓扑配置***************************/

export function addConfig(data) {
  return request({
    url: '/householdConfig/config',
    method: 'post',
    data: data
  })
}

/******************************修改分户计量拓扑配置***************************/

export function updateConfig(data) {
  return request({
    url: '/householdConfig/config',
    method: 'put',
    data: data
  })
}

/******************************删除分户计量拓扑配置***************************/

export function delConfig(id) {
  return request({
    url: '/householdConfig/config/' + id,
    method: 'delete'
  })
}
//
// // 导出分户计量拓扑配置
// export function exportConfig(query) {
//   return request({
//     url: '/householdConfig/config/export',
//     method: 'get',
//     params: query
//   })
// }

/******************************包含支路***************************/

export function saveNodeListBranch(data) {
  return request({
    url: '/householdConfig/config/branch',
    method: 'put',
    data: data
  })
}

