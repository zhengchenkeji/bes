import request from '@/utils/request'

// 查询分时电价列表
export function list(query) {
  return request({
    url: '/systemSetting/timeOfUsePrice/list',
    method: 'get',
    params: query
  })
}

// 修改分时电价列表
export function update(data) {
  return request({
    url: '/systemSetting/timeOfUsePrice/update',
    method: 'post',
    data: data
  })
}

//导出
export function exportTable(query) {
  return request({
    url: '/systemSetting/timeOfUsePrice/exportTable',
    method: 'get',
    params: query,
    responseType: "blob"
  })
}

