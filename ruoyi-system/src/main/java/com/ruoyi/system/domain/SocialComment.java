package com.ruoyi.system.domain;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.config.StringToListDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * 社交评论 social_comment
 *
 * @author ruoyi
 */
public class SocialComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long commentId;

    /** 帖子ID */
    private Long postId;

    /** 用户ID */
    private Long userId;

    /** 父评论ID */
    private Long parentId;

    /** 回复的用户ID */
    private Long replyToUserId;

    /** 评论内容 */
    private String content;

    /** 评论图片列表 */
    @JsonDeserialize(using = StringToListDeserializer.class)
    private List<String> images;

    /** 数据库中的图片字符串（中间字段） */
    private transient String imagesStr;

    /** 状态（0正常 1已删除） */
    private String status;

    /** 用户名 */
    private String userName;

    /** 用户头像 */
    private String userAvatar;

    /** 被回复用户名 */
    private transient String replyToUserName;

    /** 回复列表（非数据库字段） */
    private transient List<SocialComment> replies;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(Long replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getReplyToUserName() {
        return replyToUserName;
    }

    public void setReplyToUserName(String replyToUserName) {
        this.replyToUserName = replyToUserName;
    }

    public List<SocialComment> getReplies() {
        return replies;
    }

    public void setReplies(List<SocialComment> replies) {
        this.replies = replies;
    }

    public String getImagesStr() {
        return imagesStr;
    }

    public void setImagesStr(String imagesStr) {
        this.imagesStr = imagesStr;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("commentId", getCommentId())
            .append("postId", getPostId())
            .append("userId", getUserId())
            .append("parentId", getParentId())
            .append("replyToUserId", getReplyToUserId())
            .append("content", getContent())
            .append("images", getImages())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
