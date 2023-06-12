import request from '@/utils/request'

/**********************************************计划树*********************************************/
/*****获取计划编排树列表信息*******/
export function getAllPlanConfigListInfo(query) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/getAllPlanConfigListInfo',
    method: 'post',
    params: query
  })
}

/*****查询计划详细*******/
export function getPlanConfig(id) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/' + id,
    method: 'get'
  })
}

/*****新增计划*******/
export function addPlanConfig(data) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/addPlanConfig',
    method: 'post',
    params: data
  })
}

/*****修改计划*******/
export function updatePlanConfig(data) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/editPlanConfig',
    method: 'post',
    params: data
  })
}

/*****删除计划*******/
export function delPlanConfig(id) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/delPlanConfig/' + id,
    method: 'delete'
  })
}

/*****导出控制计划*******/
export function exportPlanConfig(query) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/export',
    method: 'get',
    params: query
  })
}


/**********************************************计划列表*********************************************/
/*****获取右侧控制计划列表*******/
export function listPlanControl(query) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/planControl/list',
    method: 'get',
    params: query
  })
}

/*****获取右侧采集列表*******/
export function listPlanCollect(query) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/planCollect/list',
    method: 'get',
    params: query
  })
}

/*****查询所有场景*******/
export function getAllSceneControlList(query) {
  return request({
    url: '/deviceManagement/scheduling/scenarioConfig/getAllSceneControlList',
    method: 'post',
    params: query
  })
}

/*****获取场景模式*******/
export function getSceneModelList(query) {
  return request({
    url: '/deviceManagement/scheduling/scenarioConfig/model/getSceneModelList',
    method: 'post',
    params: query
  })
}

/*****新增控制计划*******/
export function addPlanControl(query) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/addPlanController',
    method: 'post',
    params: query
  })
}

/*****修改控制计划*******/
export function updatePlanControl(query) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/updatePlanControl',
    method: 'post',
    params: query
  })
}

/*****查询控制计划详情*******/
export function getPlanControl(query) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/getPlanControl',
    method: 'post',
    params: query
  })
}

/*****删除控制计划*******/
export function delPlanControl(id) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/deletePlanControl/'+id,
    method: 'delete',
  })
}

/*****控制计划同步接口*******/
export function modelPointSync(query) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/modelPointSync',
    method: 'post',
    params: query
  })
}

/*****控制计划数据对比接口*******/
export function planPointContrast(query) {
  return request({
    url: '/deviceManagement/scheduling/PlanConfig/planPointContrast',
    method: 'post',
    params: query
  })
}

/**********************************************转化时间*********************************************/
export function formaterDate(date){
  var data = new Date(date);
  var year = data.getFullYear();
  var month = (data.getMonth() + 1)>=10?(data.getMonth() + 1):'0'+(data.getMonth() + 1);
  var day = data.getDate()>=10?data.getDate():'0'+data.getDate();
  var datapage = year + "-" + month + "-" + day + " ";
  return datapage;
}

export function formaterTime(time){
  var data = new Date(time);
  var h = data.getHours()>=10?data.getHours():('0'+data.getHours());
  var i = data.getMinutes()>=10?data.getMinutes():('0'+data.getMinutes());
  var s = data.getSeconds()>=10?data.getSeconds():('0'+data.getSeconds());
  var timepage = h + ":" + i + ":" + s;
  return timepage;
}

export function formaterDateTime(date){
  var data = new Date(date);
  var year = data.getFullYear();
  var month = (data.getMonth() + 1)>=10?(data.getMonth() + 1):'0'+(data.getMonth() + 1);
  var day = data.getDate()>=10?data.getDate():'0'+data.getDate();
  var h = data.getHours()>=10?data.getHours():('0'+data.getHours());
  var i = data.getMinutes()>=10?data.getMinutes():('0'+data.getMinutes());
  var s = data.getSeconds()>=10?data.getSeconds():('0'+data.getSeconds());
  var datatime = year + "-" + month + "-" + day + " "+ h + ":" + i + ":" + s;
  return datatime;
}
