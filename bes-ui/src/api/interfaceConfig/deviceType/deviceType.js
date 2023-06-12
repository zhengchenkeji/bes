import request from '@/utils/request'


/******************************查询设备类型列表***************************/
export function listDeviceType(query) {
  return request({
    url: '/basicData/parkInfo/list',
    method: 'get',
    params: query
  })
}
