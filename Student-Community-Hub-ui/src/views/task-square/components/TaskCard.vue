<template>
  <div class="task-card">
    <div class="task-header-section">
      <div class="user-info">
        <el-avatar :src="formatImageUrl(task.userAvatar)" :size="40" />
        <div class="user-details">
          <div class="user-name">{{ task.userName }}</div>
          <div class="publish-time">{{ formatDateTime(task.createTime) }}</div>
        </div>
      </div>
      <div class="header-tags">
        <el-tag :type="getCategoryType(task.category)" size="small">
          {{ getCategoryName(task.category) }}
        </el-tag>
        <el-tag :type="getStatusType(task.status)" size="small">
          {{ getStatusName(task.status) }}
        </el-tag>
      </div>
    </div>

    <div class="task-content">
      <h3 class="task-title">{{ task.title }}</h3>
      <p class="task-description">{{ task.description }}</p>

      <!-- 任务图片 -->
      <div v-if="task.images && task.images.length > 0" class="task-images" :class="'grid-' + task.images.length">
        <img
          v-for="(image, index) in task.images"
          :key="index"
          :src="formatImageUrl(image)"
          @click="previewImage(index)"
          class="task-image"
        />
      </div>

      <!-- 任务详情 -->
      <div class="task-details-row">
        <div class="detail-item">
          <span class="label">悬赏：</span>
          <span class="reward-value">{{ task.reward }}</span>
        </div>
        <div class="detail-item">
          <span class="label">地点：</span>
          <span>{{ task.location }}</span>
        </div>
      </div>

      <div class="task-details-row">
        <div class="detail-item">
          <span class="label">截止时间：</span>
          <span>{{ formatDateTime(task.deadline) }}</span>
        </div>
      </div>
    </div>

    <!-- 接单者信息 -->
    <div v-if="task.takerId && task.takerName" class="taker-info">
      <span class="label">接单者：</span>
      <el-avatar :src="formatImageUrl(task.takerAvatar)" :size="24" />
      <span>{{ task.takerName }}</span>
    </div>

    <div class="action-bar">
      <div class="action-left">
        <el-button-group>
          <el-button v-if="!isCurrentUserPublisher && task.status === '0'" size="small" type="primary" @click="handleTakeTask">
            接单
          </el-button>
          <el-button v-if="isCurrentUserPublisher && task.status === '0'" size="small" type="danger" @click="handleDelete">
            删除任务
          </el-button>
          <el-button v-if="isCurrentUserTaker && task.status === '1'" size="small" type="warning" @click="handleCompleteTask">
            我已完成
          </el-button>
          <el-button v-if="isCurrentUserPublisher && task.status === '2'" size="small" type="success" @click="handleConfirm">
            确认完成
          </el-button>
          <el-button
            v-if="(isCurrentUserPublisher || isCurrentUserTaker) && ['2', '3'].includes(task.status)"
            size="small"
            type="danger"
            @click="handleCreateDispute"
          >
            发起纠纷
          </el-button>
        </el-button-group>
      </div>
      <div class="action-right">
        <el-button size="small" @click="handleViewDetail">
          详情
        </el-button>
      </div>
    </div>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="imagePreviewVisible"
      :on-close="closeImagePreview"
      :url-list="imagePreviewList"
      :initial-index="currentImageIndex"
    />

    <!-- 任务详情弹窗 -->
    <task-detail-dialog v-model="showDetailDialog" :task-id="task.taskId" />

    <!-- 发起纠纷弹窗 -->
    <create-dispute-dialog
      :visible.sync="showDisputeDialog"
      :task-id="task.taskId"
      :task-title="task.title"
      :opposite-user-id="oppositeUserId"
      :opposite-user-name="oppositeUserName"
      :my-role="myRole"
      @success="handleDisputeSuccess"
    />
  </div>
</template>

<script>
import { delTask, takeTask, completeTask, confirmTask } from '@/api/task/task'
import TaskDetailDialog from './TaskDetailDialog.vue'
import CreateDisputeDialog from './CreateDisputeDialog.vue'
import { formatTime, formatDateTime } from '@/utils/datetime'

export default {
  name: 'TaskCard',
  components: {
    TaskDetailDialog,
    CreateDisputeDialog
  },
  props: {
    task: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      imagePreviewVisible: false,
      currentImageIndex: 0,
      showDetailDialog: false,
      showDisputeDialog: false
    }
  },
  computed: {
    imagePreviewList() {
      return this.task.images ? this.task.images.map(img => this.formatImageUrl(img)) : []
    },
    isCurrentUserPublisher() {
      return this.$store.state.user.userId === this.task.userId
    },
    isCurrentUserTaker() {
      return this.$store.state.user.userId === this.task.takerId
    },
    oppositeUserId() {
      // 获取对方用户ID
      if (this.isCurrentUserPublisher) {
        return this.task.takerId
      } else {
        return this.task.userId
      }
    },
    oppositeUserName() {
      // 获取对方用户名
      if (this.isCurrentUserPublisher) {
        return this.task.takerName
      } else {
        return this.task.userName
      }
    },
    myRole() {
      return this.isCurrentUserPublisher ? 'publisher' : 'taker'
    }
  },
  methods: {
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
    handleTakeTask() {
      this.$confirm('确定要接单这个任务吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        takeTask(this.task.taskId).then(() => {
          this.$message.success('接单成功')
          this.$emit('task-taken')
        })
      }).catch(() => {})
    },
    handleCompleteTask() {
      this.$confirm('确定任务已完成吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        completeTask(this.task.taskId).then(() => {
          this.$message.success('请等待发布者确认')
          this.$emit('task-taken')
        })
      }).catch(() => {})
    },
    handleConfirm() {
      this.$confirm('确认任务已完成？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        confirmTask(this.task.taskId).then(() => {
          this.$message.success('任务已关闭，可进行评价')
          this.$emit('task-taken')
        })
      }).catch(() => {})
    },
    handleDelete() {
      this.$confirm('确定删除这个任务吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'danger'
      }).then(() => {
        delTask(this.task.taskId).then(() => {
          this.$message.success('删除成功')
          this.$emit('task-deleted', this.task.taskId)
        })
      }).catch(() => {})
    },
    handleViewDetail() {
      this.showDetailDialog = true
    },
    handleCreateDispute() {
      this.showDisputeDialog = true
    },
    handleDisputeSuccess() {
      this.$message.success('纠纷已提交，请等待管理员审理')
      // 刷新任务数据
      this.$emit('task-taken')
    }
  }
}
</script>

<style scoped>
.task-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.task-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.task-header-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.user-details {
  min-width: 0;
  flex: 1;
}

.user-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.publish-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.header-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  justify-content: flex-end;
  flex-shrink: 0;
}

.header-tags .el-tag {
  white-space: nowrap;
}

.task-content {
  margin-bottom: 12px;
}

.task-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
  word-break: break-word;
  line-height: 1.4;
}

.task-description {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 12px 0;
  word-break: break-word;
}

.task-images {
  display: grid;
  gap: 8px;
  margin: 12px 0;
  padding: 0;
  background-color: transparent;
  border-radius: 0;
}

.grid-1 {
  grid-template-columns: minmax(200px, 300px);
}

.grid-1 .task-image {
  width: 100%;
  height: auto;
  max-height: 300px;
}

.grid-2 {
  grid-template-columns: repeat(2, minmax(120px, 180px));
}

.grid-2 .task-image {
  width: 100%;
  height: auto;
  max-height: 180px;
}

.grid-3,
.grid-4,
.grid-5,
.grid-6,
.grid-7,
.grid-8,
.grid-9 {
  grid-template-columns: repeat(3, minmax(100px, 150px));
}

.grid-3 .task-image,
.grid-4 .task-image,
.grid-5 .task-image,
.grid-6 .task-image,
.grid-7 .task-image,
.grid-8 .task-image,
.grid-9 .task-image {
  width: 100%;
  height: auto;
  max-height: 150px;
}

.task-image {
  object-fit: contain;
  border-radius: 4px;
  cursor: pointer;
  transition: opacity 0.3s;
  background-color: transparent;
  display: block;
}

.task-image:hover {
  opacity: 0.8;
}

.task-details-row {
  display: flex;
  gap: 24px;
  margin-bottom: 10px;
  font-size: 13px;
  flex-wrap: wrap;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  min-width: 0;
  flex: 0 1 auto;
}

.detail-item .label {
  color: #999;
  min-width: fit-content;
  flex-shrink: 0;
}

.detail-item span:not(.label) {
  word-break: break-word;
}

.reward-value {
  color: #e6a23c;
  font-weight: 600;
}

.taker-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #666;
}

.taker-info .label {
  color: #999;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  gap: 12px;
}

.action-left {
  flex: 1;
}

.action-right {
  white-space: nowrap;
}

/* 平板响应式 */
@media (max-width: 768px) {
  .task-card {
    padding: 12px;
    margin-bottom: 10px;
  }

  .task-header-section {
    flex-wrap: wrap;
    gap: 8px;
  }

  .user-info {
    flex-basis: 100%;
  }

  .header-tags {
    flex-basis: 100%;
    justify-content: flex-start;
  }

  .task-title {
    font-size: 15px;
  }

  .task-description {
    font-size: 13px;
  }

  .task-images {
    gap: 8px;
    padding: 8px;
    margin: 12px 0;
  }

  .grid-1 .task-image {
    max-height: 250px;
  }

  .grid-2 {
    grid-template-columns: repeat(2, 1fr);
  }

  .grid-2 .task-image {
    max-height: 150px;
  }

  .grid-3,
  .grid-4,
  .grid-5,
  .grid-6,
  .grid-7,
  .grid-8,
  .grid-9 {
    grid-template-columns: repeat(2, 1fr);
  }

  .grid-3 .task-image,
  .grid-4 .task-image,
  .grid-5 .task-image,
  .grid-6 .task-image,
  .grid-7 .task-image,
  .grid-8 .task-image,
  .grid-9 .task-image {
    max-height: 120px;
  }

  .task-details-row {
    gap: 16px;
    margin-bottom: 8px;
    font-size: 12px;
  }

  .action-bar {
    flex-direction: column;
    gap: 8px;
  }

  .action-left {
    width: 100%;
  }

  .action-right {
    width: 100%;
  }
}

/* 小屏手机 */
@media (max-width: 480px) {
  .task-card {
    padding: 10px;
    margin-bottom: 8px;
    border-radius: 8px;
  }

  .task-header-section {
    flex-direction: column;
    gap: 6px;
  }

  .user-info {
    flex-basis: 100%;
    gap: 8px;
  }

  .user-name {
    font-size: 13px;
  }

  .publish-time {
    font-size: 11px;
  }

  .header-tags {
    flex-basis: 100%;
    justify-content: flex-start;
  }

  .task-title {
    font-size: 14px;
    margin-bottom: 6px;
  }

  .task-description {
    font-size: 12px;
    margin-bottom: 8px;
  }

  .task-images {
    gap: 6px;
    padding: 6px;
    margin: 10px 0;
  }

  .grid-1 .task-image {
    max-height: 200px;
  }

  .grid-2,
  .grid-3,
  .grid-4,
  .grid-5,
  .grid-6,
  .grid-7,
  .grid-8,
  .grid-9 {
    grid-template-columns: repeat(2, 1fr);
  }

  .grid-2 .task-image,
  .grid-3 .task-image,
  .grid-4 .task-image,
  .grid-5 .task-image,
  .grid-6 .task-image,
  .grid-7 .task-image,
  .grid-8 .task-image,
  .grid-9 .task-image {
    max-height: 100px;
  }

  .task-details-row {
    gap: 12px;
    margin-bottom: 6px;
    font-size: 11px;
  }

  .detail-item {
    gap: 4px;
  }

  .taker-info {
    padding: 6px;
    margin-bottom: 8px;
    font-size: 11px;
  }

  .action-bar {
    padding-top: 8px;
    gap: 6px;
  }
}
</style>
