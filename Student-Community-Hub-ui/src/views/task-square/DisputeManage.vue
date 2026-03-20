<template>
  <div class="app-container">
    <el-card>
      <div slot="header"><span>纠纷管理</span></div>

      <el-form :inline="true" :model="queryParams" style="margin-bottom:16px;">
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable @change="handleSearch">
            <el-option label="待审理" value="0" />
            <el-option label="申请人责任" value="1" />
            <el-option label="被申请人责任" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column label="ID" prop="caseId" width="70" />
        <el-table-column label="任务" prop="taskTitle" min-width="120" show-overflow-tooltip />
        <el-table-column label="申请人" prop="plaintiffName" width="100" />
        <el-table-column label="被申请人" prop="defendantName" width="100" />
        <el-table-column label="纠纷原因" width="140">
          <template slot-scope="{ row }">{{ getReasonText(row.reason) }}</template>
        </el-table-column>
        <el-table-column label="证据" width="110">
          <template slot-scope="{ row }">
            <span v-if="row.plaintiffDescription && row.defendantDescription" style="color:#67c23a;">双方已提交</span>
            <span v-else-if="row.plaintiffDescription || row.defendantDescription" style="color:#e6a23c;">一方已提交</span>
            <span v-else style="color:#909399;">暂无证据</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template slot-scope="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="handleViewDetail(row)">详情</el-button>
            <el-button v-if="row.status === '0'" type="text" size="small" style="color:#e6a23c;" @click="handleOpenJudge(row)">裁定</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top:16px;text-align:right;"
        @current-change="handlePageChange"
      />
    </el-card>

    <!-- 详情弹窗（大布局） -->
    <el-dialog title="纠纷详情" :visible.sync="detailVisible" width="860px" append-to-body>
      <div v-if="currentCase" class="dispute-detail">
        <!-- 基本信息 -->
        <el-row :gutter="20" class="info-section">
          <el-col :span="12">
            <div class="info-item"><span class="label">关联任务：</span>
              <span>{{ currentCase.taskTitle }}</span>
              <el-button type="text" size="mini" style="margin-left:8px;" @click="handleViewTask(currentCase.taskId)">查看任务</el-button>
            </div>
            <div class="info-item"><span class="label">纠纷原因：</span><span>{{ getReasonText(currentCase.reason) }}</span></div>
            <div class="info-item"><span class="label">创建时间：</span><span>{{ currentCase.createTime }}</span></div>
          </el-col>
          <el-col :span="12">
            <div class="info-item"><span class="label">纠纷状态：</span>
              <el-tag :type="getStatusType(currentCase.status)">{{ getStatusText(currentCase.status) }}</el-tag>
            </div>
            <div class="info-item" v-if="currentCase.handlerName"><span class="label">处理人：</span><span>{{ currentCase.handlerName }}</span></div>
            <div class="info-item" v-if="currentCase.handleTime"><span class="label">处理时间：</span><span>{{ currentCase.handleTime }}</span></div>
          </el-col>
        </el-row>

        <el-divider />

        <!-- 申请人 -->
        <div class="party-section">
          <div class="party-title">申请人（发起方）</div>
          <el-row :gutter="20">
            <el-col :span="4" class="avatar-col">
              <img :src="getAvatarUrl(currentCase.plaintiffAvatar)" class="user-avatar">
              <div class="user-name">{{ currentCase.plaintiffName }}</div>
            </el-col>
            <el-col :span="20">
              <div class="desc-block" v-if="currentCase.plaintiffDescription">
                <div class="block-label">陈述：</div>
                <p>{{ currentCase.plaintiffDescription }}</p>
              </div>
              <div class="evidence-block" v-if="currentCase.plaintiffEvidenceImages">
                <div class="block-label">证据：</div>
                <div class="images-grid">
                  <img
                    v-for="(img, i) in splitImages(currentCase.plaintiffEvidenceImages)"
                    :key="i"
                    :src="getImgUrl(img)"
                    class="evidence-img"
                    @click="previewImg(splitImages(currentCase.plaintiffEvidenceImages), i)"
                  />
                </div>
              </div>
              <p v-else class="no-data">未上传证据</p>
            </el-col>
          </el-row>
        </div>

        <el-divider />

        <!-- 被申请人 -->
        <div class="party-section">
          <div class="party-title">被申请人（被告方）</div>
          <el-row :gutter="20">
            <el-col :span="4" class="avatar-col">
              <img :src="getAvatarUrl(currentCase.defendantAvatar)" class="user-avatar">
              <div class="user-name">{{ currentCase.defendantName }}</div>
            </el-col>
            <el-col :span="20">
              <div class="desc-block" v-if="currentCase.defendantDescription">
                <div class="block-label">答辩：</div>
                <p>{{ currentCase.defendantDescription }}</p>
              </div>
              <div class="evidence-block" v-if="currentCase.defendantEvidenceImages">
                <div class="block-label">证据：</div>
                <div class="images-grid">
                  <img
                    v-for="(img, i) in splitImages(currentCase.defendantEvidenceImages)"
                    :key="i"
                    :src="getImgUrl(img)"
                    class="evidence-img"
                    @click="previewImg(splitImages(currentCase.defendantEvidenceImages), i)"
                  />
                </div>
              </div>
              <p v-else class="no-data">暂未提交答辩</p>
            </el-col>
          </el-row>
        </div>

        <!-- 裁定结果 -->
        <template v-if="currentCase.status !== '0'">
          <el-divider />
          <div class="judgment-section">
            <div class="party-title">裁定结果</div>
            <el-alert
              :title="getStatusText(currentCase.status)"
              :description="currentCase.handleReason"
              :type="currentCase.status === '1' || currentCase.status === '2' ? 'error' : 'info'"
              :closable="false"
              show-icon
            />
          </div>
        </template>
      </div>

      <span slot="footer">
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="currentCase && currentCase.status === '0'" type="primary" @click="handleOpenJudge(currentCase); detailVisible = false">去裁定</el-button>
      </span>
    </el-dialog>

    <!-- 裁定弹窗 -->
    <el-dialog title="裁定纠纷" :visible.sync="judgeVisible" width="500px" append-to-body>
      <el-form ref="judgeForm" :model="judgeForm" :rules="judgeRules" label-width="100px">
        <el-form-item label="裁定结果" prop="status">
          <el-radio-group v-model="judgeForm.status">
            <el-radio label="1">申请人责任</el-radio>
            <el-radio label="2">被申请人责任</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="裁定理由" prop="handleReason">
          <el-input v-model="judgeForm.handleReason" type="textarea" :rows="4" placeholder="请填写裁定理由..." />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="judgeVisible = false">取消</el-button>
        <el-button type="primary" :loading="judgeLoading" @click="submitJudge">确认裁定</el-button>
      </span>
    </el-dialog>

    <!-- 任务详情弹窗 -->
    <task-detail-dialog v-if="viewTaskId" v-model="taskDetailVisible" :task-id="viewTaskId" />

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="previewVisible"
      :on-close="() => { previewVisible = false }"
      :url-list="previewList"
      :initial-index="previewIndex"
    />
  </div>
</template>

<script>
import { getAdminDisputeList, handleDispute } from '@/api/dispute'
import TaskDetailDialog from '@/views/task-square/components/TaskDetailDialog.vue'

export default {
  name: 'DisputeManage',
  components: { TaskDetailDialog },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, status: '' },
      detailVisible: false,
      currentCase: null,
      judgeVisible: false,
      judgeLoading: false,
      judgeForm: { caseId: null, status: null, handleReason: '' },
      judgeRules: {
        status: [{ required: true, message: '请选择裁定结果', trigger: 'change' }],
        handleReason: [{ required: true, message: '请填写裁定理由', trigger: 'blur' }]
      },
      taskDetailVisible: false,
      viewTaskId: null,
      previewVisible: false,
      previewList: [],
      previewIndex: 0
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    fetchList() {
      this.loading = true
      getAdminDisputeList({ pageNum: this.queryParams.pageNum, pageSize: this.queryParams.pageSize, status: this.queryParams.status })
        .then(res => { this.list = res.rows || []; this.total = res.total || 0 })
        .finally(() => { this.loading = false })
    },
    handleSearch() { this.queryParams.pageNum = 1; this.fetchList() },
    handleReset() { this.queryParams = { pageNum: 1, pageSize: 10, status: '' }; this.fetchList() },
    handlePageChange(page) { this.queryParams.pageNum = page; this.fetchList() },
    handleViewDetail(row) { this.currentCase = row; this.detailVisible = true },
    handleViewTask(taskId) { this.viewTaskId = taskId; this.taskDetailVisible = true },
    handleOpenJudge(row) { this.judgeForm = { caseId: row.caseId, status: null, handleReason: '' }; this.judgeVisible = true },
    submitJudge() {
      this.$refs.judgeForm.validate(valid => {
        if (!valid) return
        this.judgeLoading = true
        handleDispute(this.judgeForm.caseId, { status: this.judgeForm.status, handleReason: this.judgeForm.handleReason })
          .then(() => { this.$message.success('裁定成功'); this.judgeVisible = false; this.fetchList() })
          .catch(() => { this.$message.error('裁定失败') })
          .finally(() => { this.judgeLoading = false })
      })
    },
    splitImages(str) { return str ? str.split(',').filter(s => s && s !== 'undefined') : [] },
    getImgUrl(url) {
      if (!url) return ''
      if (url.startsWith('http')) return url
      return process.env.VUE_APP_BASE_API + url
    },
    getAvatarUrl(avatar) {
      if (!avatar) return require('@/assets/images/profile.jpg')
      if (avatar.startsWith('http')) return avatar
      return process.env.VUE_APP_BASE_API + avatar
    },
    previewImg(list, index) {
      this.previewList = list.map(img => this.getImgUrl(img))
      this.previewIndex = index
      this.previewVisible = true
    },
    getStatusType(status) { return { '0': 'warning', '1': 'danger', '2': 'danger' }[status] || 'info' },
    getStatusText(status) { return { '0': '待审理', '1': '申请人责任', '2': '被申请人责任' }[status] || '未知' },
    getReasonText(reason) {
      return { quality_issue: '服务质量不达预期', false_description: '虚假宣传/描述不符', timeout: '未按时完成', unfair_price: '收费不合理', bad_attitude: '态度恶劣/骚扰', other: '其他' }[reason] || reason
    }
  }
}
</script>

<style scoped>
.dispute-detail { padding: 4px 0; }
.info-section { margin-bottom: 4px; }
.info-item { margin-bottom: 10px; font-size: 14px; }
.label { color: #909399; margin-right: 4px; }
.party-section { margin: 4px 0; }
.party-title { font-size: 15px; font-weight: 600; color: #303133; margin-bottom: 12px; }
.avatar-col { text-align: center; }
.user-avatar { width: 60px; height: 60px; border-radius: 50%; object-fit: cover; border: 2px solid #ebeef5; }
.user-name { font-size: 13px; color: #606266; margin-top: 6px; }
.desc-block, .evidence-block { margin-bottom: 12px; }
.block-label { font-size: 13px; color: #606266; margin-bottom: 6px; }
.desc-block p { margin: 0; font-size: 14px; color: #303133; line-height: 1.6; }
.images-grid { display: flex; flex-wrap: wrap; gap: 8px; }
.evidence-img { width: 110px; height: 82px; object-fit: cover; border-radius: 4px; cursor: pointer; border: 1px solid #ebeef5; transition: opacity 0.2s; }
.evidence-img:hover { opacity: 0.8; }
.no-data { color: #c0c4cc; font-size: 13px; margin: 0; }
.judgment-section { margin-top: 4px; }
</style>
