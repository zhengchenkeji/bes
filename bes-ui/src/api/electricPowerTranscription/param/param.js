import request from '@/utils/request'


//查询电表下采集参数
export function getMeterParams(param) {
  return request({
    url: '/electricPowerTranscription/param/getMeterParams',
    params: param,
    method: 'get',
  })
}
//根据电能参数 查询电能数据
export function getDataByParamsCode(data) {
  return request({
    url: '/electricPowerTranscription/param/getDataByParamsCode',
    params: data,
    method: 'get',
  })
}
