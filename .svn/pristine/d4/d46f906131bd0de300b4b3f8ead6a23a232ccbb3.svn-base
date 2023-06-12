import request from '@/utils/request'

// //查询电价规则树
// export function periceConfigListInfo(query) {
//   return request({
//     url: '/systemSetting/electricityPriceSetting/periceConfigListInfo',
//     method: 'post',
//     params: query
//   })
// }
//
// //删除电价规则
// export function deletepriceConfig(id) {
//   return request({
//     url: '/systemSetting/electricityPriceSetting/deletepriceConfig/' + id,
//     method: 'delete',
//   })
// }
//
// //添加电价规则
// export function addPriceConfig(query) {
//   return request({
//     url: '/systemSetting/electricityPriceSetting/addPriceConfig',
//     method: 'post',
//     params: query
//   })
// }
//
// //修改电价规则
// export function updatePriceConfig(query) {
//   return request({
//     url: '/systemSetting/electricityPriceSetting/updatePriceConfig',
//     method: 'post',
//     params: query
//   })
// }

// 查询电价设置列表
export function listSetting(query) {
  return request({
    url: '/systemSetting/electricityPriceSetting/list',
    method: 'get',
    params: query
  })
}

// 查询电价设置详细
export function getSetting(id) {
  return request({
    url: '/systemSetting/electricityPriceSetting/' + id,
    method: 'get'
  })
}

// 新增电价设置
export function addSetting(data) {
  return request({
    url: '/systemSetting/electricityPriceSetting',
    method: 'post',
    data: data
  })
}

// 修改电价设置
export function updateSetting(data) {
  return request({
    url: '/systemSetting/electricityPriceSetting',
    method: 'put',
    data: data
  })
}

// 删除电价设置
export function delSetting(id) {
  return request({
    url: '/systemSetting/electricityPriceSetting/' + id,
    method: 'delete'
  })
}

// 导出电价设置
export function exportSetting(query) {
  return request({
    url: '/systemSetting/electricityPriceSetting/export',
    method: 'get',
    params: query
  })
}
