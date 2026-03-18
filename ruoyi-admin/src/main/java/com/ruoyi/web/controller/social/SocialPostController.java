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
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SocialPost;
import com.ruoyi.system.domain.SocialComment;
import com.ruoyi.system.service.ISocialPostService;
import com.ruoyi.system.service.ISocialCommentService;

/**
 * 社交帖子 控制层
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/social/post")
public class SocialPostController extends BaseController
{
    @Autowired
    private ISocialPostService socialPostService;

    @Autowired
    private ISocialCommentService socialCommentService;

    /**
     * 查询帖子列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SocialPost post)
    {
        startPage();
        List<SocialPost> list = socialPostService.selectPostList(post);
        // 为每个帖子加载最新3条评论（包括回复）
        for (SocialPost p : list) {
            List<SocialComment> comments = socialCommentService.selectCommentsByPostIdWithReplies(p.getPostId(), 3);
            p.setComments(comments);
        }
        return getDataTable(list);
    }

    /**
     * 查询个人帖子列表
     */
    @GetMapping("/my/list")
    public TableDataInfo myList(SocialPost post)
    {
        startPage();
        post.setUserId(SecurityUtils.getUserId());
        List<SocialPost> list = socialPostService.selectPostList(post);
        // 为每个帖子加载最新3条评论（包括回复）
        for (SocialPost p : list) {
            List<SocialComment> comments = socialCommentService.selectCommentsByPostIdWithReplies(p.getPostId(), 3);
            p.setComments(comments);
        }
        return getDataTable(list);
    }

    /**
     * 查询帖子详情
     */
    @GetMapping("/{postId}")
    public AjaxResult getInfo(@PathVariable("postId") Long postId)
    {
        SocialPost post = socialPostService.selectPostById(postId);
        if (post != null) {
            // 加载该帖子的所有评论（包括回复）
            List<SocialComment> comments = socialCommentService.selectCommentsByPostIdWithReplies(postId, 10);
            post.setComments(comments);
        }
        return AjaxResult.success(post);
    }

    /**
     * 新增帖子
     */
    @PostMapping
    @Log(title = "社交帖子", businessType = BusinessType.INSERT)
    public AjaxResult add(@RequestBody SocialPost post)
    {
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            return AjaxResult.error("帖子内容不能为空");
        }

        // 将前端的List转换为逗号分隔字符串存储到数据库
        // 注意：这里需要新增字段接收数据库的字符串格式

        post.setUserId(SecurityUtils.getUserId());
        post.setCreateBy(SecurityUtils.getUsername());
        int rows = socialPostService.insertPost(post);
        return toAjax(rows);
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{postId}")
    @Log(title = "社交帖子", businessType = BusinessType.DELETE)
    public AjaxResult remove(@PathVariable("postId") Long postId)
    {
        // 检查权限：只能删除自己的帖子或管理员可删除任何帖子
        SocialPost post = socialPostService.selectPostById(postId);
        if (post == null) {
            return AjaxResult.error("帖子不存在");
        }

        Long currentUserId = SecurityUtils.getUserId();
        if (!post.getUserId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            return AjaxResult.error("只能删除自己的帖子");
        }

        int rows = socialPostService.deletePostById(postId);
        return toAjax(rows);
    }

    /**
     * 点赞帖子
     */
    @PostMapping("/like/{postId}")
    @Log(title = "社交帖子", businessType = BusinessType.UPDATE)
    public AjaxResult likePost(@PathVariable("postId") Long postId)
    {
        Long userId = SecurityUtils.getUserId();
        int rows = socialPostService.likePost(postId, userId);
        if (rows > 0) {
            return AjaxResult.success("点赞成功");
        } else {
            return AjaxResult.error("已经点赞过");
        }
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/like/{postId}")
    @Log(title = "社交帖子", businessType = BusinessType.UPDATE)
    public AjaxResult unlikePost(@PathVariable("postId") Long postId)
    {
        Long userId = SecurityUtils.getUserId();
        int rows = socialPostService.unlikePost(postId, userId);
        return toAjax(rows);
    }
}
