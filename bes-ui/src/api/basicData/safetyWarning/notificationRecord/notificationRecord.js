import request from '@/utils/request'

/******************************查询告警通知记录列表***************************/
export function listAlarmTactics(query) {
  return request({
    url: '/safetyWarning/NotificationRecord/list',
    method: 'get',
    params: query
  })
}






/******************************导出告警通知记录***************************/
export function exportAlarmTactics(query) {
  return request({
    url: '/safetyWarning/NotificationRecord/export',
    method: 'get',
    params: query
  })
}