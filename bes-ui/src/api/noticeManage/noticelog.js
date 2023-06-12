import request from '@/utils/request'

  // 获取通知日志列表集合
  export function getNoticeLogList(query) {
    return request({
      url: '/noticeManage/noticeConfig/getNoticeLogList',
      method: 'get',
      params: query
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


  // 查询通知模板配置列表
export function listTemplateall() {
  return request({
    url: '/noticeManage/noticeTemplate/listall',
    method: 'get',
  })
}
  
// 获取日志详情
export function getlogInfo(query) {
  return request({
    url: '/noticeManage/noticeConfig/getlogInfo',
    method: 'get',
    params: query
  })
}