import request from '@/utils/request'

/***************************************添加区域****************************************************/
export function addDesignerArea(query) {
  return request({
    url: '/designerConf/designerArea/addDesignerArea',
    method: 'get',
    params: query
  })
}
/***************************************修改区域****************************************************/
export function updateDesignerArea(query) {
  return request({
    url: '/designerConf/designerArea/updateDesignerArea',
    method: 'get',
    params: query
  })
}
/***************************************删除区域****************************************************/
export function deleteDesignerArea(id) {
  return request({
    url: '/designerConf/designerArea/' + id,
    method: 'delete',
  })
}

/***************************************获取设计器区域信息****************************************************/
export function designerAreaListInfo(query) {
  return request({
    url: '/designerConf/designerArea/designerAreaListInfo',
    method: 'post',
    params: query
  })
}

/***************************************保存设计器区域信息(根据区域id)****************************************************/
export function addDesignerAreaPage(query) {
  return request({
    url: '/designerConf/designerArea/addDesignerAreaPage',
    method: 'post',
    data: query
  })
}

/***************************************修改设计器区域信息(根据区域id和页面id)****************************************************/
export function updateDesignerAreaPage(query) {
  return request({
    url: '/designerConf/designerArea/updateDesignerAreaPage',
    method: 'post',
    data: query
  })
}

/***************************************查询设计器页面信息(根据区域id和页面id)****************************************************/
export function seleteDesignerAreaPage(query) {
  return request({
    url: '/designerConf/designerArea/seleteDesignerAreaPage',
    method: 'get',
    params: query
  })
}

/***************************************删除设计器页面信息(根据区域id和页面id)****************************************************/
export function deleteDesignerAreaPage(query) {
  return request({
    url: '/designerConf/designerArea/deleteDesignerAreaPage',
    method: 'get',
    params: query
  })
}

/***************************************设计器背景图上传****************************************************/
export function uploadDesignerAvatar(data) {
  return request({
    url: '/designerConf/designerArea/designerPicture',
    method: 'post',
    data: data
  })
}

/***************************************设计器截图上传****************************************************/
export function uploadDesignerScreenshot(data) {
  return request({
    url: '/designerConf/designerArea/uploadDesignerScreenshot',
    method: 'post',
    data: data
  })
}
/***************************************设计器复制****************************************************/

export function copyDesignerScreenshot(data) {
  return request({
    url: '/designerConf/designerArea/CopyDesignerAreaPage',
    method: 'post',
    data: data
  })
}


/***************************************获取所有园区****************************************************/
export function getAllPark() {
  return request({
    url: '/basicData/buildBaseInfo/getAllPark',
    method: 'get',
  })
}



