import request from '@/utils/request'

//查询所有园区
export function getAllPark() {
  return request({
    url: '/energyAnalysis/survey/getAllPark',
    method: 'get',
  })
}
//查询能源类型
export function getEnergyType() {
  return request({
    url: '/energyAnalysis/survey/getEnergyType',
    method: 'get',
  })
}
//查询支路环比
export function queryRingRatioData(param) {
  return request({
    url: '/energyAnalysis/survey/queryRingRatioData',
    params: param,
    method: 'get',
  })
}
//查询支路能耗趋势
export function queryTrendData(param) {
  return request({
    url: '/energyAnalysis/survey/queryTrendData',
    params: param,
    method: 'get',
  })
}
//查询支路能耗排行
export function queryRankData(param) {
  return request({
    url: '/energyAnalysis/survey/queryRankData',
    params: param,
    method: 'get',
  })
}
