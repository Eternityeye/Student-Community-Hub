<template>
  <div class="dispute-list-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span class="title">我的纠纷</span>
      </div>

      <!-- 筛选区域 -->
      <el-row :gutter="20" class="filter-row" style="margin-bottom: 20px;">
        <el-col :span="24" :sm="12" :xs="24">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择纠纷状态"
            clearable
            @change="handleSearch"
            style="width: 100%;"
          >
            <el-option label="全部状态" value=""></el-option>
            <el-option label="待审理" value="0"></el-option>
            <el-option label="申请人责任" value="1"></el-option>
            <el-option label="被申请人责任" value="2"></el-option>
            <el-option label="驳回" value="3"></el-option>
          </el-select>
        </el-col>
        <el-col :span="24" :sm="12" :xs="24">
          <el-select
            v-model="queryParams.roleType"
            placeholder="请选择角色"
            clearable
            @change="handleSearch"
            style="width: 100%;"
          >
            <el-option label="全部角色" value=""></el-option>
            <el-option label="作为申请人" value="plaintiff"></el-option>
            <el-option label="作为被申请人" value="defendant"></el-option>
          </el-select>
        </el-col>
        <el-col :span="24">
          <el-button-group>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-button-group>
        </el-col>
      </el-row>

      <!-- 纠纷列表 -->
      <el-skeleton :loading="loading" :count="3">
        <div v-if="!loading && list.length > 0">
          <el-row
            v-for="item in list"
            :key="item.caseId"
            class="dispute-item"
            @click="goToDetail(item.caseId)"
          >
            <el-col :span="4" class="dispute-avatar">
              <img :src="item.oppositeAvatar" class="avatar">
              <div class="user-name">{{ item.oppositeName }}</div>
            </el-col>
            <el-col :span="16" class="dispute-content">
              <div class="dispute-header">
                <h4>{{ item.taskTitle }}</h4>
                <el-tag :type="getStatusType(item.status)">
                  {{ getStatusText(item.status) }}
                </el-tag>
              </div>
              <div class="dispute-info">
                <span class="label">纠纷原因：</span>
                <span>{{ getReasonText(item.reason) }}</span>
              </div>
              <div class="dispute-info">
                <span class="label">角色：</span>
                <span>{{ item.roleType === 'plaintiff' ? '申请人' : '被申请人' }}</span>
              </div>
              <div class="dispute-info">
                <span class="label">创建时间：</span>
                <span>{{ formatDate(item.createTime) }}</span>
              </div>
            </el-col>
            <el-col :span="4" class="dispute-action">
              <el-button type="text" size="small" @click.stop="goToDetail(item.caseId)">
                查看详情 >
              </el-button>
            </el-col>
          </el-row>

          <!-- 分页 -->
          <el-pagination
            :current-page="queryParams.pageNum"
            :page-size="queryParams.pageSize"
            :page-sizes="[10, 20, 30]"
            :total="total"
            layout="total, sizes, prev, pager, next"
            @size-change="handlePageSizeChange"
            @current-change="handlePageChange"
            style="text-align: right; margin-top: 20px;"
          ></el-pagination>
        </div>

        <el-empty v-else description="暂无纠纷记录"></el-empty>
      </el-skeleton>
    </el-card>
  </div>
</template>

<script>
import { getDisputeList } from '@/api/dispute'
import { formatDate } from '@/utils/datetime'

export default {
  name: 'DisputeList',
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        status: '',
        roleType: ''
      },
      list: [],
      total: 0,
      loading: false,
      currentUserId: null
    }
  },
  created() {
    this.currentUserId = this.$store.state.user.id
    this.fetchList()
  },
  methods: {
    fetchList() {
      this.loading = true
      const params = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize,
        status: this.queryParams.status,
        roleType: this.queryParams.roleType
      }
      getDisputeList(params).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleSearch() {
      this.queryParams.pageNum = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        status: '',
        roleType: ''
      }
      this.fetchList()
    },
    handlePageChange(pageNum) {
      this.queryParams.pageNum = pageNum
      this.fetchList()
    },
    handlePageSizeChange(pageSize) {
      this.queryParams.pageSize = pageSize
      this.queryParams.pageNum = 1
      this.fetchList()
    },
    goToDetail(caseId) {
      this.$router.push(`/dispute/${caseId}`)
    },
    getStatusType(status) {
      const types = {
        '0': 'info',
        '1': 'danger',
        '2': 'danger',
        '3': 'warning'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        '0': '待审理',
        '1': '申请人责任',
        '2': '被申请人责任',
        '3': '驳回'
      }
      return texts[status] || '未知'
    },
    getReasonText(reason) {
      const reasons = {
        'quality_issue': '服务质量不达预期',
        'false_description': '虚假宣传/描述不符',
        'timeout': '未按时完成',
        'unfair_price': '收费不合理',
        'bad_attitude': '态度恶劣/骚扰',
        'other': '其他'
      }
      return reasons[reason] || reason
    },
    formatDate(date) {
      return formatDate(date, 'YYYY-MM-DD HH:mm:ss')
    }
  }
}
</script>

<style scoped>
.dispute-list-container {
  padding: 20px;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.filter-row {
  margin-bottom: 20px;
}

.dispute-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.dispute-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.dispute-avatar {
  text-align: center;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.user-name {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dispute-content {
  padding: 0 15px;
}

.dispute-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.dispute-header h4 {
  margin: 0;
  color: #333;
  font-weight: 500;
  flex: 1;
}

.dispute-info {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
  color: #666;
}

.dispute-info .label {
  min-width: 80px;
  color: #999;
}

.dispute-action {
  text-align: right;
}

@media (max-width: 768px) {
  .dispute-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .dispute-avatar {
    width: 100%;
    margin-bottom: 10px;
  }

  .dispute-content {
    width: 100%;
    padding: 0;
  }

  .dispute-action {
    width: 100%;
    margin-top: 10px;
  }

  .filter-row {
    flex-direction: column;
  }
}
</style>
