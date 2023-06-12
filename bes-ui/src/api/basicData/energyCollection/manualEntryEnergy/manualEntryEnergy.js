import request from '@/utils/request'

/******************************设备树结构***************************/
export function TreeSelect() {
  return request({
    url: '/basicData/manualEntryEnergy/treeSelect',
    method: 'get',
  })
}

/******************************查询能源类型列表***************************/

export function listType() {
  return request({
    url: '/basicData/manualEntryEnergy/allEnergyTypeList',
    method: 'get',
  })
}

// export function allListType() {
//   return request({
//     url: '/basicData/manualEntryEnergy/allEnergyTypeList',
//     method: 'get',
//   })
// }



/******************************查询采集参数列表***************************/

export function getElectricParams(query) {
  return request({
    url: '/basicData/manualEntryEnergy/getElectricParams',
    method: 'get',
    params:query

  })
}


/******************************增加手动录入能耗***************************/
export function addmanualEntryEnergy(data) {
  return request({
    url: '/basicData/manualEntryEnergy',
    method: 'post',
    data: data
  })
}


/******************************获取手动录入能耗数据***************************/
export function getmanualEntryEnergyData(query) {
  return request({
    url: '/basicData/manualEntryEnergy/getmanualEntryEnergyData',
    method: 'get',
    params: query
  })
}

/******************************获取录入能耗数值***************************/
export function getEnergyDetailData(id) {
  return request({
    url: '/basicData/manualEntryEnergy/getEnergyDetailData?entryEnergyId='+id,
    method: 'get',
  })
}

/******************************删除能耗信息***************************/
export function delEnergyDetailData(id) {
  return request({
    url: '/basicData/manualEntryEnergy/' + id,
    method: 'delete'
  })
}
