import request from '@/utils/request'

//查询所有园区
export function getAllPark() {
  return request({
    url: '/energyAnalysis/EnergyConsumptionTrend/getAllPark',
    method: 'get',
  })
}
//查询能源类型
export function getEnergyType(params) {
  return request({
    url: '/energyAnalysis/EnergyConsumptionTrend/getEnergyType',
    method: 'get',
    params: params,
  })
}
//查询分户能耗趋势
export function queryData(param) {
  return request({
    url: '/energyAnalysis/EnergyConsumptionTrend/queryIndividualAccountData',
    params: param,
    method: 'get',
  })
}
