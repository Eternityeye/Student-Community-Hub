<template>
  <div class="comment-section">
    <!-- 评论输入框 -->
    <div v-if="showInput" class="comment-input-wrapper">
      <div class="comment-input-box">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          maxlength="500"
          show-word-limit
        ></el-input>
        <div class="comment-actions">
          <div class="action-buttons">
            <el-button size="small" @click="cancelComment">取消</el-button>
            <el-button size="small" type="primary" @click="submitComment">发布</el-button>
          </div>
          <image-upload
            v-model="commentImages"
            :limit="3"
            :file-size="5"
          />
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div v-if="comments.length > 0" class="comment-list">
      <div
        v-for="(comment, index) in displayComments"
        :key="comment.commentId"
        class="comment-item"
      >
        <div class="comment-avatar">
          <el-avatar :src="comment.userAvatar" :size="36"></el-avatar>
        </div>
        <div class="comment-content-wrapper">
          <div class="comment-header">
            <span class="comment-user">{{ comment.userName }}</span>
            <span class="comment-time">{{ comment.createTime }}</span>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div v-if="comment.images && comment.images.length > 0" class="comment-images">
            <img
              v-for="(img, imgIndex) in comment.images"
              :key="imgIndex"
              :src="formatImageUrl(img)"
              class="comment-image"
              @click="showImagePreview(comment.images, imgIndex)"
            />
          </div>
          <div class="comment-actions-bar">
            <el-button
              type="text"
              size="small"
              @click="handleReply(comment)"
            >回复</el-button>
            <el-button
              v-if="comment.userId === $store.state.user.id"
              type="text"
              size="small"
              style="color: #f56c6c;"
              @click="handleDeleteComment(comment.commentId)"
            >删除</el-button>
          </div>

          <!-- 回复输入框 -->
          <div v-if="replyToParentId === comment.commentId" class="reply-input-wrapper">
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="2"
              :placeholder="`回复 ${replyingToComment ? replyingToComment.userName : comment.userName}...`"
              maxlength="200"
            ></el-input>
            <div class="reply-actions">
              <el-button size="mini" @click="cancelReply">取消</el-button>
              <el-button size="mini" type="primary" @click="submitReply">回复</el-button>
            </div>
          </div>

          <!-- 回复列表 -->
          <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
            <div
              v-for="reply in comment.replies"
              :key="reply.commentId"
              class="reply-item"
            >
              <div class="reply-header">
                <div class="reply-user-info">
                  <span class="reply-user">{{ reply.userName }}</span>
                  <span class="reply-to">回复</span>
                  <span class="reply-user">{{ reply.replyToUserName || comment.userName }}</span>
                </div>
                <span class="reply-time">{{ reply.createTime }}</span>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
              <div class="reply-button">
                <el-button
                  type="text"
                  size="small"
                  @click="handleReply(reply, comment.commentId)"
                >回复</el-button>
                <el-button
                  v-if="reply.userId === $store.state.user.id"
                  type="text"
                  size="small"
                  style="color: #f56c6c;"
                  @click="handleDeleteComment(reply.commentId)"
                >删除</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 查看更多评论 -->
      <div v-if="comments.length > 3 && !showAllComments && !expandAll" class="view-more">
        <el-button type="text" @click="showAllComments = true">
          查看全部 {{ comments.length }} 条评论
        </el-button>
      </div>
    </div>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="imagePreviewVisible"
      :on-close="closeImagePreview"
      :url-list="previewImages"
      :initial-index="currentImageIndex"
    ></el-image-viewer>
  </div>
</template>

<script>
import ImageUpload from '@/components/ImageUpload'
import { addComment, replyComment, delComment } from '@/api/social/post'

export default {
  name: 'CommentSection',
  components: {
    ImageUpload
  },
  props: {
    postId: {
      type: Number,
      required: true
    },
    comments: {
      type: Array,
      default: () => []
    },
    showInput: {
      type: Boolean,
      default: false
    },
    expandAll: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      commentContent: '',
      commentImages: '',
      replyContent: '',
      replyingToComment: null,  // 被回复的评论（可以是顶级评论或子评论）
      replyToParentId: null,    // 被回复评论所属的顶级评论ID
      showAllComments: false,
      imagePreviewVisible: false,
      currentImageIndex: 0,
      previewImages: []
    }
  },
  computed: {
    displayComments() {
      if (this.showAllComments || this.expandAll) {
        return this.comments
      }
      return this.comments.slice(0, 3)
    }
  },
  methods: {
    submitComment() {
      if (!this.commentContent.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }

      addComment({
        postId: this.postId,
        content: this.commentContent,
        images: this.commentImages
      }).then(response => {
        this.$message.success('评论成功')
        this.commentContent = ''
        this.commentImages = ''
        this.$emit('comment-added')
        this.$emit('update:showInput', false)
      })
    },
    cancelComment() {
      this.commentContent = ''
      this.commentImages = ''
      this.$emit('update:showInput', false)
    },
    handleReply(replyTarget, parentCommentId) {
      // replyTarget: 被回复的评论（顶级评论或子评论）
      // parentCommentId: 所属顶级评论的ID（用于定位输入框位置）
      this.replyingToComment = replyTarget
      this.replyToParentId = parentCommentId || replyTarget.commentId
      this.replyContent = ''
    },
    submitReply() {
      if (!this.replyContent.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }

      replyComment({
        postId: this.postId,
        commentId: this.replyingToComment.commentId,  // 被回复评论的ID
        content: this.replyContent
      }).then(response => {
        this.$message.success('回复成功')
        this.replyContent = ''
        this.replyingToComment = null
        this.replyToParentId = null
        this.$emit('comment-added')
      })
    },
    cancelReply() {
      this.replyingToComment = null
      this.replyToParentId = null
      this.replyContent = ''
    },
    handleDeleteComment(commentId) {
      this.$confirm('确定要删除这条评论吗？删除后无法恢复。', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delComment(commentId).then(response => {
          this.$message.success('删除成功')
          this.$emit('comment-added')
        }).catch(error => {
          this.$message.error(error.msg || '删除失败')
        })
      }).catch(() => {
        // 用户取消删除
      })
    },
    formatImageUrl(url) {
      return url.startsWith('/dev-api') ? url : `/dev-api${url}`
    },
    showImagePreview(images, index) {
      this.previewImages = images.map(img => this.formatImageUrl(img))
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
.comment-section {
  margin-top: 16px;
}

.comment-input-wrapper {
  margin-bottom: 16px;
}

.comment-input-box {
  background: #f7f8fa;
  padding: 12px;
  border-radius: 8px;
}

.comment-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 12px;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.comment-list {
  .comment-item {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;

    .comment-avatar {
      flex-shrink: 0;
    }

    .comment-content-wrapper {
      flex: 1;
      min-width: 0;
    }

    .comment-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 8px;

      .comment-user {
        font-weight: 500;
        color: #333;
      }

      .comment-time {
        font-size: 12px;
        color: #999;
      }
    }

    .comment-content {
      color: #666;
      line-height: 1.6;
      margin-bottom: 8px;
      word-break: break-word;
    }

    .comment-images {
      display: flex;
      gap: 8px;
      margin-bottom: 8px;

      .comment-image {
        width: 80px;
        height: 80px;
        border-radius: 4px;
        cursor: pointer;
        object-fit: cover;
        display: block;
      }
    }

    .comment-actions-bar {
      display: flex;
      gap: 12px;
      margin-bottom: 8px;
    }
  }
}

.reply-input-wrapper {
  margin-top: 12px;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.reply-list {
  margin-top: 12px;
  padding-left: 12px;
  border-left: 2px solid #e4e7ed;

  .reply-item {
    padding: 12px;
    margin-bottom: 8px;
    font-size: 14px;
    background: #f9f9f9;
    border-radius: 4px;

    .reply-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .reply-user-info {
        font-size: 13px;

        .reply-user {
          color: #409eff;
          font-weight: 500;
        }

        .reply-to {
          margin: 0 4px;
          color: #999;
        }
      }

      .reply-time {
        font-size: 12px;
        color: #999;
      }
    }

    .reply-content {
      color: #666;
      line-height: 1.6;
      word-break: break-word;
      margin-bottom: 8px;
    }

    .reply-button {
      display: flex;
      gap: 8px;
      justify-content: flex-end;
    }
  }
}

.view-more {
  text-align: center;
  padding: 12px 0;
  border-top: 1px solid #e4e7ed;
}

@media (max-width: 768px) {
  .comment-item {
    .comment-avatar {
      ::v-deep .el-avatar {
        width: 32px !important;
        height: 32px !important;
      }
    }
  }
}
</style>
