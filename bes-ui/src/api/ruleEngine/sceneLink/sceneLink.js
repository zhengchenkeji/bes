import request from '@/utils/request'

/**************  修改场景 **************/
export function listOrder(data) {
  return request({
    url: '/sceneLink/add',
    method: 'post',
    data: data
  })
}

/**************  修改场景 **************/
export function edit(data) {
  return request({
    url: '/sceneLink/edit',
    method: 'put',
    data: data
  })
}

/**************  删除场景 **************/
export function del(id) {
  return request({
    url: '/sceneLink/del/' + id,
    method: 'delete'
  })
}

/**************  获取场景联动详细信息 **************/
export function getInfo(id) {
  return request({
    url: '/sceneLink/info/' + id,
    method: 'get'
  })
}

/**************  获取场景联动列表 **************/
export function getList(query) {
  return request({
    url: '/sceneLink/list',
    method: 'get',
    params: query
  })
}
/***************************************场景列表dic****************************************************/
export function listSceneDic(query) {
  return request({
    url: '/sceneLink/listSceneDic',
    method: 'get',
    params: query

  })
}
/***************************************修改场景状态****************************************************/
export function changeStatus(data) {
  return request({
    url: '/sceneLink/changeStatus',
    method: 'put',
    data: data
  })
}


/***************************************获取第三方设备树****************************************************/
export function getEquipmentListByScene(data) {
  return request({
    url: '/baseData/equipment/getEquipmentListByScene',
    method: 'get',
  })
}


/***************************************获取第三方设备树****************************************************/
export function getbesDeviceList(data) {
  return request({
    url: '/sceneLink/getbesDeviceList',
    method: 'get',
  })
}

// 查询第三方树节点功能列表
export function allEquipmentFunctionTree() {
  return request({
    url: '/baseData/equipment/equipmentFunctionTreeByJob',
    method: 'get',
  })
}


/***************************************根据通知类型获取通知类型****************************************************/
export function getNoticeConfigListByType(query) {
  return request({
    url: '/noticeManage/noticeConfig/getNoticeConfigListByType',
    method: 'get',
    params: query

  })
}


/***************************************根据通知配置查询模板****************************************************/
export function getNoticeTemplateListByConfig(query) {
  return request({
    url: '/noticeManage/noticeConfig/getNoticeTemplateListByConfig',
    method: 'get',
    params: query

  })
}


/**************  手动执行场景 **************/
export function execute(id) {
  return request({
    url: '/sceneLink/execute/'+id,
    method: 'get',
  })
}