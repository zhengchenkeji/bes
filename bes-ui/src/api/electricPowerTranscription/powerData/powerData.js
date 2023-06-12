import request from '@/utils/request'

//查询所有园区
export function getAllPark() {
  return request({
    url: '/electricPowerTranscription/powerData/getAllPark',
    method: 'get',
  })
}
//查询能源类型
export function getEnergyType(param) {
  return request({
    url: '/electricPowerTranscription/powerData/getEnergyType',
    method: 'get',
    params: param,
  })
}

//查询支路下电表
export function getCheckMeterList(param) {
  return request({
    url: '/electricPowerTranscription/powerData/getCheckMeterList',
    params: param,
    method: 'get',
  })
}

//查询电表下采集参数
export function getMeterParams(param) {
  return request({
    url: '/electricPowerTranscription/powerData/getMeterParams',
    params: param,
    method: 'get',
  })
}

//查询采集参数
export function getCheckMeterParamsList(param) {
  return request({
    url: '/electricPowerTranscription/powerData/getCheckMeterParamsList',
    params: param,
    method: 'get',
  })
}

//查询采集参数下分项
export function queryMeterparams(param) {
  return request({
    url: '/electricPowerTranscription/powerData/getMeterParamsList',
    params: param,
    method: 'get',
  })
}

//查询日原始数据右侧图表
export function queryData(param) {
  return request({
    url: '/electricPowerTranscription/powerData/queryDayChartsData',
    params: param,
    method: 'get',
  })
}

//查询日原始数据右侧表格
export function queryDataTable(param) {
  return request({
    url: '/electricPowerTranscription/powerData/queryDayTableData',
    params: param,
    method: 'get',
  })
}

//查询逐日极值数据右侧图表及表格
export function queryMaxData(param) {
  return request({
    url: '/electricPowerTranscription/powerData/queryMaxChartsData',
    params: param,
    method: 'get',
  })
}

//导出日原始数据
export function exportPowerTable(query) {
  return request({
    url: '/electricPowerTranscription/powerData/exportPowerTable',
    method: 'get',
    params: query,
    responseType: "blob"
  })
}

//导出逐日极值数据
export function exportMaxPowerTable(query) {
  return request({
    url: '/electricPowerTranscription/powerData/exportMaxPowerTable',
    method: 'get',
    params: query,
    responseType: "blob"
  })
}

