import request from '@/utils/request'

// 查询物联设备列表
export function listEquipment(query) {
  return request({
    url: '/baseData/equipment/list',
    method: 'get',
    params: query
  })
}

// 查询物联子设备列表
export function listEquipmentSon(query) {
  return request({
    url: '/baseData/equipment/sonList',
    method: 'get',
    params: query
  })
}


// 查询物联设备详细
export function getEquipment(id) {
  return request({
    url: '/baseData/equipment/' + id,
    method: 'get'
  })
}

// 查询物联设备详情
export function getEquipmentInfo(id) {
  return request({
    url: '/baseData/equipment/info/' + id,
    method: 'get'
  })
}

// 查询物联设备实时数据
export function getEquipmentActualTimeList(id) {
  return request({
    url: '/baseData/equipment/actualTime/' + id,
    method: 'get'
  })
}

// 获取物联设备是保存的数据项列表
export function getEquipmentActualTimeListPreserve(id) {
  return request({
    url: '/baseData/equipment/actualTimePreserve/' + id,
    method: 'get'
  })
}

// 查询物联设备历史数据
export function getHistoryList(query) {
  return request({
    url: '/baseData/equipment/getHistoryList',
    method: 'get',
    params: query
  })
}

// 查询物联设备报警历史数据
export function getWarnDataList(query) {
  return request({
    url: '/baseData/equipment/getWarnDataList',
    method: 'get',
    params: query
  })
}

// 新增物联设备
export function addEquipment(data) {
  return request({
    url: '/baseData/equipment',
    method: 'post',
    data: data
  })
}

// 修改物联设备
export function updateEquipment(data) {
  return request({
    url: '/baseData/equipment',
    method: 'put',
    data: data
  })
}

// 修改物联设备离线报警状态
export function updateAthenaBesEquipmentOfflineAlarm(query) {
  return request({
    url: '/baseData/equipment/updateAthenaBesEquipmentOfflineAlarm',
    method: 'get',
    params: query
  })
}

// 删除物联设备
export function delEquipment(id) {
  return request({
    url: '/baseData/equipment/' + id,
    method: 'delete'
  })
}

// 导出物联设备
export function exportEquipment(query) {
  return request({
    url: '/baseData/equipment/export',
    method: 'get',
    params: query
  })
}

//轮询测试
export function pollingEquipment(){
  return request({
    url: '/HTTPCommunication/HttpPolling',
    method: 'get'
  })
}

//查询所有园区
export function listAllPark(){
  return request({
    url: '/electricPowerTranscription/electricityStatement/getAllPark',
    method: 'get'
  })
}

// 配置设备的数据项重命名
export function toConfigureEquipmentItemData(data) {
  return request({
    url: '/baseData/equipment/toConfigureEquipmentItemData',
    method: 'post',
    data: data
  })
}
