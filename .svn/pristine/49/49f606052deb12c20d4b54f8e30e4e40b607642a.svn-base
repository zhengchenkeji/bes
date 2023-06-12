import request from '@/utils/request'


// 授权码授权
export function authentication(data) {
  return request({
    url: '/license/authentication',
    method: 'post',
    data: { authCode: data.authCode }
  })
}
// 授权码授权（登录后使用该接口）
export function authorize(data) {
  return request({
    url: '/license/authorize',
    method: 'post',
    data: { authCode: data.authCode }
  })
}

// 获取申请码
export function getIdentityCode() {
  return request({
    url: '/license/identityCode',
    method: 'get'
  })
}

// 获取 license 信息
export function getLicenseInfo() {
  return request({
    url: '/license/licenseInfo',
    method: 'get'
  })
}
