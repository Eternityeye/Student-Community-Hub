<template>
  <el-dialog
    title="提交评价"
    :visible.sync="visible"
    width="500px"
    @close="handleClose"
    append-to-body
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="评分" prop="rating">
        <el-rate v-model="form.rating" :max="5" allow-half />
      </el-form-item>

      <el-form-item label="评价内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="4"
          placeholder="请输入评价内容（可选）"
          maxlength="200"
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
    value: {
      type: Boolean,
      default: false
    },
    taskId: {
      type: [String, Number],
      required: true
    },
    revieweeId: {
      type: [String, Number],
      default: null
    },
    roleType: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      form: {
        taskId: this.taskId,
        revieweeId: this.revieweeId,
        roleType: this.roleType,
        rating: 5,
        content: ''
      },
      rules: {
        rating: [{ required: true, message: '请选择评分', trigger: 'change' }]
      },
      submitting: false
    }
  },
  computed: {
    visible: {
      get() {
        return this.value
      },
      set(val) {
        this.$emit('input', val)
      }
    }
  },
  watch: {
    taskId(newVal) {
      this.form.taskId = newVal
    },
    revieweeId(newVal) {
      this.form.revieweeId = newVal
    },
    roleType(newVal) {
      this.form.roleType = newVal
    }
  },
  methods: {
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return

        this.submitting = true
        addReview(this.form).then(() => {
          this.$message.success('评价提交成功')
          this.$emit('review-submitted')
          this.handleClose()
        }).finally(() => {
          this.submitting = false
        })
      })
    },
    handleClose() {
      this.visible = false
      this.$refs.form.resetFields()
      this.form = {
        taskId: this.taskId,
        revieweeId: this.revieweeId,
        roleType: this.roleType,
        rating: 5,
        content: ''
      }
    }
  }
}
</script>

<style scoped>
.el-form {
  padding: 20px 0;
}

.dialog-footer {
  text-align: right;
}
</style>
