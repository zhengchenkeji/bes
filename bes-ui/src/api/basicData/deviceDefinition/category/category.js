import request from '@/utils/request'

// 查询品类列表
export function listCategory(query) {
  return request({
    url: '/baseData/category/list',
    method: 'get',
    params: query
  })
}

// 查询品类详细
export function getCategory(id) {
  return request({
    url: '/baseData/category/' + id,
    method: 'get'
  })
}

// 新增品类
export function addCategory(data) {
  return request({
    url: '/baseData/category',
    method: 'post',
    data: data
  })
}

// 修改品类
export function updateCategory(data) {
  return request({
    url: '/baseData/category',
    method: 'put',
    data: data
  })
}

// 删除品类
export function delCategory(id) {
  return request({
    url: '/baseData/category/' + id,
    method: 'delete'
  })
}

// 导出品类
export function exportCategory(query) {
  return request({
    url: '/baseData/category/export',
    method: 'get',
    params: query
  })
}