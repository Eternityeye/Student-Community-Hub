package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SocialPost;

/**
 * 社交帖子 数据层
 *
 * @author ruoyi
 */
public interface SocialPostMapper
{
    /**
     * 查询帖子列表
     *
     * @param post 帖子信息
     * @return 帖子列表
     */
    public List<SocialPost> selectPostList(SocialPost post);

    /**
     * 查询帖子详情
     *
     * @param postId 帖子ID
     * @return 帖子详情
     */
    public SocialPost selectPostById(@Param("postId") Long postId);

    /**
     * 新增帖子
     *
     * @param post 帖子信息
     * @return 结果
     */
    public int insertPost(SocialPost post);

    /**
     * 修改帖子
     *
     * @param post 帖子信息
     * @return 结果
     */
    public int updatePost(SocialPost post);

    /**
     * 删除帖子
     *
     * @param postId 帖子ID
     * @return 结果
     */
    public int deletePostById(@Param("postId") Long postId);

    /**
     * 增加点赞数
     *
     * @param postId 帖子ID
     * @return 结果
     */
    public int updateLikeCountAdd(@Param("postId") Long postId);

    /**
     * 减少点赞数
     *
     * @param postId 帖子ID
     * @return 结果
     */
    public int updateLikeCountSub(@Param("postId") Long postId);

    /**
     * 增加评论数
     *
     * @param postId 帖子ID
     * @return 结果
     */
    public int updateCommentCountAdd(@Param("postId") Long postId);

    /**
     * 减少评论数
     *
     * @param postId 帖子ID
     * @return 结果
     */
    public int updateCommentCountSub(@Param("postId") Long postId);
}
