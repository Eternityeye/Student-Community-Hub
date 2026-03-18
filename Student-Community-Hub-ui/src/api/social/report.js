import request from '@/utils/request'

// 查询举报列表
export function listReports(query) {
  return request({
    url: '/social/report/list',
    method: 'get',
    params: query
  })
}

// 查询举报详情
export function getReport(id) {
  return request({
    url: '/social/report/' + id,
    method: 'get'
  })
}

// 审核不通过（违规删帖）
export function handleViolation(id, data) {
  return request({
    url: '/social/report/handle/violation/' + id,
    method: 'put',
    data: data
  })
}

// 审核通过（驳回举报）
export function handleDismiss(id, data) {
  return request({
    url: '/social/report/handle/dismiss/' + id,
    method: 'put',
    data: data
  })
}

// 检查用户是否已举报过该帖子
export function checkReportedPost(postId) {
  return request({
    url: '/social/report/check/' + postId,
    method: 'get'
  })
}
