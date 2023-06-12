import request from '@/utils/request'

// 查询通知模板配置列表
export function listTemplate(query) {
  return request({
    url: '/noticeManage/noticeTemplate/list',
    method: 'get',
    params: query
  })
}

// 查询通知模板配置详细
export function getTemplate(id) {
  return request({
    url: '/noticeManage/noticeTemplate/' + id,
    method: 'get'
  })
}

// 新增通知模板配置
export function addTemplate(data) {
  return request({
    url: '/noticeManage/noticeTemplate',
    method: 'post',
    data: data
  })
}

// 修改通知模板配置
export function updateTemplate(data) {
  return request({
    url: '/noticeManage/noticeTemplate/edit',
    method: 'post',
    data: data
  })
}

// 删除通知模板配置
export function delTemplate(id) {
  return request({
    url: '/noticeManage/noticeTemplate/' + id,
    method: 'delete'
  })
}

// 导出通知模板配置
export function exportTemplate(query) {
  return request({
    url: '/noticeManage/noticeTemplate/export',
    method: 'get',
    params: query
  })
}

// 获取所属配置集合
export function getNoticeConfigbyTemplate(query) {
  return request({
    url: '/noticeManage/noticeTemplate/getNoticeConfigbyTemplate',
    method: 'get',
    params: query
  })
}

  // 提交配置调试
  export function debugingTemplate(data) {
    return request({
      url: '/noticeManage/noticeTemplate/debugTemplate',
      method: 'post',
      data: data
    })
  }