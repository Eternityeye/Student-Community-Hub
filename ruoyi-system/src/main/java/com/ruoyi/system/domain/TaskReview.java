package com.ruoyi.system.domain;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 任务评价 task_review
 *
 * @author ruoyi
 */
public class TaskReview implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 评价ID */
    private Long reviewId;

    /** 任务ID */
    private Long taskId;

    /** 评价者用户ID */
    private Long reviewerId;

    /** 被评价者用户ID */
    private Long revieweeId;

    /** 评价类型（0发布者评接单者 1接单者评发布者） */
    private String roleType;

    /** 星级评分（1-5） */
    private Integer rating;

    /** 评价内容 */
    private String content;

    /** 创建时间 */
    private Date createTime;

    /** 评价者名称（非DB字段） */
    private transient String reviewerName;

    /** 评价者头像（非DB字段） */
    private transient String reviewerAvatar;

    /** 被评价者名称（非DB字段） */
    private transient String revieweeName;

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Long getRevieweeId() {
        return revieweeId;
    }

    public void setRevieweeId(Long revieweeId) {
        this.revieweeId = revieweeId;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerAvatar() {
        return reviewerAvatar;
    }

    public void setReviewerAvatar(String reviewerAvatar) {
        this.reviewerAvatar = reviewerAvatar;
    }

    public String getRevieweeName() {
        return revieweeName;
    }

    public void setRevieweeName(String revieweeName) {
        this.revieweeName = revieweeName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("reviewId", getReviewId())
            .append("taskId", getTaskId())
            .append("reviewerId", getReviewerId())
            .append("revieweeId", getRevieweeId())
            .append("roleType", getRoleType())
            .append("rating", getRating())
            .append("content", getContent())
            .append("createTime", getCreateTime())
            .toString();
    }
}
