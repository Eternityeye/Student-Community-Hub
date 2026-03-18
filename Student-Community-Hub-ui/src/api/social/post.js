import request from '@/utils/request'

// 查询帖子列表
export function listPosts(query) {
  return request({
    url: '/social/post/list',
    method: 'get',
    params: query
  })
}


// 查询个人帖子列表
export function listMyPosts(query) {
  return request({
    url: '/social/post/my/list',
    method: 'get',
    params: query
  })
}


// 查询帖子详情
export function getPost(postId) {
  return request({
    url: '/social/post/' + postId,
    method: 'get'
  })
}


// 发布帖子
export function addPost(data) {
  return request({
    url: '/social/post',
    method: 'post',
    data: data
  })
}


// 删除帖子
export function delPost(postId) {
  return request({
    url: '/social/post/' + postId,
    method: 'delete'
  })
}


// 点赞帖子
export function likePost(postId) {
  return request({
    url: '/social/post/like/' + postId,
    method: 'post'
  })
}


// 取消点赞
export function unlikePost(postId) {
  return request({
    url: '/social/post/like/' + postId,
    method: 'delete'
  })
}


// 举报帖子
export function reportPost(data) {
  return request({
    url: '/social/report',
    method: 'post',
    data: data
  })
}


// 添加评论
export function addComment(data) {
  return request({
    url: '/social/comment',
    method: 'post',
    data: data
  })
}


// 删除评论
export function delComment(commentId) {
  return request({
    url: '/social/comment/' + commentId,
    method: 'delete'
  })
}


// 回复评论
export function replyComment(data) {
  return request({
    url: '/social/comment/reply',
    method: 'post',
    data: data
  })
}


// 查询评论列表
export function listComments(postId) {
  return request({
    url: '/social/comment/list/' + postId,
    method: 'get'
  })
}
