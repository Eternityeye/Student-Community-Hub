<template>
  <el-dialog
    title="任务评价"
    :visible.sync="visible"
    width="600px"
    @close="handleClose"
    append-to-body
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <!-- 任务信息（只读） -->
      <el-form-item label="任务">
        <span>{{ taskTitle }}</span>
      </el-form-item>

      <!-- 被评价人（只读） -->
      <el-form-item label="被评价人">
        <span>{{ revieweeUserName }}</span>
      </el-form-item>

      <!-- 星级评分 -->
      <el-form-item label="评分" prop="rating">
        <el-rate v-model="form.rating" :max="5" allow-half show-text text-color="#ff9900" />
      </el-form-item>

      <!-- 评价内容 -->
      <el-form-item label="评价内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="4"
          placeholder="请输入您的评价（可选）"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">提交评价</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { addReview } from '@/api/task/task'

export default {
  name: 'ReviewDialog',
  props: {
    visible: Boolean,
    taskId: {
      type: [String, Number],
      required: true
    },
    taskTitle: String,
    revieweeUserId: {
      type: [String, Number],
      default: null
    },
    revieweeUserName: String,
    myRole: {
      type: String,
      default: 'publisher'
    }
  },
  data() {
    return {
      form: {
        taskId: this.taskId,
        revieweeId: this.revieweeUserId,
        rating: 5,
        content: '',
        roleType: this.myRole === 'publisher' ? '0' : '1'
      },
      rules: {
        rating: [{ required: true, message: '请选择评分', trigger: 'change' }]
      },
      submitting: false
    }
  },
  watch: {
    taskId(newVal) {
      this.form.taskId = newVal
    },
    revieweeUserId(newVal) {
      this.form.revieweeId = newVal
    },
    myRole(newVal) {
      this.form.roleType = newVal === 'publisher' ? '0' : '1'
    }
  },
  methods: {
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return

        this.submitting = true
        const submitData = {
          ...this.form,
          roleType: this.myRole === 'publisher' ? '0' : '1'
        }
        addReview(submitData).then(() => {
          this.$message.success('评价提交成功')
          this.$emit('success')
          this.handleClose()
        }).finally(() => {
          this.submitting = false
        })
      })
    },
    handleClose() {
      this.$emit('update:visible', false)
      this.$refs.form.resetFields()
      this.form = {
        taskId: this.taskId,
        revieweeId: this.revieweeUserId,
        rating: 5,
        content: '',
        roleType: this.myRole === 'publisher' ? '0' : '1'
      }
    }
  }
}
</script>

<style scoped>
</style>
