import request from '@/utils/request'

// 查询设备协议列表
export function listAgreement(query) {
  return request({
    url: '/baseData/agreement/list',
    method: 'get',
    params: query
  })
}

// 查询设备协议详细
export function getAgreement(id) {
  return request({
    url: '/baseData/agreement/' + id,
    method: 'get'
  })
}

// 新增设备协议
export function addAgreement(data) {
  return request({
    url: '/baseData/agreement',
    method: 'post',
    data: data
  })
}

// 修改设备协议
export function updateAgreement(data) {
  return request({
    url: '/baseData/agreement',
    method: 'put',
    data: data
  })
}

// 删除设备协议
export function delAgreement(id,check) {
  return request({
    url: '/baseData/agreement/'+ id+"?check="+check,
    method: 'delete'
  })
}

// 导出设备协议
export function exportAgreement(query) {
  return request({
    url: '/baseData/agreement/export',
    method: 'get',
    params: query
  })
}