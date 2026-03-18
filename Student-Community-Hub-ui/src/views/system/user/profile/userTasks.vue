<template>
  <div class="user-tasks">
    <el-tabs v-model="activeTab">
      <!-- 我发布的任务 -->
      <el-tab-pane label="我发布的任务" name="published">
        <div class="task-list">
          <div v-if="loading" class="loading">加载中...</div>
          <div v-else-if="publishedTasks.length === 0" class="empty">
            <el-empty description="还没有发布过任务" />
          </div>
          <div v-else>
            <el-row :gutter="20">
              <el-col v-for="task in publishedTasks" :key="task.taskId" :xs="24" :sm="24" :md="12" :lg="12">
                <div class="task-card">
                  <div class="card-header">
                    <h4>{{ task.title }}</h4>
                    <el-tag :type="getStatusType(task.status)" size="small">
                      {{ getStatusName(task.status) }}
                    </el-tag>
                  </div>
                  <div class="card-body">
                    <div class="info-row">
                      <span class="label">分类：</span>
                      <el-tag :type="getCategoryType(task.category)" size="small">
                        {{ getCategoryName(task.category) }}
                      </el-tag>
                    </div>
                    <div class="info-row">
                      <span class="label">悬赏：</span>
                      <span class="reward">{{ task.reward }}</span>
                    </div>
                    <div class="info-row">
                      <span class="label">地点：</span>
                      <span>{{ task.location }}</span>
                    </div>
                    <div class="info-row">
                      <span class="label">接单者：</span>
                      <span>{{ task.takerName || '待接单' }}</span>
                    </div>
                    <div class="info-row">
                      <span class="label">截止时间：</span>
                      <span>{{ formatDateTime(task.deadline) }}</span>
                    </div>
                  </div>
                  <div class="card-footer">
                    <el-button size="small" @click="viewTaskDetail(task.taskId)">详情</el-button>
                    <el-button
                      v-if="task.status === '0'"
                      size="small"
                      type="danger"
                      @click="deleteTask(task.taskId)"
                    >
                      删除
                    </el-button>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-tab-pane>

      <!-- 我接的任务 -->
      <el-tab-pane label="我接的任务" name="taken">
        <div class="task-list">
          <div v-if="loading" class="loading">加载中...</div>
          <div v-else-if="takenTasks.length === 0" class="empty">
            <el-empty description="还没有接过任务" />
          </div>
          <div v-else>
            <el-row :gutter="20">
              <el-col v-for="task in takenTasks" :key="task.taskId" :xs="24" :sm="24" :md="12" :lg="12">
                <div class="task-card">
                  <div class="card-header">
                    <h4>{{ task.title }}</h4>
                    <el-tag :type="getStatusType(task.status)" size="small">
                      {{ getStatusName(task.status) }}
                    </el-tag>
                  </div>
                  <div class="card-body">
                    <div class="info-row">
                      <span class="label">分类：</span>
                      <el-tag :type="getCategoryType(task.category)" size="small">
                        {{ getCategoryName(task.category) }}
                      </el-tag>
                    </div>
                    <div class="info-row">
                      <span class="label">悬赏：</span>
                      <span class="reward">{{ task.reward }}</span>
                    </div>
                    <div class="info-row">
                      <span class="label">发布者：</span>
                      <span>{{ task.userName }}</span>
                    </div>
                    <div class="info-row">
                      <span class="label">地点：</span>
                      <span>{{ task.location }}</span>
                    </div>
                    <div class="info-row">
                      <span class="label">截止时间：</span>
                      <span>{{ formatDateTime(task.deadline) }}</span>
                    </div>
                  </div>
                  <div class="card-footer">
                    <el-button size="small" @click="viewTaskDetail(task.taskId)">详情</el-button>
                    <el-button
                      v-if="task.status === '1'"
                      size="small"
                      type="warning"
                      @click="completeTask(task.taskId)"
                    >
                      我已完成
                    </el-button>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-tab-pane>

      <!-- 我的评价 -->
      <el-tab-pane label="我的评价" name="reviews">
        <div class="review-list">
          <div v-if="loading" class="loading">加载中...</div>
          <div v-else-if="myReviews.length === 0" class="empty">
            <el-empty description="还没有收到过评价" />
          </div>
          <div v-else>
            <div v-for="review in myReviews" :key="review.reviewId" class="review-item">
              <div class="review-header">
                <el-avatar :src="formatImageUrl(review.reviewerAvatar)" :size="36" />
                <div class="review-info">
                  <div class="reviewer-name">{{ review.reviewerName }}</div>
                  <el-rate v-model="review.rating" disabled :max="5" class="review-rating" />
                </div>
                <div class="review-time">{{ formatTime(review.createTime) }}</div>
              </div>
              <div class="review-content">{{ review.content }}</div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 任务详情弹窗 -->
    <task-detail-dialog v-if="selectedTaskId" v-model="showDetailDialog" :task-id="selectedTaskId" />
  </div>
</template>

<script>
import { listMyPublished, listMyTaken, listMyReviews, delTask, completeTask } from '@/api/task/task'
import TaskDetailDialog from '@/views/task-square/components/TaskDetailDialog.vue'
import { formatTime, formatDateTime } from '@/utils/datetime'

export default {
  name: 'UserTasks',
  components: {
    TaskDetailDialog
  },
  data() {
    return {
      activeTab: 'published',
      publishedTasks: [],
      takenTasks: [],
      myReviews: [],
      loading: false,
      showDetailDialog: false,
      selectedTaskId: null
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      Promise.all([
        this.loadPublishedTasks(),
        this.loadTakenTasks(),
        this.loadMyReviews()
      ]).finally(() => {
        this.loading = false
      })
    },
    loadPublishedTasks() {
      return listMyPublished({ pageNum: 1, pageSize: 100 }).then(response => {
        this.publishedTasks = response.rows || []
      }).catch(() => {
        this.publishedTasks = []
      })
    },
    loadTakenTasks() {
      return listMyTaken({ pageNum: 1, pageSize: 100 }).then(response => {
        this.takenTasks = response.rows || []
      }).catch(() => {
        this.takenTasks = []
      })
    },
    loadMyReviews() {
      return listMyReviews({ pageNum: 1, pageSize: 100 }).then(response => {
        this.myReviews = response.rows || []
      }).catch(() => {
        this.myReviews = []
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
    viewTaskDetail(taskId) {
      this.selectedTaskId = taskId
      this.showDetailDialog = true
    },
    deleteTask(taskId) {
      this.$confirm('确定删除这个任务吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'danger'
      }).then(() => {
        delTask(taskId).then(() => {
          this.$message.success('删除成功')
          this.loadPublishedTasks()
        })
      }).catch(() => {})
    },
    completeTask(taskId) {
      this.$confirm('确定任务已完成吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        completeTask(taskId).then(() => {
          this.$message.success('请等待发布者确认')
          this.loadTakenTasks()
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.user-tasks {
  background-color: #fff;
  border-radius: 4px;
}

.task-list,
.review-list {
  padding: 20px;
}

.loading,
.empty {
  padding: 40px;
  text-align: center;
  color: #999;
}

.task-card {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.task-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.card-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  margin-right: 12px;
}

.card-body {
  padding: 12px 16px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #666;
}

.info-row .label {
  color: #999;
  min-width: 60px;
}

.reward {
  color: #e6a23c;
  font-weight: 600;
}

.card-footer {
  padding: 8px 16px;
  background-color: #fafafa;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 8px;
}

.review-list {
  max-height: 600px;
  overflow-y: auto;
}

.review-item {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 12px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.review-info {
  flex: 1;
  min-width: 0;
}

.reviewer-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.review-rating {
  font-size: 12px;
}

.review-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.review-content {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  padding-left: 48px;
}
</style>
