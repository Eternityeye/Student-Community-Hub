<template>
  <div class="dispute-detail-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span class="title">纠纷详情</span>
        <el-button
          style="float: right; padding: 3px 0"
          type="text"
          @click="$router.back()"
        >
          返回
        </el-button>
      </div>

      <el-skeleton :loading="loading" :count="3">
        <div v-if="!loading && caseDetail">
          <!-- 基本信息 -->
          <el-row :gutter="20" class="info-section">
            <el-col :span="12">
              <div class="info-item">
                <span class="label">纠纷ID：</span>
                <span>{{ caseDetail.caseId }}</span>
              </div>
              <div class="info-item">
                <span class="label">关联任务：</span>
                <span>{{ caseDetail.taskTitle }}</span>
              </div>
              <div class="info-item">
                <span class="label">纠纷状态：</span>
                <el-tag :type="getStatusType(caseDetail.status)">
                  {{ getStatusText(caseDetail.status) }}
                </el-tag>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="label">纠纷原因：</span>
                <span>{{ getReasonText(caseDetail.reason) }}</span>
              </div>
              <div class="info-item">
                <span class="label">创建时间：</span>
                <span>{{ formatDate(caseDetail.createTime) }}</span>
              </div>
              <div class="info-item" v-if="caseDetail.handleTime">
                <span class="label">处理时间：</span>
                <span>{{ formatDate(caseDetail.handleTime) }}</span>
              </div>
            </el-col>
          </el-row>

          <el-divider></el-divider>

          <!-- 申请人信息 -->
          <div class="party-section">
            <h3>申请人（发起方）</h3>
            <el-row :gutter="20">
              <el-col :span="6">
                <img :src="caseDetail.plaintiffAvatar" class="user-avatar">
                <div class="user-name">{{ caseDetail.plaintiffName }}</div>
              </el-col>
              <el-col :span="18">
                <div class="description-section">
                  <h4>申请人描述：</h4>
                  <p>{{ caseDetail.plaintiffDescription }}</p>
                </div>
                <div class="evidence-section" v-if="caseDetail.plaintiffEvidenceImages">
                  <h4>申请人证据：</h4>
                  <div class="images-grid">
                    <img
                      v-for="(img, idx) in caseDetail.plaintiffEvidenceImages.split(',')"
                      :key="`plaintiff-${idx}`"
                      :src="img"
                      class="evidence-img"
                      @click="previewImage(img)"
                    >
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <el-divider></el-divider>

          <!-- 被申请人信息 -->
          <div class="party-section">
            <h3>被申请人（被告方）</h3>
            <el-row :gutter="20">
              <el-col :span="6">
                <img :src="caseDetail.defendantAvatar" class="user-avatar">
                <div class="user-name">{{ caseDetail.defendantName }}</div>
              </el-col>
              <el-col :span="18">
                <!-- 如果纠纷还在待审且当前用户是被申请人，显示上传证据 -->
                <div v-if="isDefendant && caseDetail.status === '0'">
                  <div class="description-section">
                    <h4>你的答辩：</h4>
                    <el-input
                      v-model="defendantDescription"
                      type="textarea"
                      :rows="3"
                      placeholder="请输入你的答辩意见"
                      maxlength="500"
                      show-word-limit
                    ></el-input>
                  </div>
                  <div class="evidence-section">
                    <h4>上传证据：</h4>
                    <el-upload
                      ref="defendantUploadRef"
                      :action="uploadUrl"
                      :headers="uploadHeaders"
                      :file-list="defendantFileList"
                      :on-success="handleDefendantUploadSuccess"
                      :before-upload="beforeUpload"
                      :limit="5"
                      multiple
                      list-type="picture-card"
                    >
                      <i class="el-icon-plus"></i>
                    </el-upload>
                  </div>
                  <el-button
                    type="primary"
                    @click="submitDefenseEvidence"
                    :loading="submittingDefense"
                  >
                    提交答辩
                  </el-button>
                </div>

                <!-- 已有答辩内容时显示 -->
                <div v-else>
                  <div class="description-section" v-if="caseDetail.defendantDescription">
                    <h4>被申请人描述：</h4>
                    <p>{{ caseDetail.defendantDescription }}</p>
                  </div>
                  <div class="evidence-section" v-if="caseDetail.defendantEvidenceImages">
                    <h4>被申请人证据：</h4>
                    <div class="images-grid">
                      <img
                        v-for="(img, idx) in caseDetail.defendantEvidenceImages.split(',')"
                        :key="`defendant-${idx}`"
                        :src="img"
                        class="evidence-img"
                        @click="previewImage(img)"
                      >
                    </div>
                  </div>
                  <p v-else class="no-data">暂未提交答辩</p>
                </div>
              </el-col>
            </el-row>
          </div>

          <el-divider v-if="caseDetail.status !== '0'"></el-divider>

          <!-- 管理员裁定结果 -->
          <div v-if="caseDetail.status !== '0'" class="judgment-section">
            <h3>管理员裁定结果</h3>
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="info-item">
                  <span class="label">裁定结果：</span>
                  <el-tag :type="getJudgmentType(caseDetail.status)">
                    {{ getJudgmentText(caseDetail.status) }}
                  </el-tag>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="info-item">
                  <span class="label">处理人：</span>
                  <span>{{ caseDetail.handlerName || '管理员' }}</span>
                </div>
              </el-col>
            </el-row>
            <div class="info-item">
              <span class="label">裁定理由：</span>
              <p>{{ caseDetail.handleReason }}</p>
            </div>
          </div>
        </div>
      </el-skeleton>
    </el-card>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="imageViewerVisible"
      :on-close="closeImageViewer"
      :url-list="[previewImageUrl]"
    ></el-image-viewer>
  </div>
</template>

<script>
import { getDisputeDetail, submitDefenseEvidence } from '@/api/dispute'
import { getToken } from '@/utils/auth'
import { formatDate } from '@/utils/datetime'

export default {
  name: 'DisputeDetail',
  data() {
    return {
      caseId: null,
      caseDetail: null,
      loading: false,
      defendantDescription: '',
      defendantFileList: [],
      submittingDefense: false,
      imageViewerVisible: false,
      previewImageUrl: '',
      uploadUrl: process.env.VUE_APP_BASE_API + '/common/upload',
      uploadHeaders: {
        Authorization: 'Bearer ' + getToken()
      },
      currentUserId: null
    }
  },
  computed: {
    isDefendant() {
      return this.caseDetail && this.caseDetail.defendantId === this.currentUserId
    }
  },
  created() {
    this.caseId = this.$route.params.id
    this.currentUserId = this.$store.state.user.id
    this.fetchDetail()
  },
  methods: {
    fetchDetail() {
      this.loading = true
      getDisputeDetail(this.caseId).then(res => {
        this.caseDetail = res.data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
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
    getJudgmentType(status) {
      if (status === '3') return 'warning'
      return 'danger'
    },
    getJudgmentText(status) {
      const texts = {
        '1': '判定申请人责任',
        '2': '判定被申请人责任',
        '3': '纠纷驳回'
      }
      return texts[status] || '已处理'
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
    beforeUpload(file) {
      const isImage = ['image/jpeg', 'image/png'].includes(file.type)
      const isLt5M = file.size / 1024 / 1024 < 5
      if (!isImage) {
        this.$message.error('只支持jpg/png格式的图片')
        return false
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过5MB')
        return false
      }
      return true
    },
    handleDefendantUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.defendantFileList = fileList
      }
    },
    submitDefenseEvidence() {
      if (!this.defendantDescription.trim()) {
        this.$message.error('请输入答辩意见')
        return
      }

      this.submittingDefense = true
      const params = {
        caseId: this.caseId,
        defendantDescription: this.defendantDescription,
        defendantEvidenceImages: this.defendantFileList.map(f => f.response?.fileName || f.url).join(',')
      }

      submitDefenseEvidence(params).then(res => {
        if (res.code === 200) {
          this.$message.success('答辩提交成功')
          this.fetchDetail()
          this.defendantDescription = ''
          this.defendantFileList = []
        } else {
          this.$message.error(res.msg || '提交失败')
        }
      }).catch(() => {
        this.$message.error('提交失败，请重试')
      }).finally(() => {
        this.submittingDefense = false
      })
    },
    previewImage(url) {
      this.previewImageUrl = url
      this.imageViewerVisible = true
    },
    closeImageViewer() {
      this.imageViewerVisible = false
    },
    formatDate(date) {
      return formatDate(date, 'YYYY-MM-DD HH:mm:ss')
    }
  }
}
</script>

<style scoped>
.dispute-detail-container {
  padding: 20px;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.info-section {
  margin-bottom: 20px;
}

.info-item {
  margin-bottom: 12px;
  display: flex;
  align-items: flex-start;
}

.info-item .label {
  min-width: 100px;
  font-weight: 500;
  color: #333;
}

.party-section {
  margin: 20px 0;
}

.party-section h3 {
  margin-bottom: 15px;
  font-size: 16px;
  color: #333;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.user-name {
  text-align: center;
  margin-top: 8px;
  font-weight: 500;
}

.description-section {
  margin-bottom: 20px;
}

.description-section h4 {
  margin-bottom: 10px;
  color: #333;
}

.description-section p {
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.evidence-section {
  margin-bottom: 20px;
}

.evidence-section h4 {
  margin-bottom: 10px;
  color: #333;
}

.images-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.evidence-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.3s;
}

.evidence-img:hover {
  transform: scale(1.05);
}

.no-data {
  color: #999;
  font-style: italic;
}

.judgment-section {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin: 20px 0;
}

.judgment-section h3 {
  margin-top: 0;
}

@media (max-width: 768px) {
  .dispute-detail-container {
    padding: 10px;
  }

  .user-avatar {
    width: 60px;
    height: 60px;
  }
}
</style>
