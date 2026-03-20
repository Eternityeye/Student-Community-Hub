import request from '@/utils/request'

// 创建纠纷
export function createDispute(data) {
  return request({
    url: '/dispute/create',
    method: 'post',
    data
  })
}

// 获取纠纷列表
export function getDisputeList(params) {
  return request({
    url: '/dispute/list',
    method: 'get',
    params
  })
}

// 获取纠纷详情
export function getDisputeDetail(caseId) {
  return request({
    url: `/dispute/${caseId}`,
    method: 'get'
  })
}

// 被申请人提交答辩证据
export function submitDefenseEvidence(data) {
  return request({
    url: '/dispute/submitDefense',
    method: 'post',
    data
  })
}

// 获取用户信誉信息
export function getUserCredit(userId) {
  return request({
    url: `/user/credit/${userId}`,
    method: 'get'
  })
}

// 获取用户信誉历史
export function getCreditLog(userId) {
  return request({
    url: `/user/credit/log/${userId}`,
    method: 'get'
  })
}

// ========== 管理后台接口 ==========

// 获取所有纠纷列表（管理员）
export function getAdminDisputeList(params) {
  return request({
    url: '/dispute/admin/list',
    method: 'get',
    params
  })
}

// 处理纠纷（管理员裁定）
export function handleDispute(caseId, data) {
  return request({
    url: `/dispute/${caseId}/handle`,
    method: 'put',
    params: data
  })
}
