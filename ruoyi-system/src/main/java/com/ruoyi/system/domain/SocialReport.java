package com.ruoyi.system.domain;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.config.StringToListDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * 社交举报 social_report
 *
 * @author ruoyi
 */
public class SocialReport implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 举报ID */
    private Long reportId;

    /** 帖子ID */
    private Long postId;

    /** 举报用户ID */
    private Long userId;

    /** 举报原因列表 */
    @JsonDeserialize(using = StringToListDeserializer.class)
    private java.util.List<String> reasons;

    /** 数据库中的原因字符串（中间字段） */
    private transient String reasonsStr;

    /** 详细描述 */
    private String description;

    /** 处理状态（0待处理 1已处理 2已驳回） */
    private String status;

    /** 处理人 */
    private String handleBy;

    /** 处理时间 */
    private Date handleTime;

    /** 处理结果 */
    private String handleResult;

    /** 创建时间 */
    private Date createTime;

    /** 被举报帖子内容摘要（非DB字段） */
    private transient String postContent;

    /** 举报用户名（非DB字段） */
    private transient String reportUserName;

    /** 被举报帖子作者名（非DB字段） */
    private transient String postUserName;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public java.util.List<String> getReasons() {
        return reasons;
    }

    public void setReasons(java.util.List<String> reasons) {
        this.reasons = reasons;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHandleBy() {
        return handleBy;
    }

    public void setHandleBy(String handleBy) {
        this.handleBy = handleBy;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReasonsStr() {
        return reasonsStr;
    }

    public void setReasonsStr(String reasonsStr) {
        this.reasonsStr = reasonsStr;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getReportUserName() {
        return reportUserName;
    }

    public void setReportUserName(String reportUserName) {
        this.reportUserName = reportUserName;
    }

    public String getPostUserName() {
        return postUserName;
    }

    public void setPostUserName(String postUserName) {
        this.postUserName = postUserName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("reportId", getReportId())
            .append("postId", getPostId())
            .append("userId", getUserId())
            .append("reasons", getReasons())
            .append("description", getDescription())
            .append("status", getStatus())
            .append("handleBy", getHandleBy())
            .append("handleTime", getHandleTime())
            .append("handleResult", getHandleResult())
            .append("createTime", getCreateTime())
            .toString();
    }
}
