package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SocialComment;

/**
 * 社交评论 数据层
 *
 * @author ruoyi
 */
public interface SocialCommentMapper
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
    public SocialComment selectCommentById(@Param("commentId") Long commentId);

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
    public int deleteCommentById(@Param("commentId") Long commentId);

    /**
     * 查询帖子的最新评论
     *
     * @param postId 帖子ID
     * @param limit 限制数量
     * @return 评论列表
     */
    public List<SocialComment> selectCommentsByPostId(@Param("postId") Long postId, @Param("limit") Integer limit);

    /**
     * 查询帖子的所有评论及回复
     *
     * @param postId 帖子ID
     * @return 所有评论及回复列表
     */
    public List<SocialComment> selectAllCommentsByPostId(@Param("postId") Long postId);
}
