import request from '@/utils/request'

// 查询告警历史数据列表
export function listData(query) {
  return request({
    url: '/alarmHistoricalData/data/list',
    method: 'get',
    params: query
  })
}
// 删除告警历史数据
export function delData(id) {
  return request({
    url: '/alarmHistoricalData/data/' + id,
    method: 'delete'
  })
}


// 查询告警策略根据告警类型
export function getDataByAlarmTypeId(query) {
  return request({
    url: '/safetyWarning/alarmTactics/byAlarmTypeId',
    method: 'get',
    params: query
  })
}
