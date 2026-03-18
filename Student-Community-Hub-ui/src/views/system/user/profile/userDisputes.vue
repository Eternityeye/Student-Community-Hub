<template>
  <div class="user-disputes">
    <!-- 筛选区域 -->
    <el-row :gutter="20" class="filter-row">
      <el-col :span="7">
        <el-select
          v-model="queryParams.status"
          placeholder="全部状态"
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
      <el-col :span="7">
        <el-select
          v-model="queryParams.roleType"
          placeholder="全部角色"
          clearable
          @change="handleSearch"
          style="width: 100%;"
        >
          <el-option label="全部角色" value=""></el-option>
          <el-option label="申请人" value="plaintiff"></el-option>
          <el-option label="被申请人" value="defendant"></el-option>
        </el-select>
      </el-col>
      <el-col :span="10">
        <el-button-group>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-button-group>
      </el-col>
    </el-row>

    <!-- 纠纷列表 -->
    <el-skeleton :loading="loading" :count="5" :rows="2">
      <div v-if="!loading && list.length > 0">
        <el-row
          v-for="item in list"
          :key="item.caseId"
          class="dispute-item"
          @click="goToDetail(item.caseId)"
        >
          <el-col :span="20">
            <div class="dispute-header">
              <h4>{{ item.taskTitle }}</h4>
              <el-tag :type="getStatusType(item.status)">{{ getStatusText(item.status) }}</el-tag>
            </div>
            <div class="dispute-info">
              <span>纠纷原因：{{ getReasonText(item.reason) }}</span>
              <span style="margin-left: 20px;">角色：{{ item.roleType === 'plaintiff' ? '申请人' : '被申请人' }}</span>
              <span style="margin-left: 20px;">{{ formatDate(item.createTime) }}</span>
            </div>
          </el-col>
          <el-col :span="4" class="text-right">
            <el-button type="text" size="small">查看详情 ></el-button>
          </el-col>
        </el-row>

        <!-- 分页 -->
        <el-pagination
          :current-page="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :page-sizes="[5, 10, 15]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handlePageSizeChange"
          @current-change="handlePageChange"
          style="text-align: right; margin-top: 20px;"
        ></el-pagination>
      </div>

      <el-empty v-else description="暂无纠纷记录"></el-empty>
    </el-skeleton>
  </div>
</template>

<script>
import { getDisputeList } from '@/api/dispute'
import { formatDate as formatDateUtil } from '@/utils/datetime'

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
      return formatDateUtil(date, 'YYYY-MM-DD HH:mm:ss')
    }
  }
}
</script>

<style scoped>
.user-disputes {
  padding: 10px 0;
}

.filter-row {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
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
  flex-wrap: wrap;
  font-size: 13px;
  color: #666;
}

.text-right {
  text-align: right;
}

@media (max-width: 768px) {
  .user-disputes {
    padding: 5px 0;
  }

  .dispute-item {
    flex-direction: column;
    align-items: flex-start;
    padding: 12px;
  }

  .dispute-header {
    width: 100%;
  }

  .text-right {
    width: 100%;
    text-align: left;
    margin-top: 10px;
  }
}
</style>
