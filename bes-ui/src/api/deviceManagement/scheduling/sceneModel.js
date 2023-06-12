import request from '@/utils/request'

/***************************************获取场景模式****************************************************/
export function getSceneModelList(query) {
  return request({
    url: '/deviceManagement/scheduling/scenarioConfig/model/getSceneModelList',
    method: 'post',
    params: query
  })
}

/***************************************查询场景模式****************************************************/
export function getSceneModel(query) {
  return request({
    url: '/deviceManagement/scheduling/scenarioConfig/model/getSceneModel',
    method: 'post',
    params: query
  })
}

/***************************************新增场景模式****************************************************/
export function addSceneModel(query) {
  return request({
    url: '/deviceManagement/scheduling/scenarioConfig/model/addSceneModel',
    method: 'post',
    params: query
  })
}

/***************************************修改场景模式****************************************************/
export function updateSceneModel(query) {
  return request({
    url: '/deviceManagement/scheduling/scenarioConfig/model/updateSceneModel',
    method: 'post',
    params: query
  })
}

/***************************************删除场景模式****************************************************/
export function deleteSceneModel(id) {
  return request({
    url: '/deviceManagement/scheduling/scenarioConfig/model/deleteSceneModel/' + id,
    method: 'delete',
  })
}

/***************************************修改场景模式点位****************************************************/
export function addSceneModelPoint(query) {
  return request({
    url: '/deviceManagement/scheduling/scenarioConfig/model/addSceneModelPoint',
    method: 'post',
    params: query
  })
}

/***************************************获取场景模式点位列表****************************************************/
export function getSceneModelPoint(query) {
  return request({
    url: '/deviceManagement/scheduling/scenarioConfig/model/getSceneModelPoint',
    method: 'post',
    params: query
  })
}

/***************************************场景模式点位同步****************************************************/
export function modelPointSync(query) {
  return request({
    url: '/deviceManagement/scheduling/scenarioConfig/model/modelPointSync',
    method: 'post',
    params: query
  })
}

/***************************************场景模式点位数据对比****************************************************/
export function modelPointContrast(query) {
  return request({
    url: '/deviceManagement/scheduling/scenarioConfig/model/modelPointContrast',
    method: 'post',
    params: query
  })
}




