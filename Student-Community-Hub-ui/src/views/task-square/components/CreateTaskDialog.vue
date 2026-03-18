<template>
  <el-dialog
    title="发布悬赏任务"
    :visible.sync="visible"
    width="700px"
    @close="handleClose"
    append-to-body
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="任务标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入任务标题，不超过50字" maxlength="50" />
      </el-form-item>

      <el-form-item label="任务分类" prop="category">
        <el-select v-model="form.category" placeholder="请选择任务分类">
          <el-option label="取快递" value="0" />
          <el-option label="占座" value="1" />
          <el-option label="代购" value="2" />
          <el-option label="答疑" value="3" />
          <el-option label="其他" value="4" />
        </el-select>
      </el-form-item>

      <el-form-item label="任务描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="请详细描述任务内容"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="悬赏说明" prop="reward">
        <el-input
          v-model="form.reward"
          placeholder="如：20元、一杯奶茶、以物易物、面议等"
          maxlength="100"
        />
      </el-form-item>

      <el-form-item label="任务地点" prop="location">
        <el-input
          v-model="form.location"
          placeholder="请输入任务具体地点"
          maxlength="100"
        />
      </el-form-item>

      <el-form-item label="截止时间" prop="deadline">
        <el-date-picker
          v-model="form.deadline"
          type="datetime"
          placeholder="选择截止时间"
          format="yyyy-MM-dd HH:mm"
          value-format="yyyy-MM-dd HH:mm:ss"
        />
      </el-form-item>

      <el-form-item label="任务图片" prop="images">
        <image-upload
          v-model="form.images"
          :limit="3"
          :size-limit="5242880"
          multiple
        />
      </el-form-item>
    </el-form>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">发布</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { addTask } from '@/api/task/task'
import ImageUpload from '@/components/ImageUpload'

export default {
  name: 'CreateTaskDialog',
  components: {
    ImageUpload
  },
  props: {
    value: {
      type: Boolean,
      default: false
    }
  },
  data() {
    const validateTitle = (rule, value, callback) => {
      if (!value) {
        callback(new Error('任务标题不能为空'))
      } else if (value.trim().length === 0) {
        callback(new Error('任务标题不能为空'))
      } else {
        callback()
      }
    }

    const validateDescription = (rule, value, callback) => {
      if (!value) {
        callback(new Error('任务描述不能为空'))
      } else if (value.trim().length === 0) {
        callback(new Error('任务描述不能为空'))
      } else {
        callback()
      }
    }

    const validateReward = (rule, value, callback) => {
      if (!value) {
        callback(new Error('悬赏说明不能为空'))
      } else if (value.trim().length === 0) {
        callback(new Error('悬赏说明不能为空'))
      } else {
        callback()
      }
    }

    const validateLocation = (rule, value, callback) => {
      if (!value) {
        callback(new Error('任务地点不能为空'))
      } else if (value.trim().length === 0) {
        callback(new Error('任务地点不能为空'))
      } else {
        callback()
      }
    }

    return {
      form: {
        title: '',
        category: '',
        description: '',
        reward: '',
        location: '',
        deadline: null,
        images: []
      },
      rules: {
        title: [{ validator: validateTitle, trigger: 'blur' }],
        category: [{ required: true, message: '请选择任务分类', trigger: 'change' }],
        description: [{ validator: validateDescription, trigger: 'blur' }],
        reward: [{ validator: validateReward, trigger: 'blur' }],
        location: [{ validator: validateLocation, trigger: 'blur' }],
        deadline: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
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
  methods: {
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return

        this.submitting = true
        addTask(this.form).then(() => {
          this.$message.success('任务发布成功')
          this.$emit('post-created')
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
        title: '',
        category: '',
        description: '',
        reward: '',
        location: '',
        deadline: null,
        images: []
      }
    }
  }
}
</script>

<style scoped>
.el-form {
  padding: 20px 0;
}

.el-form-item {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>
