import request from '@/utils/request'


/******************************查询告警策略列表***************************/
export function listAlarmTactics(query) {
  return request({
    url: '/safetyWarning/alarmTactics/list',
    method: 'get',
    params: query
  })
}

/******************************查询告警策略详情***************************/
export function getAlarmTactics(id) {
  return request({
    url: '/safetyWarning/alarmTactics/' + id,
    method: 'get'
  })
}
/******************************新增告警策略***************************/
export function addAlarmTactics(data) {
  return request({
    url: '/safetyWarning/alarmTactics',
    method: 'post',
    data: data
  })
}
/******************************修改告警策略***************************/
export function updateAlarmTactics(data) {
  return request({
    url: '/safetyWarning/alarmTactics',
    method: 'put',
    data: data
  })
}

/******************************删除告警策略***************************/
export function delAlarmTactics(id) {
  return request({
    url: '/safetyWarning/alarmTactics/' + id,
    method: 'delete'
  })
}

/******************************导出告警策略***************************/
export function exportAlarmTactics(query) {
  return request({
    url: '/safetyWarning/alarmTactics/export',
    method: 'get',
    params: query
  })
}
/******************************查询所有电表***************************/
export function listmeter(query) {
    return request({
      url: '/deviceTree/meter/getMeterDicData',
      method: 'get',
      params: query
    })
  }
/******************************查询所有告警接收组***************************/
export function getAlarmTacticsDicData(query) {
  return request({
    url: '/safetyWarning/alarmTactics/getAlarmTacticsDicData',
    method: 'get',
    params: query
  })
}
/******************************新增告警组和告警策略的关联关系***************************/
export function saveTacticsNoitifierLink(data) {
  return request({
    url: '/safetyWarning/TacticsAlarmNotifierLink',
    method: 'post',
    data: data
  })
}
/******************************删除告警组和告警策略的关联关系***************************/
export function delTacticsNoitifierLink(data) {
  return request({
    url: '/safetyWarning/TacticsAlarmNotifierLink/delete',
    method: 'post',
    data: data
  })
}


/******************************获取采集参数***************************/
export function getElectricParamsDatalist(query) {
  return request({
    url: '/basicData/acquisitionParam/getElectricParamsDatalist',
    method: 'get',
    params:query
  })
}
/******************************获取数据项***************************/
export function getElectricParamsDatalistOther(query) {
  return request({
    url: '/baseData/equipment/getElectricParamsDatalistOther',
    method: 'get',
    params:query
  })
}



/******************************支路树结构***************************/
export function branchTreeSelect(query) {
  return request({
    url: '/branchConfig/config/treeSelect',
    method: 'get',
    params: query
  })
}
/******************************分户树结构***************************/
export function householdTreeSelect(query) {
  return request({
    url: '/householdConfig/config/treeSelect',
    method: 'get',
    params: query
  })
}

/******************************分项树结构***************************/
export function subitemTreeSelect(query) {
  return request({
    url: '/subitemConfig/config/treeSelect',
    method: 'get',
    params: query
  })
}

/******************************电表树结构***************************/
export function meterTreeSelect(query) {
  return request({
    // url: '/safetyWarning/alarmTactics/treeSelect',
    url:'/basicData/manualEntryEnergy/treeSelect',
    method: 'get',
    params: query
  })
}

/******************************第三方设备树***************************/
export function meterTreeSelectOther(query) {
  return request({
    url:'/baseData/equipment/equipmentTree',
    method: 'get',
    params: query
  })
}


/******************************获取阿里token***************************/
export function getToken() {
  return request({
    url: '/safetyWarning/alarmTactics/getToken',
    method: 'get',
  })
}

/******************************获取语音环境***************************/
export function getAudioEnvironment() {
  return request({
    url: '/safetyWarning/alarmTactics/getAudioEnvironment',
    method: 'get',
  })
}


/******************************查询能源类型列表***************************/

export function listType(query) {
  return request({
    url: '/basicData/energyType/energyTypeList',
    method: 'get',
    params: query

  })
}
/******************************查询园区列表***************************/

export function listPark() {
  return request({
    url: '/basicData/parkInfo/parkList',
    method: 'get',
  })
}


/******************************查询通知配置列表***************************/

export function listNoticeConfig(noticeType) {
  return request({
    url: '/noticeManage/noticeConfig/getNoticeConfigListByType?noticeType='+noticeType,
    method: 'get',
  })
}

/******************************查询通知模板列表***************************/
export function listNoticeTemplate(configId) {
  return request({
    url: '/noticeManage/noticeConfig/getNoticeTemplateListByConfig?configId='+configId,
    method: 'get',
  })
}

export function getNoticeLinkBytype(query) {
  return request({
    url: '/safetyWarning/alarmTactics/getNoticeLinkBytype',
    method: 'get',
    params: query

  })
}
