import request from '@/utils/request'


//获取树节点
export function getDeviceTreee(data) {
  return request({
    url: '/deviceJob/timeTaskSync/listTreeController',
    method: 'get',
    data: data
  })
}
  // 查询定时任务调度列表
export function listDevicejob(query) {
    return request({
      url: '/deviceJob/timeTaskSync/list',
      method: 'get',
      params: query
    })
  }

// 新增定时任务调度
export function addDevicejob(data) {
  return request({
    url: '/deviceJob/timeTaskSync',
    method: 'post',
    data: data
  })
}
// 修改定时任务调度
export function updateJob(data) {
  return request({
    url: '/deviceJob/timeTaskSync',
    method: 'put',
    data: data
  })
}


//获取当前所有点位
export function getPointList(data) {
  return request({
    url: '/deviceJob/timeTaskSync/querySbList',
    method: 'get',
    params: data
  })
}

//获取当前同步任务详情
export function getDeviceJob(Id) {
  return request({
    url: '/deviceJob/timeTaskSync/' + Id,
    method: 'get'
  })
}

//获取当前已选中的节点
export function getChcekNodes(Id) {
  return request({
    url: '/deviceJob/timeTaskSync/getChcekNodes/' + Id,
    method: 'get'
  })
}

// 删除定时同步任务
export function delDeviceJob(jobId) {
  return request({
    url: '/deviceJob/timeTaskSync/' + jobId,
    method: 'delete'
  })
}

// 任务状态修改
export function changeJobStatus(jobId, status,jobGroup) {
  const data = {
    jobId,
    status,
    jobGroup
  }
  return request({
    url: '/deviceJob/timeTaskSync/changeStatus',
    method: 'put',
    data: data
  })
}


// 定时任务立即执行一次
export function runJob(jobId, jobGroup) {
  const data = {
    jobId,
    jobGroup
  }
  return request({
    url: '/deviceJob/timeTaskSync/run',
    method: 'put',
    data: data
  })
}
