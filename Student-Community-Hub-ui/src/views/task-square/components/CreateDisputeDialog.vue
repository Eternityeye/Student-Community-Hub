<template>
  <el-dialog
    title="发起纠纷"
    :visible.sync="visible"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="form" label-width="100px">
      <!-- 基本信息（只读） -->
      <el-form-item label="任务">
        <span>{{ taskInfo }}</span>
      </el-form-item>

      <el-form-item label="对方用户">
        <span>{{ oppositeUserName }}</span>
      </el-form-item>

      <!-- 纠纷原因 -->
      <el-form-item label="纠纷原因" prop="reason">
        <el-select
          v-model="form.reason"
          placeholder="请选择纠纷原因"
        >
          <el-option label="服务质量不达预期" value="quality_issue"></el-option>
          <el-option label="虚假宣传/描述不符" value="false_description"></el-option>
          <el-option label="未按时完成" value="timeout"></el-option>
          <el-option label="收费不合理" value="unfair_price"></el-option>
          <el-option label="态度恶劣/骚扰" value="bad_attitude"></el-option>
          <el-option label="其他" value="other"></el-option>
        </el-select>
      </el-form-item>

      <!-- 详细描述 -->
      <el-form-item label="详细描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="请详细描述纠纷情况及理由（最多500字）"
          maxlength="500"
          show-word-limit
        ></el-input>
      </el-form-item>

      <!-- 上传证据 -->
      <el-form-item label="证据图片" prop="evidenceImages">
        <el-upload
          ref="uploadRef"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :file-list="fileList"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :before-upload="beforeUpload"
          :limit="5"
          multiple
          list-type="picture-card"
        >
          <i class="el-icon-plus"></i>
          <div slot="tip" class="el-upload__tip">支持jpg/png格式，单个文件不超过5MB，最多5张</div>
        </el-upload>
      </el-form-item>
    </el-form>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="submitDispute" :loading="submitting">
        提交纠纷
      </el-button>
    </span>
  </el-dialog>
</template>

<script>
import { createDispute } from '@/api/dispute'
import { getToken } from '@/utils/auth'

export default {
  name: 'CreateDisputeDialog',
  props: {
    visible: Boolean,
    taskId: [String, Number],
    taskTitle: String,
    oppositeUserId: [String, Number],
    oppositeUserName: String,
    myRole: {
      // 'publisher' 或 'taker' - 用来确定对方角色
      type: String,
      default: 'publisher'
    }
  },
  data() {
    return {
      form: {
        reason: '',
        description: '',
        evidenceImages: []
      },
      rules: {
        reason: [
          { required: true, message: '请选择纠纷原因', trigger: 'change' }
        ],
        description: [
          { required: true, message: '请输入详细描述', trigger: 'blur' },
          { min: 10, max: 500, message: '描述长度需要在10-500字之间', trigger: 'blur' }
        ]
      },
      fileList: [],
      submitting: false,
      uploadUrl: process.env.VUE_APP_BASE_API + '/common/upload',
      uploadHeaders: {
        Authorization: 'Bearer ' + getToken()
      }
    }
  },
  computed: {
    taskInfo() {
      return this.taskTitle || '---'
    }
  },
  methods: {
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
    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.form.evidenceImages = fileList
          .filter(f => f.response && f.response.code === 200)
          .map(f => f.response.fileName)
      } else {
        this.$message.error('图片上传失败')
      }
    },
    handleUploadError(err, file, fileList) {
      this.$message.error('图片上传失败，请重试')
    },
    submitDispute() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return

        this.submitting = true
        try {
          const params = {
            taskId: this.taskId,
            defendantId: this.oppositeUserId,
            reason: this.form.reason,
            plaintiffDescription: this.form.description,
            plaintiffEvidenceImages: this.form.evidenceImages.join(',')
          }

          const result = await createDispute(params)
          if (result.code === 200) {
            this.$message.success('纠纷发起成功，管理员会在24小时内审理')
            this.$emit('success')
            this.handleClose()
          } else {
            this.$message.error(result.msg || '发起纠纷失败')
          }
        } catch (error) {
        } finally {
          this.submitting = false
        }
      })
    },
    handleClose() {
      this.$refs.form.resetFields()
      this.fileList = []
      this.form.evidenceImages = []
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style scoped>
</style>
