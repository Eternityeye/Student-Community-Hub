package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.config.StringToListDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.system.domain.TaskReview;

/**
 * 悬赏任务 reward_task
 *
 * @author ruoyi
 */
public class RewardTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long taskId;

    /** 发布者用户ID */
    private Long userId;

    /** 任务标题 */
    private String title;

    /** 任务分类（0取快递 1占座 2代购 3答疑 4其他） */
    private String category;

    /** 任务描述 */
    private String description;

    /** 悬赏说明 */
    private String reward;

    /** 任务地点 */
    private String location;

    /** 截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    /** 接单者用户ID */
    private Long takerId;

    /** 任务状态（0待接单 1进行中 2已完成待确认 3已关闭） */
    private String status;

    /** 图片地址列表 */
    @JsonDeserialize(using = StringToListDeserializer.class)
    private List<String> images;

    /** 数据库中的图片字符串（中间字段） */
    private transient String imagesStr;

    /** 发布者名称（非数据库字段） */
    private transient String userName;

    /** 发布者头像（非数据库字段） */
    private transient String userAvatar;

    /** 接单者名称（非数据库字段） */
    private transient String takerName;

    /** 接单者头像（非数据库字段） */
    private transient String takerAvatar;

    /** 我的评价（非数据库字段） */
    private transient List<TaskReview> myReviews;

    /** 是否已评价（非数据库字段） */
    private transient Boolean isReviewed;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Long getTakerId() {
        return takerId;
    }

    public void setTakerId(Long takerId) {
        this.takerId = takerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getImagesStr() {
        return imagesStr;
    }

    public void setImagesStr(String imagesStr) {
        this.imagesStr = imagesStr;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getTakerName() {
        return takerName;
    }

    public void setTakerName(String takerName) {
        this.takerName = takerName;
    }

    public String getTakerAvatar() {
        return takerAvatar;
    }

    public void setTakerAvatar(String takerAvatar) {
        this.takerAvatar = takerAvatar;
    }

    public List<TaskReview> getMyReviews() {
        return myReviews;
    }

    public void setMyReviews(List<TaskReview> myReviews) {
        this.myReviews = myReviews;
    }

    public Boolean getIsReviewed() {
        return isReviewed;
    }

    public void setIsReviewed(Boolean isReviewed) {
        this.isReviewed = isReviewed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("userId", getUserId())
            .append("title", getTitle())
            .append("category", getCategory())
            .append("description", getDescription())
            .append("reward", getReward())
            .append("location", getLocation())
            .append("deadline", getDeadline())
            .append("takerId", getTakerId())
            .append("status", getStatus())
            .append("images", getImages())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
