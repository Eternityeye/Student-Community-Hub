package com.ruoyi.system.domain;

import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.config.StringToListDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * 社交帖子 social_post
 *
 * @author ruoyi
 */
public class SocialPost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 帖子ID */
    private Long postId;

    /** 用户ID */
    private Long userId;

    /** 帖子内容 */
    private String content;

    /** 图片地址列表 */
    @JsonDeserialize(using = StringToListDeserializer.class)
    private List<String> images;

    /** 标签列表 */
    @JsonDeserialize(using = StringToListDeserializer.class)
    private List<String> tags;

    /** 数据库中的图片字符串（中间字段） */
    private transient String imagesStr;

    /** 数据库中的标签字符串（中间字段） */
    private transient String tagsStr;

    /** 点赞数 */
    private Integer likeCount;

    /** 评论数 */
    private Integer commentCount;

    /** 状态（0正常 1已删除 2已屏蔽） */
    private String status;

    /** 用户名 */
    private String userName;

    /** 用户头像 */
    private String userAvatar;

    /** 是否点赞（非数据库字段） */
    private transient Boolean isLiked;

    /** 评论列表（非数据库字段） */
    private transient List<SocialComment> comments;

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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
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

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public List<SocialComment> getComments() {
        return comments;
    }

    public void setComments(List<SocialComment> comments) {
        this.comments = comments;
    }

    public String getImagesStr() {
        return imagesStr;
    }

    public void setImagesStr(String imagesStr) {
        this.imagesStr = imagesStr;
    }

    public String getTagsStr() {
        return tagsStr;
    }

    public void setTagsStr(String tagsStr) {
        this.tagsStr = tagsStr;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("postId", getPostId())
            .append("userId", getUserId())
            .append("content", getContent())
            .append("images", getImages())
            .append("tags", getTags())
            .append("likeCount", getLikeCount())
            .append("commentCount", getCommentCount())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
