import request from '@/utils/request'

//查询所有园区
export function getAllPark() {
  return request({
    url: '/electricPowerTranscription/electricityStatement/getAllPark',
    method: 'get',
  })
}
//查询能源类型
export function getEnergyType() {
  return request({
    url: '/electricPowerTranscription/electricityStatement/getEnergyType',
    method: 'get',
  })
}
//查询数据
export function queryData(param) {
  return request({
    url: '/electricPowerTranscription/electricityStatement/queryData',
    params: param,
    method: 'get',
  })
}
//导出
export function exportTable(query) {
  return request({
    url: '/electricPowerTranscription/electricityStatement/exportTable',
    method: 'get',
    params: query,
    responseType: "blob"
  })
}
