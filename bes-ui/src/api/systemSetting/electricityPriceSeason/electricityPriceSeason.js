import request from '@/utils/request'

// 查询电价-季节范围列表
export function listSeason(query) {
  return request({
    url: '/systemSetting/electricityPriceSeason/list',
    method: 'get',
    params: query
  })
}

// 查询电价-季节范围详细
export function getSeason(id) {
  return request({
    url: '/systemSetting/electricityPriceSeason/' + id,
    method: 'get'
  })
}

// 新增电价-季节范围
export function addSeason(data) {
  return request({
    url: '/systemSetting/electricityPriceSeason',
    method: 'post',
    data: data
  })
}

// 修改电价-季节范围
export function updateSeason(data) {
  return request({
    url: '/systemSetting/electricityPriceSeason',
    method: 'put',
    data: data
  })
}

// 删除电价-季节范围
export function delSeason(id) {
  return request({
    url: '/systemSetting/electricityPriceSeason/' + id,
    method: 'delete'
  })
}

// 导出电价-季节范围
export function exportSeason(query) {
  return request({
    url: '/systemSetting/electricityPriceSeason/export',
    method: 'get',
    params: query
  })
}
