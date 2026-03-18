package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SocialReport;

/**
 * 社交举报 服务层
 *
 * @author ruoyi
 */
public interface ISocialReportService
{
    /**
     * 查询举报列表
     *
     * @param report 举报信息
     * @return 举报列表
     */
    public List<SocialReport> selectReportList(SocialReport report);

    /**
     * 查询举报详情
     *
     * @param reportId 举报ID
     * @return 举报详情
     */
    public SocialReport selectReportById(Long reportId);

    /**
     * 新增举报
     *
     * @param report 举报信息
     * @return 结果
     */
    public int insertReport(SocialReport report);

    /**
     * 审核不通过（违规删帖）
     *
     * @param reportId 举报ID
     * @param handleResult 审核结果说明
     * @param handleBy 审核人
     * @return 结果
     */
    public int handleViolation(Long reportId, String handleResult, String handleBy);

    /**
     * 审核通过（驳回举报）
     *
     * @param reportId 举报ID
     * @param handleResult 审核结果说明
     * @param handleBy 审核人
     * @return 结果
     */
    public int handleDismiss(Long reportId, String handleResult, String handleBy);

    /**
     * 检查用户是否已举报过该帖子
     *
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否已举报过
     */
    public boolean hasUserReportedPost(Long userId, Long postId);
}
