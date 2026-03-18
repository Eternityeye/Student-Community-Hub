<template>
  <div class="task-square">
    <div class="container">
      <div class="main-content">
        <!-- 任务列表区域 -->
        <div class="task-list-container">
          <div class="task-header">
            <h2>校园互助任务广场</h2>
            <el-button type="primary" icon="el-icon-plus" @click="showCreateDialog = true">发布任务</el-button>
          </div>

          <!-- 任务卡片列表 -->
          <div class="task-items">
            <task-card
              v-for="task in taskList"
              :key="task.taskId"
              :task="task"
              @task-deleted="handleTaskDeleted"
              @task-taken="handleTaskTaken"
            />
          </div>

          <!-- 加载更多 -->
          <div v-if="hasMore && !loadingMore" class="load-more-btn">
            <el-button @click="loadMoreTasks">加载更多</el-button>
          </div>
          <div v-if="loadingMore" class="loading">加载中...</div>
          <el-empty v-if="!loading && taskList.length === 0" description="暂无任务" />
        </div>

        <!-- 侧边栏 -->
        <div class="sidebar">
          <div class="filter-card">
            <h3>筛选</h3>
            <div class="filter-item">
              <label>任务分类</label>
              <el-select v-model="queryParams.category" clearable placeholder="所有分类" @change="getTaskList">
                <el-option label="取快递" value="0" />
                <el-option label="占座" value="1" />
                <el-option label="代购" value="2" />
                <el-option label="答疑" value="3" />
                <el-option label="其他" value="4" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>任务状态</label>
              <el-select v-model="queryParams.status" clearable placeholder="所有状态" @change="getTaskList">
                <el-option label="待接单" value="0" />
                <el-option label="进行中" value="1" />
                <el-option label="已完成待确认" value="2" />
                <el-option label="已关闭" value="3" />
              </el-select>
            </div>
          </div>

          <div class="hot-tags-card">
            <h3>热门分类</h3>
            <div class="hot-tags">
              <el-tag
                v-for="tag in categoryTags"
                :key="tag.value"
                :type="tag.type"
                style="cursor: pointer; margin: 5px;"
                @click="filterByCategory(tag.value)"
              >
                {{ tag.label }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 发布任务弹窗 -->
    <create-task-dialog v-model="showCreateDialog" @post-created="handleTaskCreated" />
  </div>
</template>

<script>
import { listTasks } from '@/api/task/task'
import TaskCard from './components/TaskCard.vue'
import CreateTaskDialog from './components/CreateTaskDialog.vue'

export default {
  name: 'TaskSquare',
  components: {
    TaskCard,
    CreateTaskDialog
  },
  data() {
    return {
      taskList: [],
      loading: false,
      loadingMore: false,
      showCreateDialog: false,
      hasMore: false,
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        category: null,
        status: null
      },
      categoryTags: [
        { label: '取快递', value: '0', type: 'info' },
        { label: '占座', value: '1', type: 'warning' },
        { label: '代购', value: '2', type: 'success' },
        { label: '答疑', value: '3', type: 'primary' },
        { label: '其他', value: '4', type: '' }
      ]
    }
  },
  computed: {
    categoryName() {
      const map = { '0': '取快递', '1': '占座', '2': '代购', '3': '答疑', '4': '其他' }
      return map
    }
  },
  mounted() {
    this.getTaskList()
    // 监听滚动加载更多
    window.addEventListener('scroll', this.handleScroll)
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll)
  },
  methods: {
    getTaskList() {
      this.loading = true
      this.queryParams.pageNum = 1
      listTasks(this.queryParams).then(response => {
        this.taskList = response.rows
        this.total = response.total
        this.hasMore = response.rows.length === this.queryParams.pageSize
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    loadMoreTasks() {
      if (this.loadingMore) return
      this.loadingMore = true
      this.queryParams.pageNum++
      listTasks(this.queryParams).then(response => {
        this.taskList = this.taskList.concat(response.rows)
        this.hasMore = response.rows.length === this.queryParams.pageSize
        this.loadingMore = false
      }).catch(() => {
        this.loadingMore = false
      })
    },
    handleTaskCreated() {
      this.getTaskList()
    },
    handleTaskDeleted(taskId) {
      this.taskList = this.taskList.filter(t => t.taskId !== taskId)
    },
    handleTaskTaken() {
      this.getTaskList()
    },
    filterByCategory(categoryValue) {
      this.queryParams.category = categoryValue
      this.getTaskList()
    },
    handleScroll() {
      // 距底部100px时触发加载更多
      if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 100) {
        if (this.hasMore && !this.loadingMore && !this.loading) {
          this.loadMoreTasks()
        }
      }
    }
  }
}
</script>

<style scoped>
.task-square {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding: 20px 0;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 20px;
}

.task-list-container {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.task-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.task-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.load-more-btn {
  text-align: center;
  padding: 20px 0;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #999;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-card,
.hot-tags-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.filter-card h3,
.hot-tags-card h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.filter-item {
  margin-bottom: 16px;
}

.filter-item label {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.filter-item .el-select {
  width: 100%;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
}

@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr 280px;
  }
}

@media (max-width: 768px) {
  .main-content {
    grid-template-columns: 1fr;
  }

  .sidebar {
    display: none;
  }

  .task-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
