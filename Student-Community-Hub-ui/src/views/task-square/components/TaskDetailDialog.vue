<template>
  <el-dialog
    title="任务详情"
    :visible.sync="visible"
    width="700px"
    @close="handleClose"
    append-to-body
  >
    <div v-if="task" class="task-detail">
      <!-- 基本信息 -->
      <div class="detail-section">
        <h3>{{ task.title }}</h3>
        <div class="meta-info">
          <span>发布者：{{ task.userName }}</span>
          <span>分类：<el-tag :type="getCategoryType(task.category)" size="small">{{ getCategoryName(task.category) }}</el-tag></span>
          <span>状态：<el-tag :type="getStatusType(task.status)" size="small">{{ getStatusName(task.status) }}</el-tag></span>
        </div>
      </div>

      <!-- 任务描述 -->
      <div class="detail-section">
        <h4>任务描述</h4>
        <p>{{ task.description }}</p>
      </div>

      <!-- 任务详情 -->
      <div class="detail-section">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">悬赏：</span>
              <span class="reward-value">{{ task.reward }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">地点：</span>
              <span>{{ task.location }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">截止时间：</span>
              <span>{{ formatDateTime(task.deadline) }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">发布时间：</span>
              <span>{{ formatDateTime(task.createTime) }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 任务图片 -->
      <div v-if="task.images && task.images.length > 0" class="detail-section">
        <h4>任务图片</h4>
        <div class="task-images">
          <img
            v-for="(image, index) in task.images"
            :key="index"
            :src="formatImageUrl(image)"
            @click="previewImage(index)"
            class="task-image"
          />
        </div>
      </div>

      <!-- 接单者信息 -->
      <div v-if="task.takerId" class="detail-section">
        <h4>接单者</h4>
        <div class="taker-info">
          <el-avatar :src="formatImageUrl(task.takerAvatar)" :size="40" />
          <div>
            <div class="name">{{ task.takerName }}</div>
            <div class="stats">
              <span v-if="takerStats && takerStats.total > 0">
                评价：{{ takerStats.total }}个 | 平均分：{{ takerStats.avgRating.toFixed(1) }}星
              </span>
              <span v-else>暂无评价</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 评价列表 -->
      <div v-if="task.myReviews && task.myReviews.length > 0" class="detail-section">
        <h4>评价信息</h4>
        <div v-for="review in task.myReviews" :key="review.reviewId" class="review-item">
          <div class="review-header">
            <span class="reviewer">{{ review.reviewerName }}</span>
            <el-rate v-model="review.rating" disabled class="review-rating" />
          </div>
          <div class="review-content">{{ review.content }}</div>
          <div class="review-time">{{ formatTime(review.createTime) }}</div>
        </div>
      </div>

      <!-- 操作按钮区域 -->
      <div v-if="showActionButtons" class="action-section">
        <el-button
          v-if="isCurrentUserPublisher && task.status === '2'"
          type="primary"
          @click="handleConfirm"
        >
          确认完成
        </el-button>
        <el-button
          v-else-if="isCurrentUserTaker && task.status === '1'"
          type="warning"
          @click="handleCompleteTask"
        >
          我已完成
        </el-button>
        <el-button
          v-if="canReview"
          type="primary"
          @click="showReviewDialog = true"
        >
          提交评价
        </el-button>
      </div>
    </div>

    <!-- 评价弹窗 -->
    <review-dialog
      v-if="task && task.takerId"
      v-model="showReviewDialog"
      :task-id="taskId"
      :reviewee-id="task.takerId"
      :role-type="roleType"
      @review-submitted="handleReviewSubmitted"
    />

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="imagePreviewVisible"
      :on-close="closeImagePreview"
      :url-list="imagePreviewList"
      :initial-index="currentImageIndex"
    />

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getTask, confirmTask, completeTask, getReviewStats } from '@/api/task/task'
import ReviewDialog from './ReviewDialog.vue'
import { formatTime, formatDateTime } from '@/utils/datetime'

export default {
  name: 'TaskDetailDialog',
  components: {
    ReviewDialog
  },
  props: {
    value: {
      type: Boolean,
      default: false
    },
    taskId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      task: null,
      loading: false,
      imagePreviewVisible: false,
      currentImageIndex: 0,
      showReviewDialog: false,
      takerStats: null
    }
  },
  computed: {
    visible: {
      get() {
        return this.value
      },
      set(val) {
        this.$emit('input', val)
      }
    },
    imagePreviewList() {
      return this.task && this.task.images ? this.task.images.map(img => this.formatImageUrl(img)) : []
    },
    isCurrentUserPublisher() {
      return this.task && this.$store.state.user.userId === this.task.userId
    },
    isCurrentUserTaker() {
      return this.task && this.$store.state.user.userId === this.task.takerId
    },
    canReview() {
      // 任务已关闭且当前用户是发布者或接单者
      if (!this.task || this.task.status !== '3') return false
      return this.isCurrentUserPublisher || this.isCurrentUserTaker
    },
    roleType() {
      // 发布者评接单者 = 0，接单者评发布者 = 1
      return this.isCurrentUserPublisher ? '0' : '1'
    },
    showActionButtons() {
      return this.isCurrentUserPublisher || this.isCurrentUserTaker
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.loadTask()
      }
    },
    taskId(newVal) {
      if (newVal && this.visible) {
        this.loadTask()
      }
    }
  },
  mounted() {
    if (this.visible) {
      this.loadTask()
    }
  },
  methods: {
    loadTask() {
      this.loading = true
      getTask(this.taskId).then(response => {
        this.task = response.data
        // 加载接单者的评价统计
        if (this.task && this.task.takerId) {
          getReviewStats(this.task.takerId).then(res => {
            this.takerStats = res.data
          }).catch(() => {})
        }
      }).finally(() => {
        this.loading = false
      })
    },
    formatImageUrl(url) {
      if (!url) return ''
      return url.startsWith('/dev-api') ? url : `/dev-api${url}`
    },
    formatTime(time) {
      return formatTime(time)
    },
    formatDateTime(time) {
      return formatDateTime(time)
    },
    getCategoryName(category) {
      const map = { '0': '取快递', '1': '占座', '2': '代购', '3': '答疑', '4': '其他' }
      return map[category] || '其他'
    },
    getCategoryType(category) {
      const map = { '0': 'info', '1': 'warning', '2': 'success', '3': 'primary', '4': '' }
      return map[category] || ''
    },
    getStatusName(status) {
      const map = { '0': '待接单', '1': '进行中', '2': '待确认', '3': '已关闭' }
      return map[status] || '未知'
    },
    getStatusType(status) {
      const map = { '0': 'success', '1': 'warning', '2': 'info', '3': 'danger' }
      return map[status] || ''
    },
    previewImage(index) {
      this.currentImageIndex = index
      this.imagePreviewVisible = true
    },
    closeImagePreview() {
      this.imagePreviewVisible = false
    },
    handleCompleteTask() {
      this.$confirm('确定任务已完成吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        completeTask(this.taskId).then(() => {
          this.$message.success('请等待发布者确认')
          this.loadTask()
        })
      }).catch(() => {})
    },
    handleConfirm() {
      this.$confirm(
        '请先验证接单者已完成任务！\n\n确认完成后将无法修改，且只能通过评价来反映问题。\n\n如有疑问，请拒绝确认并与对方沟通。',
        '确认任务完成',
        {
          confirmButtonText: '验证已完成，确认关闭',
          cancelButtonText: '取消',
          type: 'warning',
          center: true
        }
      ).then(() => {
        confirmTask(this.taskId).then(() => {
          this.$message.success('任务已关闭，现在可以互相评价')
          this.loadTask()
        })
      }).catch(() => {})
    },
    handleReviewSubmitted() {
      this.showReviewDialog = false
      this.loadTask()
    },
    handleClose() {
      this.visible = false
    }
  }
}
</script>

<style scoped>
.task-detail {
  /* 让 el-dialog 自己处理滚动 */
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.detail-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 8px;
}

.detail-section p {
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.meta-info {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #999;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.info-item {
  display: flex;
  gap: 8px;
  font-size: 13px;
  color: #666;
  margin-bottom: 12px;
}

.info-item .label {
  color: #999;
  min-width: fit-content;
}

.reward-value {
  color: #e6a23c;
  font-weight: 600;
}

.task-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.task-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.task-image:hover {
  opacity: 0.8;
}

.taker-info {
  display: flex;
  gap: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  align-items: flex-start;
}

.taker-info .name {
  font-weight: 600;
  color: #333;
}

.taker-info .stats {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.review-item {
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 12px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reviewer {
  font-weight: 600;
  font-size: 13px;
  color: #333;
}

.review-rating {
  font-size: 12px;
}

.review-content {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 8px;
}

.review-time {
  font-size: 12px;
  color: #999;
}

.action-section {
  display: flex;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.dialog-footer {
  text-align: right;
}
</style>
