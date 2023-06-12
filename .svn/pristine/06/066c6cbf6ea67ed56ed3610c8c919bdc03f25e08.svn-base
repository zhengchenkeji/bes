import request from '@/utils/request'



// 新增通知配置
export function addNoticeConfig(data) {
    return request({
        url: '/noticeManage/noticeConfig',
        method: 'post',
        data: data
    })
}

// 查询通知配置列表
export function listNoticeConfig(query) {
    return request({
        url: '/noticeManage/noticeConfig/list',
        method: 'get',
        params: query
    })
}
// 修改通知配置
export function updateNoticeConfig(data) {
    return request({
        url: '/noticeManage/noticeConfig',
        method: 'put',
        data: data
    })
}
// 删除通知配置
export function delNoticeConfig(id) {
    return request({
        url: '/noticeManage/noticeConfig/'+id,
      method: 'delete'
    })
  }
// 通知配置详情
  export function getNoticeConfig(id) {
    return request({
      url: '/noticeManage/noticeConfig/' + id,
      method: 'get'
    })
  }
// 导出配置
  export function exportNoticeConfig(query) {
    return request({
      url: '/noticeManage/noticeConfig/export',
      method: 'get',
      params: query
    })
  }
// 获取所有厂商集合
export function getAllServiceFactory() {
    return request({
      url: '/noticeManage/noticeConfig/getAllServiceFactory',
      method: 'get',
    })
  }
  // 获取所属模板集合
  export function getNoticeTemplatebyConfig(query) {
    return request({
      url: '/noticeManage/noticeConfig/getNoticeTemplatebyConfig',
      method: 'get',
      params: query
    })
  }
  // 提交配置调试
  export function debugingConfig(data) {
    return request({
      url: '/noticeManage/noticeConfig/debugConfig',
      method: 'post',
      data: data
    })
  }

  // 获取通知日志列表集合
  export function getNoticeLogList(query) {
    return request({
      url: '/noticeManage/noticeConfig/getNoticeLogList',
      method: 'get',
      params: query
    })
  }