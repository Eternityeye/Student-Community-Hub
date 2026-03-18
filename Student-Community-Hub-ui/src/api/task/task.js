import request from '@/utils/request'

// 查询任务列表
export function listTasks(query) {
  return request({
    url: '/task/list',
    method: 'get',
    params: query
  })
}

// 查询我发布的任务列表
export function listMyPublished(query) {
  return request({
    url: '/task/my/published',
    method: 'get',
    params: query
  })
}

// 查询我接单的任务列表
export function listMyTaken(query) {
  return request({
    url: '/task/my/taken',
    method: 'get',
    params: query
  })
}

// 查询任务详情
export function getTask(taskId) {
  return request({
    url: '/task/' + taskId,
    method: 'get'
  })
}

// 发布任务
export function addTask(data) {
  return request({
    url: '/task',
    method: 'post',
    data: data
  })
}

// 删除任务
export function delTask(taskId) {
  return request({
    url: '/task/' + taskId,
    method: 'delete'
  })
}

// 接单
export function takeTask(taskId) {
  return request({
    url: '/task/take/' + taskId,
    method: 'post'
  })
}

// 接单者确认完成
export function completeTask(taskId) {
  return request({
    url: '/task/complete/' + taskId,
    method: 'put'
  })
}

// 发布者确认完成
export function confirmTask(taskId) {
  return request({
    url: '/task/confirm/' + taskId,
    method: 'put'
  })
}

// 提交评价
export function addReview(data) {
  return request({
    url: '/task/review',
    method: 'post',
    data: data
  })
}

// 查询我的评价记录（收到的）
export function listMyReviews(query) {
  return request({
    url: '/task/review/my',
    method: 'get',
    params: query
  })
}

// 获取用户评价统计
export function getReviewStats(userId) {
  return request({
    url: '/task/review/stat/' + userId,
    method: 'get'
  })
}
