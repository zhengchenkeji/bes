import request from '@/utils/request'

/**************  微信支付 **************/
// 查询支付_订单列表
export function listOrder(query) {
  return request({
    url: '/payment/order/list',
    method: 'get',
    params: query
  })
}

// 查询支付_订单详细
export function getOrder(id) {
  return request({
    url: '/payment/order/' + id,
    method: 'get'
  })
}

// 新增支付_订单
export function addOrder(data) {
  return request({
    url: '/payment/order',
    method: 'post',
    data: data
  })
}

// 修改支付_订单
export function updateOrder(data) {
  return request({
    url: '/payment/order',
    method: 'put',
    data: data
  })
}

// 删除支付_订单
export function delOrder(id) {
  return request({
    url: '/payment/order/' + id,
    method: 'delete'
  })
}

/***
 * 统一支付接口
 * 微信支付
 * 立即支付
*/
export function idePayment(query) {
  return request({
    url: '/payment/wxPay/order',
    method: 'get',
    params: query
  })
}

/***
 * 统一支付接口
 * 微信支付
 * 退款
 */
export function refundPayment(query) {
  return request({
    url: '/payment/wxPay/refund',
    method: 'post',
    params: query
  })
}

/**************  支付宝支付 **************/

/***
 * 统一支付接口
 * 支付宝支付
 * 立即支付
 */
export function aliPayment(query) {
  return request({
    url: '/payment/aliPay/order/alipay',
    method: 'post',
    params: query
  })
}
