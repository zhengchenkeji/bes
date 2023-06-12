import request from '@/utils/request'

// 查询告警实时数据列表
export function listData(query) {
  return request({
    url: '/alarmRealtime/data/list',
    method: 'get',
    params: query
  })
}



// 处理告警实时数据
export function updateAlarm(data) {
  return request({
    url: '/alarmRealtime/data/updateAlarm',
    method: 'put',
    data: data
  })
}

// 删除告警实时数据
export function delData(id) {
  return request({
    url: '/alarmRealtime/data/' + id,
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
