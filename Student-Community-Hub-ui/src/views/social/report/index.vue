<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item label="审核状态">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option label="全部" value="" />
          <el-option label="待处理" value="0" />
          <el-option label="已处理-违规" value="1" />
          <el-option label="已处理-驳回" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="reportList"
      stripe
      style="width: 100%"
    >
      <el-table-column label="举报ID" width="80" prop="reportId" />
      <el-table-column label="举报时间" width="180" prop="createTime">
        <template slot-scope="scope">
          {{ formatTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="举报人" width="100" prop="reportUserName" />
      <el-table-column label="被举报人" width="100" prop="postUserName" />
      <el-table-column label="举报原因" prop="reasonsStr" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          {{ scope.row.reasonsStr || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="审核状态" width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="warning">待处理</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="danger">已处理-违规</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="success">已处理-驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理人" width="100" prop="handleBy" />
      <el-table-column label="处理时间" width="180" prop="handleTime">
        <template slot-scope="scope">
          {{ formatTime(scope.row.handleTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="small"
            @click="handleShowDetail(scope.row)"
          >{{ scope.row.status === '0' ? '审核' : '查看' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 审核弹窗 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form ref="detailForm" label-width="100px">
        <!-- 举报详情 -->
        <el-form-item label="举报人">
          <el-input v-model="currentReport.reportUserName" readonly />
        </el-form-item>
        <el-form-item label="被举报人">
          <el-input v-model="currentReport.postUserName" readonly />
        </el-form-item>
        <el-form-item label="帖子内容">
          <el-input
            :value="stripHtmlTags(currentReport.postContent)"
            type="textarea"
            rows="4"
            readonly
          />
        </el-form-item>
        <el-form-item label="举报原因">
          <el-input v-model="currentReport.reasonsStr" readonly />
        </el-form-item>
        <el-form-item label="详细描述">
          <el-input
            v-model="currentReport.description"
            type="textarea"
            rows="3"
            readonly
          />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-tag v-if="currentReport.status === '0'" type="warning">待处理</el-tag>
          <el-tag v-else-if="currentReport.status === '1'" type="danger">已处理-违规</el-tag>
          <el-tag v-else-if="currentReport.status === '2'" type="success">已处理-驳回</el-tag>
        </el-form-item>

        <!-- 审核意见（仅待处理显示） -->
        <el-form-item v-if="currentReport.status === '0'" label="审核意见">
          <el-input
            v-model="handleResult"
            type="textarea"
            rows="3"
            placeholder="请输入审核意见"
          />
        </el-form-item>

        <!-- 历史处理记录（已处理则显示） -->
        <el-form-item v-else label="处理人">
          <el-input v-model="currentReport.handleBy" readonly />
        </el-form-item>
        <el-form-item v-if="currentReport.status !== '0'" label="处理时间">
          <el-input :value="formatTime(currentReport.handleTime)" readonly />
        </el-form-item>
        <el-form-item v-if="currentReport.status !== '0'" label="处理意见">
          <el-input
            v-model="currentReport.handleResult"
            type="textarea"
            rows="3"
            readonly
          />
        </el-form-item>
      </el-form>

      <span v-if="currentReport.status === '0'" slot="footer" class="dialog-footer">
        <el-button type="danger" @click="handleConfirmViolation">
          <i class="el-icon-delete" /> 违规删帖
        </el-button>
        <el-button type="success" @click="handleConfirmDismiss">
          <i class="el-icon-circle-check" /> 驳回举报
        </el-button>
        <el-button @click="dialogVisible = false">取消</el-button>
      </span>
      <span v-else slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listReports, getReport, handleViolation, handleDismiss } from '@/api/social/report'
import pagination from '@/components/Pagination'

export default {
  name: 'SocialReportManagement',
  components: { pagination },
  data() {
    return {
      loading: false,
      total: 0,
      reportList: [],
      dialogVisible: false,
      dialogTitle: '举报详情',
      currentReport: {},
      handleResult: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        status: ''
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listReports(this.queryParams).then(response => {
        this.reportList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.status = ''
      this.handleQuery()
    },
    handleShowDetail(row) {
      getReport(row.reportId).then(response => {
        this.currentReport = response.data
        this.handleResult = ''
        this.dialogVisible = true
        this.dialogTitle = '举报详情'
      })
    },
    handleConfirmViolation() {
      if (!this.handleResult || this.handleResult.trim() === '') {
        this.$message.error('请输入审核意见')
        return
      }

      this.$confirm('确定要删除该帖子吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        handleViolation(this.currentReport.reportId, { handleResult: this.handleResult }).then(() => {
          this.$message.success('审核成功，帖子已删除，用户已通知')
          this.dialogVisible = false
          this.getList()
        }).catch(() => {
          this.$message.error('审核失败')
        })
      }).catch(() => {
        // 用户取消
      })
    },
    handleConfirmDismiss() {
      if (!this.handleResult || this.handleResult.trim() === '') {
        this.$message.error('请输入审核意见')
        return
      }

      this.$confirm('确定要驳回此举报吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        handleDismiss(this.currentReport.reportId, { handleResult: this.handleResult }).then(() => {
          this.$message.success('审核成功，举报已驳回')
          this.dialogVisible = false
          this.getList()
        }).catch(() => {
          this.$message.error('审核失败')
        })
      }).catch(() => {
        // 用户取消
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    stripHtmlTags(html) {
      if (!html) return ''
      // 移除所有 HTML 标签
      return html.replace(/<[^>]*>/g, '').trim()
    }
  }
}
</script>

<style scoped lang="scss">
.app-container {
  .el-table {
    margin-bottom: 20px;
  }
}
</style>
