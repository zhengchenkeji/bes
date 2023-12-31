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

//查询采集参数
export function getCheckMeterParamsList(param) {
  return request({
    url: '/electricPowerTranscription/powerData/getAllCheckMeterParamsList',
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

//查询采集参数下分项
export function queryMeterparams(param) {
  return request({
    url: '/electricPowerTranscription/powerData/getMeterParamsList',
    params: param,
    method: 'get',
  })
}

//查询采集参数下分项
export function queryMeterConfigparams(param) {
  return request({
    url: '/electricPowerTranscription/powerData/getMeterParamsConfigList',
    params: param,
    method: 'get',
  })
}

//查询极值数据右侧表格
export function queryMaxData(query) {
  return request({
    url: '/electricPowerTranscription/extremeValueReport/queryMaxChartsData',
    data: query,
    method: 'post',
  })
}

//导出极值报表数据
export function exportMaxPowerTable(query) {
  return request({
    url: '/electricPowerTranscription/extremeValueReport/exportPowerTable',
    method: 'post',
    data: query,
    responseType: "blob"
  })
}

