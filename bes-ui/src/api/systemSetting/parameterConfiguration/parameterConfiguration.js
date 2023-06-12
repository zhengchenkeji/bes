import request from '@/utils/request'

// 查询主采集参数列表
export function listParams(query) {
  return request({
    url: '/systemSetting/parameterConfiguration/list',
    method: 'get',
    params: query
  })
}

// 查询主采集参数详细
export function getParams(id) {
  return request({
    url: '/systemSetting/parameterConfiguration/' + id,
    method: 'get'
  })
}

// 新增主采集参数
export function addParams(data) {
  return request({
    url: '/systemSetting/parameterConfiguration',
    method: 'post',
    data: data
  })
}

// 修改主采集参数
export function updateParams(data) {
  return request({
    url: '/systemSetting/parameterConfiguration',
    method: 'put',
    data: data
  })
}

// 删除主采集参数
export function delParams(id) {
  return request({
    url: '/systemSetting/parameterConfiguration/' + id,
    method: 'delete'
  })
}

// 导出主采集参数
export function exportParams(query) {
  return request({
    url: '/systemSetting/parameterConfiguration/export',
    method: 'get',
    params: query
  })
}

// 查询选中的的采集参数
export function listCheckList(query) {
  return request({
    url: '/systemSetting/parameterConfiguration/listCheckList',
    method: 'get',
    params: query
  })
}

// 查询未选中的的采集参数
export function listNoCheckList(query) {
  return request({
    url: '/systemSetting/parameterConfiguration/listNoCheckList',
    method: 'get',
    params: query
  })
}

// 查询未选中的数据项
export function listNoCheckListOther(query) {
  return request({
    url: '/systemSetting/parameterConfiguration/listNoCheckList',
    method: 'get',
    params: query
  })
}

// 添加一条采集参数
export function insertParamConfigRlgl(query) {
  return request({
    url: '/systemSetting/parameterConfiguration/insertParamConfigRlgl',
    method: 'get',
    params: query
  })
}

// 删除一条采集参数
export function delParamConfigRlgl(query) {
  return request({
    url: '/systemSetting/parameterConfiguration/delParamConfigRlgl',
    method: 'get',
    params: query
  })
}

// 全部选中采集参数
export function allCheckList(query) {
  return request({
    url: '/systemSetting/parameterConfiguration/allCheckList',
    method: 'get',
    params: query
  })
}

// 全部取消采集参数
export function allNoCheckList(query) {
  return request({
    url: '/systemSetting/parameterConfiguration/allNoCheckList',
    method: 'get',
    params: query
  })
}
