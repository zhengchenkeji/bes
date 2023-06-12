import request from '@/utils/request'

// 报警个数
export function alarmCount() {
  return request({
    url: '/alarmRealtime/data/alarmCount',
    method: 'get',
  })
}

  // 报警详情
export function alarmInfo(param) {
    return request({
      url: '/alarmRealtime/data/alarmInfo',
      method: 'get',
      params:param
    })
}


