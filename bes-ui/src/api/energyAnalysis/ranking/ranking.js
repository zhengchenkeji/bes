import request from '@/utils/request'


/************************查询支路能耗排名*************************/
export function selectRankingBranch(data) {
  return request({
    url: '/energyAnalysis/ranking/branch',
    data: data,
    method: 'post',
  })
}

/************************查询分户能耗排名*************************/
export function selectRankingHousehold(data) {
  return request({
    url: '/energyAnalysis/ranking/household',
    data: data,
    method: 'post',
  })
}

