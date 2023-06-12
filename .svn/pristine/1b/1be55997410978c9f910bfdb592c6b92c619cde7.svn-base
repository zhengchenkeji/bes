import request from '@/utils/request'

// 查询告警工单列表
export function listAlarmWorkOrder(query) {
  return request({
    url: '/safetyWarning/alarmWorkOrder/list',
    method: 'get',
    params: query
  })
}

// 查询告警工单详细
export function getAlarmWorkOrder(id) {
  return request({
    url: '/safetyWarning/alarmWorkOrder/' + id,
    method: 'get'
  })
}

// 新增告警工单
export function addAlarmWorkOrder(data) {
  return request({
    url: '/safetyWarning/alarmWorkOrder',
    method: 'post',
    data: data
  })
}

// 修改告警工单
export function updateAlarmWorkOrder(data) {
  return request({
    url: '/safetyWarning/alarmWorkOrder',
    method: 'put',
    data: data
  })
}

// 删除告警工单
export function delAlarmWorkOrder(id) {
  return request({
    url: '/safetyWarning/alarmWorkOrder/' + id,
    method: 'delete'
  })
}

// 导出告警工单
export function exportAlarmWorkOrder(query) {
  return request({
    url: '/safetyWarning/alarmWorkOrder/export',
    method: 'get',
    params: query
  })
}