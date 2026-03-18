package com.ruoyi.system.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SocialReport;
import com.ruoyi.system.domain.SocialPost;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.mapper.SocialPostMapper;
import com.ruoyi.system.mapper.SocialReportMapper;
import com.ruoyi.system.service.ISocialReportService;
import com.ruoyi.system.service.ISysNoticeService;

/**
 * 社交举报 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SocialReportServiceImpl implements ISocialReportService
{
    @Autowired
    private SocialReportMapper socialReportMapper;

    @Autowired
    private SocialPostMapper socialPostMapper;

    @Autowired
    private ISysNoticeService sysNoticeService;

    @Autowired
    private RedisCache redisCache;

    private static final int REPORT_DAILY_LIMIT = 5;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public List<SocialReport> selectReportList(SocialReport report)
    {
        return socialReportMapper.selectReportList(report);
    }

    @Override
    public SocialReport selectReportById(Long reportId)
    {
        return socialReportMapper.selectReportById(reportId);
    }

    /**
     * 新增举报
     */
    @Override
    public int insertReport(SocialReport report)
    {
        // 检查当日举报次数是否超过限制
        if (!checkReportLimit(report.getUserId())) {
            return 0; // 超过限制
        }

        // 将前端的List转换为逗号分隔字符串存储到数据库
        if (report.getReasons() != null && !report.getReasons().isEmpty()) {
            String reasonsStr = report.getReasons().stream()
                .filter(reason -> reason != null && !reason.trim().isEmpty())
                .collect(Collectors.joining(","));
            report.setReasonsStr(reasonsStr.isEmpty() ? null : reasonsStr);
        }

        report.setStatus("0");
        int result = socialReportMapper.insertReport(report);

        if (result > 0) {
            // 更新Redis计数
            String key = buildReportKey(report.getUserId());
            if (redisCache.hasKey(key)) {
                Integer count = redisCache.getCacheObject(key);
                redisCache.setCacheObject(key, count == null ? 1 : count + 1, 1, TimeUnit.DAYS);
            } else {
                redisCache.setCacheObject(key, 1, 1, TimeUnit.DAYS);
            }
        }

        return result;
    }

    /**
     * 审核不通过（违规删帖）
     */
    @Override
    @Transactional
    public int handleViolation(Long reportId, String handleResult, String handleBy)
    {
        // 查询举报详情
        SocialReport report = socialReportMapper.selectReportById(reportId);
        if (report == null) {
            return 0;
        }

        // 查询被举报帖子
        SocialPost post = socialPostMapper.selectPostById(report.getPostId());
        if (post == null) {
            return 0;
        }

        // 更新举报状态为已处理（违规）
        report.setStatus("1");
        report.setHandleBy(handleBy);
        report.setHandleTime(new Date());
        report.setHandleResult(handleResult);
        socialReportMapper.updateReport(report);

        // 逻辑删除帖子（标记为已删除状态）
        SocialPost updatePost = new SocialPost();
        updatePost.setPostId(report.getPostId());
        updatePost.setStatus("1");  // 1 = 已删除
        socialPostMapper.updatePost(updatePost);

        // 向违规用户发通知
        SysNotice notice = new SysNotice();
        notice.setNoticeTitle("您的帖子因违规已被删除");
        notice.setNoticeType("1");
        notice.setRecipientUserId(post.getUserId());  // 只有被举报人能看到
        String postPreview = post.getContent();
        if (postPreview != null && postPreview.length() > 100) {
            postPreview = postPreview.substring(0, 100) + "...";
        }
        String noticeContent = String.format(
            "您发布的帖子已被其他用户举报，经管理员审核认定违规，已予以删除。\n\n举报原因：%s\n审核意见：%s\n帖子内容：%s\n\n请遵守社区规范，诚心发帖。",
            report.getReasons() != null ? String.join("、", report.getReasons()) : "内容违规",
            handleResult != null ? handleResult : "内容违规",
            postPreview);
        notice.setNoticeContent(noticeContent);
        notice.setStatus("0");
        notice.setCreateBy(handleBy);
        sysNoticeService.insertNotice(notice);

        return 1;
    }

    /**
     * 审核通过（驳回举报）
     */
    @Override
    public int handleDismiss(Long reportId, String handleResult, String handleBy)
    {
        SocialReport report = socialReportMapper.selectReportById(reportId);
        if (report == null) {
            return 0;
        }

        // 更新举报状态为已处理（驳回）
        report.setStatus("2");
        report.setHandleBy(handleBy);
        report.setHandleTime(new Date());
        report.setHandleResult(handleResult);
        return socialReportMapper.updateReport(report);
    }

    /**
     * 检查当日举报限制
     */
    private boolean checkReportLimit(Long userId)
    {
        String key = buildReportKey(userId);
        Integer count = redisCache.getCacheObject(key);
        return count == null || count < REPORT_DAILY_LIMIT;
    }

    /**
     * 检查用户是否已举报过该帖子
     */
    @Override
    public boolean hasUserReportedPost(Long userId, Long postId)
    {
        if (userId == null || postId == null) {
            return false;
        }

        SocialReport report = new SocialReport();
        report.setUserId(userId);
        report.setPostId(postId);
        List<SocialReport> reports = socialReportMapper.selectReportList(report);
        return reports != null && !reports.isEmpty();
    }

    /**
     * 构建Redis Key
     */
    private String buildReportKey(Long userId)
    {
        String date = LocalDate.now().format(DATE_FORMATTER);
        return String.format("report:user:%d:date:%s", userId, date);
    }
}
