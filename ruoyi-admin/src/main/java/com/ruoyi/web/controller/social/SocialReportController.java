package com.ruoyi.web.controller.social;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SocialReport;
import com.ruoyi.system.service.ISocialReportService;

/**
 * 社交举报 控制层
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/social/report")
public class SocialReportController extends BaseController
{
    @Autowired
    private ISocialReportService socialReportService;

    /**
     * 用户举报帖子
     */
    @PostMapping
    @Log(title = "社交举报", businessType = BusinessType.INSERT)
    public AjaxResult report(@RequestBody SocialReport report)
    {
        if (report == null || report.getPostId() == null) {
            return AjaxResult.error("帖子ID不能为空");
        }

        if (report.getReasons() == null || report.getReasons().isEmpty()) {
            return AjaxResult.error("举报原因不能为空");
        }

        report.setUserId(SecurityUtils.getUserId());
        int rows = socialReportService.insertReport(report);
        if (rows > 0) {
            return AjaxResult.success("举报成功");
        } else {
            return AjaxResult.error("每日举报次数已超过限制（每日最多5次）");
        }
    }

    /**
     * 检查用户是否已举报过该帖子
     */
    @GetMapping("/check/{postId}")
    public AjaxResult checkReported(@PathVariable("postId") Long postId)
    {
        if (postId == null) {
            return AjaxResult.error("帖子ID不能为空");
        }

        Long userId = SecurityUtils.getUserId();
        boolean hasReported = socialReportService.hasUserReportedPost(userId, postId);
        return AjaxResult.success(hasReported);
    }

    /**
     * 管理员：查询举报列表（分页）
     */
    @GetMapping("/list")
    public TableDataInfo list(SocialReport report)
    {
        startPage();
        List<SocialReport> list = socialReportService.selectReportList(report);
        return getDataTable(list);
    }

    /**
     * 管理员：查看举报详情
     */
    @GetMapping("/{reportId}")
    public AjaxResult getInfo(@PathVariable("reportId") Long reportId)
    {
        SocialReport report = socialReportService.selectReportById(reportId);
        return AjaxResult.success(report);
    }

    /**
     * 管理员：审核不通过（违规删帖）
     */
    @PutMapping("/handle/violation/{reportId}")
    @Log(title = "举报审核", businessType = BusinessType.UPDATE)
    public AjaxResult handleViolation(@PathVariable("reportId") Long reportId, @RequestBody Map<String, String> body)
    {
        String handleResult = body.get("handleResult");
        if (handleResult == null || handleResult.trim().isEmpty()) {
            return AjaxResult.error("审核意见不能为空");
        }

        int rows = socialReportService.handleViolation(reportId, handleResult, SecurityUtils.getUsername());
        if (rows > 0) {
            return AjaxResult.success("审核成功，帖子已删除，用户已通知");
        } else {
            return AjaxResult.error("审核失败");
        }
    }

    /**
     * 管理员：审核通过（驳回举报）
     */
    @PutMapping("/handle/dismiss/{reportId}")
    @Log(title = "举报审核", businessType = BusinessType.UPDATE)
    public AjaxResult handleDismiss(@PathVariable("reportId") Long reportId, @RequestBody Map<String, String> body)
    {
        String handleResult = body.get("handleResult");
        if (handleResult == null || handleResult.trim().isEmpty()) {
            return AjaxResult.error("审核意见不能为空");
        }

        int rows = socialReportService.handleDismiss(reportId, handleResult, SecurityUtils.getUsername());
        if (rows > 0) {
            return AjaxResult.success("审核成功，举报已驳回");
        } else {
            return AjaxResult.error("审核失败");
        }
    }
}
