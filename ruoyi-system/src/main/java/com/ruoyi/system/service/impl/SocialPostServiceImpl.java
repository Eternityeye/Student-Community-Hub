package com.ruoyi.system.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SocialLike;
import com.ruoyi.system.domain.SocialPost;
import com.ruoyi.system.mapper.SocialLikeMapper;
import com.ruoyi.system.mapper.SocialPostMapper;
import com.ruoyi.system.service.ISocialPostService;

/**
 * 社交帖子 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SocialPostServiceImpl implements ISocialPostService
{
    @Autowired
    private SocialPostMapper socialPostMapper;

    @Autowired
    private SocialLikeMapper socialLikeMapper;

    /**
     * 查询帖子列表
     */
    @Override
    public List<SocialPost> selectPostList(SocialPost post)
    {
        List<SocialPost> list = socialPostMapper.selectPostList(post);
        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getUserId();

        // 转换字符串到列表，并设置isLiked字段
        for (SocialPost p : list) {
            convertStringToList(p);
            // 设置当前用户是否点赞
            if (currentUserId != null) {
                SocialLike like = socialLikeMapper.selectLikeByPostAndUser(p.getPostId(), currentUserId);
                p.setIsLiked(like != null);
            } else {
                p.setIsLiked(false);
            }
        }
        return list;
    }

    /**
     * 查询帖子详情
     */
    @Override
    public SocialPost selectPostById(Long postId)
    {
        SocialPost post = socialPostMapper.selectPostById(postId);
        if (post != null) {
            convertStringToList(post);
            // 设置当前用户是否点赞
            Long currentUserId = SecurityUtils.getUserId();
            if (currentUserId != null) {
                SocialLike like = socialLikeMapper.selectLikeByPostAndUser(postId, currentUserId);
                post.setIsLiked(like != null);
            } else {
                post.setIsLiked(false);
            }
        }
        return post;
    }

    /**
     * 新增帖子
     */
    @Override
    public int insertPost(SocialPost post)
    {
        // 将List转换为逗号分隔字符串存储
        if (post.getTags() != null && !post.getTags().isEmpty()) {
            String tagsStr = post.getTags().stream()
                .filter(tag -> tag != null && !tag.trim().isEmpty())
                .collect(Collectors.joining(","));
            post.setTagsStr(tagsStr.isEmpty() ? null : tagsStr);
        }

        if (post.getImages() != null && !post.getImages().isEmpty()) {
            String imagesStr = post.getImages().stream()
                .filter(image -> image != null && !image.trim().isEmpty())
                .collect(Collectors.joining(","));
            post.setImagesStr(imagesStr.isEmpty() ? null : imagesStr);
        }

        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus("0");
        return socialPostMapper.insertPost(post);
    }

    /**
     * 修改帖子
     */
    @Override
    public int updatePost(SocialPost post)
    {
        return socialPostMapper.updatePost(post);
    }

    /**
     * 删除帖子
     */
    @Override
    public int deletePostById(Long postId)
    {
        return socialPostMapper.deletePostById(postId);
    }

    /**
     * 点赞帖子
     */
    @Override
    @Transactional
    public int likePost(Long postId, Long userId)
    {
        // 检查是否已经点赞
        SocialLike existLike = socialLikeMapper.selectLikeByPostAndUser(postId, userId);
        if (existLike != null) {
            return 0; // 已经点赞
        }

        // 插入点赞记录
        SocialLike like = new SocialLike();
        like.setPostId(postId);
        like.setUserId(userId);
        socialLikeMapper.insertLike(like);

        // 更新帖子点赞数
        socialPostMapper.updateLikeCountAdd(postId);
        return 1;
    }

    /**
     * 取消点赞
     */
    @Override
    @Transactional
    public int unlikePost(Long postId, Long userId)
    {
        // 删除点赞记录
        int result = socialLikeMapper.deleteLikeByPostAndUser(postId, userId);
        if (result > 0) {
            // 更新帖子点赞数
            socialPostMapper.updateLikeCountSub(postId);
        }
        return result;
    }

    /**
     * 转换字符串到列表
     */
    private void convertStringToList(SocialPost post)
    {
        if (post == null) {
            return;
        }
        // 转换images字符串到列表
        if (StringUtils.isNotEmpty(post.getImagesStr())) {
            post.setImages(Arrays.stream(post.getImagesStr().split(","))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList()));
        }
        // 转换tags字符串到列表
        if (StringUtils.isNotEmpty(post.getTagsStr())) {
            post.setTags(Arrays.stream(post.getTagsStr().split(","))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList()));
        }
    }
}
