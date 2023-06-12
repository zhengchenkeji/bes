import request from '@/utils/request'

// 查询树节点列表
export function listTree(query) {
  return request({
    url: '/basicData/deviceTree/listTree',
    method: 'get',
    params: query
  })
}

// 查询树节点列表
export function allListTree(query) {
  return request({
    url: '/basicData/deviceTree/allListTree',
    method: 'get',
    params: query
  })
}

// 查询第三方树节点列表
export function allEquipmentFunctionTree() {
  return request({
    url: '/baseData/equipment/equipmentFunctionTree',
    method: 'get',
  })
}

// 根据节点类型查询相应的按钮
export function getButtonByTreeType(query) {
  return request({
    url: '/basicData/deviceTree/getButtonByTreeType',
    method: 'post',
    params: query
  })
}
/*******************************************获取当前点击节点的详细信息*********************************************************/
export function getTreeNodeManage(query) {
  return request({
    url: '/basicData/deviceTree/getTreeNodeManage',
    method: 'get',
    params: query
  })
}


  /******************************************qindehua 新增楼控/照明/能耗控制器节点信息*************************************/
  export function addController(data) {
    return request({
      url: '/basicData/deviceTree/controller/addController',
      method: 'post',
      data: data
    })
  }

   /***************************************qindehua 修改楼控信息*************************************/
    export function updateController(data) {
      return request({
        url: '/basicData/deviceTree/controller',
        method: 'put',
        data: data
      })
    }


/***************************************qindehua 设置时间*************************************/
export function setTimeDDC(data) {
  return request({
    url: '/basicData/deviceTree/controller/setTimeDDC',
    method: 'get',
    params: data
  })
}
/***************************************qindehua 获取时间*************************************/
export function getTimeDDC(data) {
  return request({
    url: '/basicData/deviceTree/controller/getTimeDDC',
    method: 'get',
    params: data
  })
}
/***************************************qindehua DDC重新启动*************************************/
export function restartDDC(data) {
  return request({
    url: '/basicData/deviceTree/controller/restartDDC',
    method: 'get',
    params: data
  })
}
/***************************************qindehua 重置DDC控制器*************************************/
export function resetDDC(data) {
  return request({
    url: '/basicData/deviceTree/controller/resetDDC',
    method: 'get',
    params: data
  })
}
/***************************************qindehua 同步DDC*************************************/
export function synchronizeDDC(data) {
  return request({
    url: '/basicData/deviceTree/controller/synchronizeDDC',
    method: 'get',
    params: data
  })
}
/***************************************qindehua DDC远程升级*************************************/
export function remoteUpgradeDdc(data) {
  return request({
    url: '/basicData/deviceTree/controller/remoteUpgradeDdc',
    method: 'get',
    params: data
  })
}
/***************************************qindehua 数据对比*************************************/
export function getDDCInfoParam(data) {
  return request({
    url: '/basicData/deviceTree/controller/getDDCInfoParam',
    method: 'get',
    params: data
  })
}



// 添加树节点
export function insertDeviceTreee(query) {
  return request({
    url: '/deviceTree/insertDeviceTreee',
    method: 'post',
    params: query
  })
}

// 添加点位
export function insertPoint(query) {
  return request({
    url: '/deviceTree/insertPoint',
    method: 'post',
    params: query
  })
}

// 修改点位
export function updatePoint(query) {
  return request({
    url: '/deviceTree/updatePoint',
    method: 'post',
    params: query
  })
}

//能源类型列表
export function listEnergy(query) {
  return request({
    url: '/basicData/energyType/energyTypeList',
    method: 'get',
    params: query
  })
}

//能源类型列表
export function listVpoint(query) {
  return request({
    url: '/deviceTree/listVpoint',
    method: 'get',
    params: query
  })
}

  /*******************************************删除树节点操作*********************************************************/
  export function deleteTreeNode(query) {
    return request({
      url: '/basicData/deviceTree/deleteTreeNode',
      method: 'post',
      params: query
    })
}
/**************************************************新增能耗总线*********************************************************/
export function addBus(data) {
  return request({
    url: '/deviceTree/bus',
    method: 'post',
    data: data
  })
}
/**************************************************修改能耗总线*********************************************************/
export function updateBus(data) {
  return request({
    url: '/deviceTree/bus',
    method: 'put',
    data: data
  })
}
/**************************************************新增能耗电表*********************************************************/
export function addMeter(data) {
  return request({
    url: '/deviceTree/meter',
    method: 'post',
    data: data
  })
}
/**************************************************修改能耗电表*********************************************************/
export function updateMeter(data) {
  return request({
    url: '/deviceTree/meter',
    method: 'put',
    data: data
  })
}
/**************************************************获取采集方案列表*********************************************************/
export function getCollectionMethodList(param) {
  return request({
    url: '/deviceTree/meter/getCollectionMethodList',
    method: 'get',
    params: param
  })
}
/*******************************************二次确认后删除电表操作*********************************************************/
export function deleteMeter(query) {
  return request({
    url: '/basicData/deviceTree/deleteMeterAll',
    method: 'post',
    params: query
  })
}
/*******************************************电表 同步数据*********************************************************/
export function syncMeter(query) {
  return request({
    url: '/deviceTree/meter/syncMeter',
    method: 'post',
    params: query
  })
}
/*******************************************电表 数据对比*********************************************************/
export function getMeterInfoParam(param) {
  return request({
    url: '/deviceTree/meter/getMeterInfoParam',
    method: 'get',
    params: param
  })
}
/*******************************************电表 电能参数*********************************************************/
export function getElectricParams(param) {
  return request({
    url: '/deviceTree/meter/getElectricParams',
    method: 'get',
    params: param
  })
}
/*******************************************电表 获取能耗数据*********************************************************/
export function getMeterRealTimeData(param) {
  return request({
    url: '/deviceTree/meter/getMeterRealTimeData',
    method: 'get',
    params: param
  })
}
/*******************************************电表 获取历史能耗数据*******************************************************/
export function getMeterHistoryData(param) {
  return request({
    url: '/deviceTree/meter/getMeterHistoryData',
    method: 'get',
    params: param
  })
}


 /*******************************************删除树节点操作*********************************************************/
 export function addCoupler(query) {
  return request({
    url: '/basicData/deviceTree/deleteTreeNode',
    method: 'post',
    params: query
  })
}

/*******************************************电表测试采集数据*********************************************************/
export function testData(param) {
  return request({
    url: '/deviceTree/meter/testData',
    method: 'get',
    params: param
  })
}

/*******************************************根据点位id获取点位实时值*********************************************************/
export function getRealTimeData(id) {
  return request({
    url: '/basicData/deviceTree/getRealTimeData',
    method: 'get',
    params: id
  })
}

/*******************************************设计器根据所有的点位id获取点位实时值*********************************************************/
export function getAllRealTimeData(param) {
  return request({
    url: '/basicData/deviceTree/getAllRealTimeData',
    method: 'post',
    data: param
  })
}

