package com.ruoyi.system.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.SocialComment;
import com.ruoyi.system.mapper.SocialCommentMapper;
import com.ruoyi.system.mapper.SocialPostMapper;
import com.ruoyi.system.service.ISocialCommentService;

/**
 * 社交评论 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SocialCommentServiceImpl implements ISocialCommentService
{
    @Autowired
    private SocialCommentMapper socialCommentMapper;

    @Autowired
    private SocialPostMapper socialPostMapper;

    /**
     * 查询评论列表
     */
    @Override
    public List<SocialComment> selectCommentList(SocialComment comment)
    {
        List<SocialComment> list = socialCommentMapper.selectCommentList(comment);
        // 转换字符串到列表
        for (SocialComment c : list) {
            convertStringToList(c);
        }
        return list;
    }

    /**
     * 查询评论详情
     */
    @Override
    public SocialComment selectCommentById(Long commentId)
    {
        SocialComment comment = socialCommentMapper.selectCommentById(commentId);
        if (comment != null) {
            convertStringToList(comment);
        }
        return comment;
    }

    /**
     * 查询帖子的最新评论
     */
    @Override
    public List<SocialComment> selectCommentsByPostId(Long postId, Integer limit)
    {
        List<SocialComment> list = socialCommentMapper.selectCommentsByPostId(postId, limit);
        // 转换字符串到列表
        for (SocialComment c : list) {
            convertStringToList(c);
        }
        return list;
    }

    /**
     * 查询帖子的评论及回复（嵌套结构）
     */
    @Override
    public List<SocialComment> selectCommentsByPostIdWithReplies(Long postId, Integer limit)
    {
        // 先获取所有评论及回复
        List<SocialComment> allComments = socialCommentMapper.selectAllCommentsByPostId(postId);

        // 转换字符串到列表
        for (SocialComment c : allComments) {
            convertStringToList(c);
        }

        // 构建嵌套结构
        List<SocialComment> result = new java.util.ArrayList<>();
        java.util.Map<Long, SocialComment> commentMap = new java.util.HashMap<>();

        // 先将所有评论加入map，便于查找
        for (SocialComment comment : allComments) {
            commentMap.put(comment.getCommentId(), comment);
        }

        // 构建嵌套结构
        for (SocialComment comment : allComments) {
            if (comment.getParentId() == null) {
                // 顶级评论
                result.add(comment);
            } else {
                // 回复，添加到父评论的replies中
                SocialComment parentComment = commentMap.get(comment.getParentId());
                if (parentComment != null) {
                    if (parentComment.getReplies() == null) {
                        parentComment.setReplies(new java.util.ArrayList<>());
                    }
                    parentComment.getReplies().add(comment);
                }
            }
        }

        // 限制数量（只限制顶级评论数）
        if (limit != null && limit > 0 && result.size() > limit) {
            result = result.subList(0, limit);
        }

        return result;
    }

    /**
     * 新增评论
     */
    @Override
    @Transactional
    public int insertComment(SocialComment comment)
    {
        // 将List转换为逗号分隔字符串存储
        if (comment.getImages() != null && !comment.getImages().isEmpty()) {
            String imagesStr = comment.getImages().stream()
                .filter(image -> image != null && !image.trim().isEmpty())
                .collect(Collectors.joining(","));
            comment.setImagesStr(imagesStr.isEmpty() ? null : imagesStr);
        }

        comment.setStatus("0");
        int result = socialCommentMapper.insertComment(comment);
        if (result > 0) {
            // 更新帖子评论数
            socialPostMapper.updateCommentCountAdd(comment.getPostId());
        }
        return result;
    }

    /**
     * 修改评论
     */
    @Override
    public int updateComment(SocialComment comment)
    {
        return socialCommentMapper.updateComment(comment);
    }

    /**
     * 删除评论
     */
    @Override
    @Transactional
    public int deleteCommentById(Long commentId)
    {
        SocialComment comment = socialCommentMapper.selectCommentById(commentId);
        int result = socialCommentMapper.deleteCommentById(commentId);
        if (result > 0 && comment != null) {
            // 更新帖子评论数
            socialPostMapper.updateCommentCountSub(comment.getPostId());
        }
        return result;
    }

    /**
     * 转换字符串到列表
     */
    private void convertStringToList(SocialComment comment)
    {
        if (comment == null) {
            return;
        }
        // 转换images字符串到列表
        if (StringUtils.isNotEmpty(comment.getImagesStr())) {
            comment.setImages(Arrays.stream(comment.getImagesStr().split(","))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList()));
        }
    }
}
