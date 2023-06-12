import request from '@/utils/request'

// 查询设备列表
export function listDevice(query) {
  return request({
    url: '/iot/device/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询网关设备列表
 * 权限标识：iot:device:list
 * @param query
 * @returns {AxiosPromise}
 */
export function listDeviceGateway(query) {
  return request({
    url: '/iot/device/gatewayList',
    method: 'get',
    params: query
  })
}

// 查询设备详细
export function getDevice(id) {
  return request({
    url: '/iot/device/' + id,
    method: 'get'
  })
}

// 新增设备
export function addDevice(data) {
  return request({
    url: '/iot/device',
    method: 'post',
    data: data
  })
}

// 修改设备
export function updateDevice(data) {
  return request({
    url: '/iot/device',
    method: 'put',
    data: data
  })
}

// 删除设备
export function delDevice(id) {
  return request({
    url: '/iot/device/' + id,
    method: 'delete'
  })
}

/**
 * 查询设备功能详细
 * 权限标识：iot:device:queryFunction
 * @param id
 * @returns {AxiosPromise}
 */
export function getDeviceFunction(id) {
  return request({
    url: '/iot/device/function/' + id,
    method: 'get'
  })
}

/**
 * 功能调试，属性设置
 * 权限标识：iot:device:debug
 * @param data:
 *            {
 *            deviceId: 设备主键
 *            functionId：功能主键
 *            value：调试属性值
 *            }
 * @returns {AxiosPromise}
 */
export function setAttribute(data) {
  return request({
    url: '/iot/device/debug/attribute',
    method: 'put',
    data: data
  })
}

/**
 * 功能调试，属性获取
 * 权限标识：iot:device:debug
 * @param data:
 *            {
 *            deviceId: 设备主键
 *            functionId：功能主键
 *            }
 * @returns {AxiosPromise} 例：{msg: '操作成功', code: 200, data: 2.3}
 */
export function getAttribute(data) {
  return request({
    url: '/iot/device/debug/attribute',
    method: 'get',
    params: data
  })
}

/**
 * 功能调试，批量属性设置
 * 权限标识：iot:device:debug
 * @param data:
 *            [{
 *            deviceId: 设备主键
 *            functionId：功能主键
 *            value：调试属性值
 *            }]
 * @returns {AxiosPromise}
 */
export function setAttributeBatch(data) {
  return request({
    url: '/iot/device/debug/attributeBatch',
    method: 'put',
    data: data
  })
}

/**
 * 功能调试，属性批量获取
 * 权限标识：iot:device:debug
 * @param data:
 *            [{
 *            deviceId: 设备主键
 *            functionId：功能主键
 *            }]
 * @returns {AxiosPromise}
 * 例：{msg: '操作成功', code: 200, data: [{deviceId: 1, functionId: 2, value: 2.3}]}
 */
export function getAttributeBatch(data) {
  return request({
    url: '/iot/device/debug/attributeBatch',
    method: 'post',
    data: data
  })
}


/**
 * 功能调试，服务调用
 * 权限标识：iot:device:debug
 * @param data:
 *            deviceId: 设备主键
 *            functionId：功能主键
 *            value：调试属性值
 * @returns {AxiosPromise}
 */
export function invokeService(data) {
  return request({
    url: '/iot/device/debug/service',
    method: 'put',
    data: data
  })
}

/**
 * 设备统计数
 * @returns {AxiosPromise}
 *  deviceCount：设备总数
 *  onlineCount：在线数
 *  enabledCount：设备启用数
 */
export function statistics() {
  return request({
    url: '/iot/device/statistics',
    method: 'get',
  })
}
