<template>
  <div class="user-disputes">
    <!-- 筛选区域 -->
    <div class="filter-row">
      <el-select v-model="queryParams.status" placeholder="全部状态" clearable size="small" @change="handleSearch">
        <el-option label="全部状态" value="" />
        <el-option label="待审理" value="0" />
        <el-option label="申请人责任" value="1" />
        <el-option label="被申请人责任" value="2" />
      </el-select>
      <el-select v-model="queryParams.roleType" placeholder="全部角色" clearable size="small" style="margin-left:10px;" @change="handleSearch">
        <el-option label="全部角色" value="" />
        <el-option label="申请人" value="plaintiff" />
        <el-option label="被申请人" value="defendant" />
      </el-select>
      <el-button size="small" type="primary" style="margin-left:10px;" @click="handleSearch">搜索</el-button>
      <el-button size="small" style="margin-left:6px;" @click="handleReset">重置</el-button>
    </div>

    <!-- 纠纷列表 -->
    <div v-if="!loading && list.length > 0">
      <div
        v-for="item in list"
        :key="item.caseId"
        class="dispute-item"
        @click="goToDetail(item.caseId)"
      >
        <div class="dispute-main">
          <div class="dispute-title">
            <span class="task-name">{{ item.taskTitle }}</span>
            <el-tag :type="getStatusType(item.status)" size="small" style="margin-left:10px;">
              {{ getStatusText(item.status) }}
            </el-tag>
          </div>
          <div class="dispute-meta">
            <span>{{ getReasonText(item.reason) }}</span>
            <span class="sep">·</span>
            <span>{{ item.roleType === 'plaintiff' ? '我是申请人' : '我是被申请人' }}</span>
            <span class="sep">·</span>
            <span>{{ formatDate(item.createTime) }}</span>
          </div>
        </div>
        <i class="el-icon-arrow-right dispute-arrow" />
      </div>

      <el-pagination
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :page-sizes="[5, 10, 15]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        style="text-align:right; margin-top:16px;"
        @size-change="handlePageSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <div v-else-if="loading" v-loading="true" style="height:100px;" />
    <el-empty v-else description="暂无纠纷记录" />
  </div>
</template>

<script>
import { getDisputeList } from '@/api/dispute'
import { formatDateTime } from '@/utils/datetime'

export default {
  name: 'UserDisputes',
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        status: '',
        roleType: ''
      },
      list: [],
      total: 0,
      loading: false
    }
  },
  created() {
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
        pageSize: 5,
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
      return formatDateTime(date)
    }
  }
}
</script>

<style scoped>
.user-disputes {
  padding: 4px 0;
}
.filter-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.dispute-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: box-shadow 0.2s, border-color 0.2s;
}
.dispute-item:hover {
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
  border-color: #409eff;
}
.dispute-main {
  flex: 1;
  min-width: 0;
}
.dispute-title {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
}
.task-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.dispute-meta {
  font-size: 12px;
  color: #909399;
}
.sep {
  margin: 0 6px;
  color: #dcdfe6;
}
.dispute-arrow {
  color: #c0c4cc;
  font-size: 14px;
  margin-left: 12px;
  flex-shrink: 0;
}
</style>
