import request from '@/utils/request'

/******************************查询告警接收组***************************/
export function listAlarmNotifier(query) {
  return request({
    url: '/safetyWarning/AlarmNotifier/list',
    method: 'get',
    params: query
  })
}

/******************************查询告警接收组详情***************************/
export function getAlarmNotifier(id) {
  return request({
    url: '/safetyWarning/AlarmNotifier/' + id,
    method: 'get'
  })
}

/******************************新增告警接收组***************************/
export function addAlarmNotifier(data) {
  return request({
    url: '/safetyWarning/AlarmNotifier',
    method: 'post',
    data: data
  })
}

/******************************修改告警接收组***************************/
export function updateAlarmNotifier(data) {
  return request({
    url: '/safetyWarning/AlarmNotifier',
    method: 'put',
    data: data
  })
}

/******************************删除告警接收组***************************/
export function delAlarmNotifier(id) {
  return request({
    url: '/safetyWarning/AlarmNotifier/' + id,
    method: 'delete'
  })
}
/******************************导出告警接收组***************************/
export function exportAlarmNotifier(query) {
  return request({
    url: '/safetyWarning/AlarmNotifier/export',
    method: 'get',
    params: query
  })
}

/******************************获取部门下的用户***************************/
export function getUserList(query) {
  return request({
    url: '/safetyWarning/AlarmNotifier/getUserList',
    method: 'get',
    params: query
  })
}
