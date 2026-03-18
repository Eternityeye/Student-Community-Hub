<template>
  <el-dialog
    title="举报"
    :visible.sync="dialogVisible"
    width="500px"
    append-to-body
    @close="handleClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="0px">
      <el-form-item prop="reasons">
        <el-checkbox-group v-model="form.reasons">
          <el-checkbox label="违法违规内容" name="type"></el-checkbox>
          <el-checkbox label="色情低俗内容" name="type"></el-checkbox>
          <el-checkbox label="虚假信息" name="type"></el-checkbox>
          <el-checkbox label="侵权内容" name="type"></el-checkbox>
          <el-checkbox label="垃圾广告" name="type"></el-checkbox>
          <el-checkbox label="人身攻击" name="type"></el-checkbox>
          <el-checkbox label="其他" name="type"></el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请详细描述举报原因（选填）"
        ></el-input>
      </el-form-item>
      <div class="report-tip">
        <i class="el-icon-warning"></i>
        <span>今日剩余举报次数：{{ remainingReports }}/5</span>
      </div>
      <div v-if="hasReported" class="report-warning">
        <i class="el-icon-warning"></i>
        <span>你已经举报过该帖子，不能重复举报</span>
      </div>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button
        type="primary"
        @click="submitReport"
        :disabled="remainingReports <= 0 || hasReported"
        :loading="loading"
      >提交举报</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { reportPost } from '@/api/social/post'
import { checkReportedPost } from '@/api/social/report'

export default {
  name: 'ReportDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    postId: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      form: {
        reasons: [],
        description: ''
      },
      rules: {
        reasons: [
          { type: 'array', required: true, message: '请至少选择一个举报原因', trigger: 'change' }
        ]
      },
      remainingReports: 5,
      hasReported: false,
      loading: false
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.initDialog()
      }
    }
  },
  methods: {
    initDialog() {
      // 初始化对话框时检查剩余举报次数和是否已举报
      this.updateRemainingReports()
      this.checkIfReported()
    },
    updateRemainingReports() {
      // 从后端或localStorage获取今日举报次数
      const today = new Date().toDateString()
      const reportData = JSON.parse(localStorage.getItem('reportData') || '{}')
      if (reportData.date !== today) {
        this.remainingReports = 5
      } else {
        this.remainingReports = Math.max(0, 5 - (reportData.count || 0))
      }
    },
    checkIfReported() {
      // 检查用户是否已举报过这个帖子
      if (!this.postId) return

      checkReportedPost(this.postId).then(response => {
        this.hasReported = response.data || false
        if (this.hasReported) {
          this.$message.warning('你已经举报过该帖子，不能重复举报')
        }
      }).catch(() => {
        this.hasReported = false
      })
    },
    submitReport() {
      // 检查是否已举报
      if (this.hasReported) {
        this.$message.warning('你已经举报过该帖子，不能重复举报')
        return
      }

      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.remainingReports <= 0) {
            this.$message.warning('今日举报次数已用完')
            return
          }

          this.loading = true
          reportPost({
            postId: this.postId,
            reasons: this.form.reasons,
            description: this.form.description
          }).then(response => {
            this.$message.success('举报成功，我们会尽快处理')

            // 更新举报次数
            const today = new Date().toDateString()
            const reportData = JSON.parse(localStorage.getItem('reportData') || '{}')
            if (reportData.date !== today) {
              localStorage.setItem('reportData', JSON.stringify({ date: today, count: 1 }))
            } else {
              reportData.count = (reportData.count || 0) + 1
              localStorage.setItem('reportData', JSON.stringify(reportData))
            }

            // 更新UI显示
            this.updateRemainingReports()
            this.hasReported = true
            this.loading = false
            this.handleClose()
          }).catch(error => {
            this.$message.error(error.msg || '举报失败')
            this.loading = false
          })
        }
      })
    },
    handleClose() {
      this.form = {
        reasons: [],
        description: ''
      }
      this.$refs.form && this.$refs.form.resetFields()
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style scoped lang="scss">
.el-checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.report-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #E6A23C;
  font-size: 14px;
  margin-top: 10px;
}

.report-warning {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #F56C6C;
  font-size: 14px;
  margin-top: 10px;
  padding: 10px;
  background-color: #fef0f0;
  border-radius: 4px;
}
</style>
