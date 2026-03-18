package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SocialComment;

/**
 * 社交评论 服务层
 *
 * @author ruoyi
 */
public interface ISocialCommentService
{
    /**
     * 查询评论列表
     *
     * @param comment 评论信息
     * @return 评论列表
     */
    public List<SocialComment> selectCommentList(SocialComment comment);

    /**
     * 查询评论详情
     *
     * @param commentId 评论ID
     * @return 评论详情
     */
    public SocialComment selectCommentById(Long commentId);

    /**
     * 查询帖子的最新评论
     *
     * @param postId 帖子ID
     * @param limit 限制数量
     * @return 评论列表
     */
    public List<SocialComment> selectCommentsByPostId(Long postId, Integer limit);

    /**
     * 查询帖子的评论及回复（嵌套结构）
     *
     * @param postId 帖子ID
     * @param limit 限制数量
     * @return 嵌套的评论列表
     */
    public List<SocialComment> selectCommentsByPostIdWithReplies(Long postId, Integer limit);

    /**
     * 新增评论
     *
     * @param comment 评论信息
     * @return 结果
     */
    public int insertComment(SocialComment comment);

    /**
     * 修改评论
     *
     * @param comment 评论信息
     * @return 结果
     */
    public int updateComment(SocialComment comment);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 结果
     */
    public int deleteCommentById(Long commentId);
}
