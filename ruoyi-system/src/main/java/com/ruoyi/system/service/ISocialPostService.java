package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SocialPost;

/**
 * 社交帖子 服务层
 *
 * @author ruoyi
 */
public interface ISocialPostService
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
    public SocialPost selectPostById(Long postId);

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
    public int deletePostById(Long postId);

    /**
     * 点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    public int likePost(Long postId, Long userId);

    /**
     * 取消点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    public int unlikePost(Long postId, Long userId);
}
