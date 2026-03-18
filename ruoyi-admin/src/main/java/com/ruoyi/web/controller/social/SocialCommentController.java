package com.ruoyi.web.controller.social;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SocialComment;
import com.ruoyi.system.service.ISocialCommentService;

/**
 * 社交评论 控制层
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/social/comment")
public class SocialCommentController extends BaseController
{
    @Autowired
    private ISocialCommentService socialCommentService;

    /**
     * 查询评论列表
     */
    @GetMapping("/list/{postId}")
    public AjaxResult list(@PathVariable("postId") Long postId)
    {
        SocialComment comment = new SocialComment();
        comment.setPostId(postId);
        comment.setStatus("0");
        List<SocialComment> list = socialCommentService.selectCommentList(comment);
        return AjaxResult.success(list);
    }

    /**
     * 添加评论
     */
    @PostMapping
    @Log(title = "社交评论", businessType = BusinessType.INSERT)
    public AjaxResult add(@RequestBody SocialComment comment)
    {
        if (comment == null || comment.getContent() == null || comment.getContent().isEmpty()) {
            return AjaxResult.error("评论内容不能为空");
        }

        comment.setUserId(SecurityUtils.getUserId());
        comment.setCreateBy(SecurityUtils.getUsername());
        int rows = socialCommentService.insertComment(comment);
        return toAjax(rows);
    }

    /**
     * 回复评论
     */
    @PostMapping("/reply")
    @Log(title = "社交评论", businessType = BusinessType.INSERT)
    public AjaxResult reply(@RequestBody SocialComment comment)
    {
        if (comment == null || comment.getContent() == null || comment.getContent().isEmpty()) {
            return AjaxResult.error("评论内容不能为空");
        }

        if (comment.getCommentId() == null) {
            return AjaxResult.error("回复的评论ID不能为空");
        }

        // 获取被回复的评论信息
        SocialComment targetComment = socialCommentService.selectCommentById(comment.getCommentId());
        if (targetComment == null) {
            return AjaxResult.error("回复的评论不存在");
        }

        // 确保所有回复都直接挂在顶级评论下（两层平铺结构）
        // 如果被回复的评论本身是回复（parentId != null），则新回复的parentId指向其父（顶级评论ID）
        Long topParentId = targetComment.getParentId() != null
            ? targetComment.getParentId()
            : targetComment.getCommentId();

        comment.setPostId(targetComment.getPostId());
        comment.setParentId(topParentId);  // 指向顶级评论
        comment.setReplyToUserId(targetComment.getUserId());  // 被回复者的userId
        comment.setUserId(SecurityUtils.getUserId());
        comment.setCreateBy(SecurityUtils.getUsername());

        int rows = socialCommentService.insertComment(comment);
        return toAjax(rows);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    @Log(title = "社交评论", businessType = BusinessType.DELETE)
    public AjaxResult remove(@PathVariable("commentId") Long commentId)
    {
        // 检查权限：只能删除自己的评论或管理员可删除任何评论
        SocialComment comment = socialCommentService.selectCommentById(commentId);
        if (comment == null) {
            return AjaxResult.error("评论不存在");
        }

        Long currentUserId = SecurityUtils.getUserId();
        if (!comment.getUserId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            return AjaxResult.error("只能删除自己的评论");
        }

        int rows = socialCommentService.deleteCommentById(commentId);
        return toAjax(rows);
    }
}
