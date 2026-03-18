<template>
  <div class="post-card">
    <!-- 帖子头部 -->
    <div class="post-header">
      <div class="user-info">
        <el-avatar :src="formatImageUrl(post.userAvatar)" :size="40"></el-avatar>
        <div class="user-details">
          <div class="user-name">{{ post.userName }}</div>
          <div class="post-time">{{ post.createTime }}</div>
        </div>
      </div>
      <el-button
        v-if="!showDelete"
        type="text"
        icon="el-icon-warning-outline"
        class="report-btn"
        @click="handleReport"
      >举报</el-button>
      <el-button
        v-else
        type="text"
        icon="el-icon-delete"
        class="delete-btn"
        @click="handleDelete"
      >删除</el-button>
    </div>

    <!-- 帖子内容 -->
    <div class="post-content">
      <div
        class="content-text"
        :class="{ 'content-collapsed': !isExpanded && isLongContent }"
      >
        <div v-html="post.content"></div>
      </div>
      <el-button
        v-if="isLongContent"
        type="text"
        class="expand-btn"
        @click="isExpanded = !isExpanded"
      >
        {{ isExpanded ? '收起' : '展开全文' }}
        <i :class="isExpanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
      </el-button>
    </div>

    <!-- 帖子图片 -->
    <div v-if="post.images && post.images.length > 0" class="post-images">
      <div
        class="image-grid"
        :class="`grid-${Math.min(post.images.length, 3)}`"
      >
        <div
          v-for="(img, index) in post.images"
          :key="index"
          class="image-wrapper"
          @click="showImagePreview(index)"
        >
          <img
            :src="formatImageUrl(img)"
            class="post-image"
          />
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="imagePreviewVisible"
      :on-close="closeImagePreview"
      :url-list="previewImageList"
      :initial-index="currentImageIndex"
    ></el-image-viewer>

    <!-- 标签 -->
    <div v-if="post.tags && post.tags.length > 0" class="post-tags">
      <el-tag
        v-for="tag in post.tags"
        :key="tag"
        size="small"
        type="info"
        class="post-tag"
      >#{{ tag }}</el-tag>
    </div>

    <!-- 点赞评论操作栏 -->
    <div class="post-actions">
      <el-button
        type="text"
        class="action-btn"
        :class="{ 'liked': post.isLiked }"
        @click="handleLike"
      >
        <i class="el-icon-star-on"></i>
        <span>{{ post.likeCount > 0 ? post.likeCount : '点赞' }}</span>
      </el-button>
      <el-button
        type="text"
        class="action-btn"
        @click="toggleComment"
      >
        <i class="el-icon-chat-dot-round"></i>
        <span>{{ post.commentCount > 0 ? post.commentCount + ' 条评论' : '评论' }}</span>
      </el-button>
    </div>

    <!-- 评论区 -->
    <comment-section
      :post-id="post.postId"
      :comments="post.comments || []"
      :show-input.sync="showCommentInput"
      :expand-all.sync="commentExpanded"
      @comment-added="handleCommentAdded"
    />

    <!-- 举报弹窗 -->
    <report-dialog
      :visible.sync="reportVisible"
      :post-id="post.postId"
    />
  </div>
</template>

<script>
import CommentSection from './CommentSection.vue'
import ReportDialog from './ReportDialog.vue'
import { likePost, unlikePost, delPost } from '@/api/social/post'

export default {
  name: 'PostCard',
  components: {
    CommentSection,
    ReportDialog
  },
  props: {
    post: {
      type: Object,
      required: true
    },
    showDelete: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isExpanded: false,
      showCommentInput: false,
      reportVisible: false,
      commentExpanded: false,
      imagePreviewVisible: false,
      currentImageIndex: 0
    }
  },
  computed: {
    isLongContent() {
      // 判断内容是否超过150字符
      const text = this.post.content ? this.post.content.replace(/<[^>]+>/g, '') : ''
      return text.length > 150
    },
    previewImageList() {
      return (this.post.images || []).map(img => this.formatImageUrl(img))
    }
  },
  methods: {
    handleLike() {
      const action = this.post.isLiked ? unlikePost : likePost
      action(this.post.postId).then(() => {
        this.$emit('like-changed', {
          postId: this.post.postId,
          isLiked: !this.post.isLiked
        })
      })
    },
    toggleComment() {
      this.showCommentInput = !this.showCommentInput
      // 点击评论按钮时，同时展开所有评论
      if (this.showCommentInput) {
        this.commentExpanded = true
      }
    },
    handleReport() {
      this.reportVisible = true
    },
    handleDelete() {
      this.$confirm('确定要删除这篇帖子吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delPost(this.post.postId).then(() => {
          this.$message.success('删除成功')
          this.$emit('post-deleted', this.post.postId)
        }).catch(() => {
          this.$message.error('删除失败')
        })
      })
    },
    handleCommentAdded() {
      this.$emit('comment-added', this.post.postId)
    },
    formatImageUrl(url) {
      // 如果URL已经包含 /dev-api 则直接返回，否则添加前缀
      return url.startsWith('/dev-api') ? url : `/dev-api${url}`
    },
    showImagePreview(index) {
      this.currentImageIndex = index
      this.imagePreviewVisible = true
    },
    closeImagePreview() {
      this.imagePreviewVisible = false
    }
  }
}
</script>

<style scoped lang="scss">
.post-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  }
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .user-info {
    display: flex;
    align-items: center;
    gap: 12px;

    .user-details {
      .user-name {
        font-weight: 600;
        color: #333;
        font-size: 15px;
      }

      .post-time {
        font-size: 12px;
        color: #999;
        margin-top: 2px;
      }
    }
  }

  .report-btn {
    color: #999;
    font-size: 13px;
    padding: 4px 8px;

    &:hover {
      color: #f56c6c;
    }
  }

  .delete-btn {
    color: #999;
    font-size: 13px;
    padding: 4px 8px;

    &:hover {
      color: #f56c6c;
    }
  }
}

.post-content {
  margin-bottom: 16px;

  .content-text {
    color: #555;
    line-height: 1.7;
    font-size: 15px;
    word-break: break-word;

    &.content-collapsed {
      max-height: 80px;
      overflow: hidden;
      position: relative;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 30px;
        background: linear-gradient(transparent, #fff);
      }
    }
  }

  .expand-btn {
    color: #409eff;
    font-size: 14px;
    padding: 4px 0;
  }
}

.post-images {
  margin-bottom: 16px;

  .image-grid {
    display: grid;
    gap: 4px;
    border-radius: 8px;
    overflow: hidden;

    &.grid-1 {
      grid-template-columns: 1fr;
      max-height: 400px;
    }

    &.grid-2 {
      grid-template-columns: 1fr 1fr;
      max-height: 250px;
    }

    &.grid-3 {
      grid-template-columns: 1fr 1fr 1fr;
      max-height: 220px;
    }

    .image-wrapper {
      width: 100%;
      height: 100%;
      cursor: pointer;
      overflow: hidden;
      border-radius: 4px;
      background: #f5f5f5;
    }

    .post-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }
  }
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;

  .post-tag {
    cursor: pointer;
    border-radius: 20px;
    background: #f0f7ff;
    border-color: #d0e8ff;
    color: #409eff;
  }
}

.post-actions {
  display: flex;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;

  .action-btn {
    color: #999;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 6px;

    i {
      font-size: 18px;
    }

    &:hover {
      color: #409eff;
    }

    &.liked {
      color: #f56c6c;

      i {
        color: #f56c6c;
      }
    }
  }
}

@media (max-width: 768px) {
  .post-card {
    padding: 14px;
    border-radius: 8px;
  }

  .post-images .image-grid {
    &.grid-1 {
      max-height: 200px;
    }

    &.grid-2 {
      max-height: 150px;
    }

    &.grid-3 {
      max-height: 120px;
    }
  }
}
</style>
