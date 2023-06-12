import request from '@/utils/request'

// 订阅
export function subscribe(query) {
  return request({
    url: '/basedatamanage/pubsubmanage/subscribe',
    method: 'get',
    params: query
  })
}

//批量订阅
export function subscribeList(query) {
  return request({
    url: '/basedatamanage/pubsubmanage/subscribeList',
    method: 'post',
    data: query
  })
}


// 取消订阅
export function unsubscribe(query) {
  return request({
    url: '/basedatamanage/pubsubmanage/unsubscribe',
    method: 'get',
    params: query
  })
}

// 批量取消订阅
export function unsubscribeList(query) {
  return request({
    url: '/basedatamanage/pubsubmanage/unsubscribeList',
    method: 'post',
    data: query
  })
}
