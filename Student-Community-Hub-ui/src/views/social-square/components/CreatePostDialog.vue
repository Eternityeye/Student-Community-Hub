<template>
  <el-dialog
    title="帖子内容"
    :visible.sync="dialogVisible"
    width="700px"
    append-to-body
    @close="handleClose"
    class="create-post-dialog"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item prop="content" label="输入内容">
        <editor
          v-model="form.content"
          :min-height="250"
          placeholder="分享你的想法..."
        />
      </el-form-item>
      <el-form-item label="添加标签">
        <div class="tag-input-wrapper">
          <el-tag
            v-for="tag in form.tags"
            :key="tag"
            closable
            @close="handleRemoveTag(tag)"
            class="tag-item"
          >
            #{{ tag }}
          </el-tag>
          <el-input
            v-if="showTagInput"
            v-model="newTag"
            ref="tagInput"
            size="small"
            class="tag-input"
            @keyup.enter.native="handleAddTag"
            @blur="handleAddTag"
            maxlength="10"
          ></el-input>
          <el-button
            v-else
            size="small"
            @click="showTagInputField"
            class="add-tag-btn"
          >+ 添加标签</el-button>
        </div>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="submitPost" :loading="submitting">发 布</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Editor from '@/components/Editor'
import ImageUpload from '@/components/ImageUpload'
import { addPost } from '@/api/social/post'

export default {
  name: 'CreatePostDialog',
  components: {
    Editor,
    ImageUpload
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      form: {
        content: '',
        images: [],
        tags: []
      },
      rules: {
        content: [
          { required: true, message: '请输入帖子内容', trigger: 'blur' }
        ]
      },
      newTag: '',
      showTagInput: false,
      submitting: false
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
  methods: {
    submitPost() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitting = true
          addPost(this.form).then(response => {
            this.$message.success('发布成功')
            this.handleClose()
            this.$emit('post-created')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    showTagInputField() {
      if (this.form.tags.length >= 5) {
        this.$message.warning('最多添加5个标签')
        return
      }
      this.showTagInput = true
      this.$nextTick(() => {
        this.$refs.tagInput.$refs.input.focus()
      })
    },
    handleAddTag() {
      const tag = this.newTag.trim()
      if (tag && !this.form.tags.includes(tag)) {
        if (this.form.tags.length >= 5) {
          this.$message.warning('最多添加5个标签')
        } else {
          this.form.tags.push(tag)
        }
      }
      this.newTag = ''
      this.showTagInput = false
    },
    handleRemoveTag(tag) {
      this.form.tags = this.form.tags.filter(t => t !== tag)
    },
    handleClose() {
      this.form = {
        content: '',
        images: [],
        tags: []
      }
      this.newTag = ''
      this.showTagInput = false
      this.$refs.form && this.$refs.form.resetFields()
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style scoped lang="scss">
.create-post-dialog {
  ::v-deep .el-dialog__body {
    padding: 20px 30px;
  }
}

.tag-input-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;

  .tag-item {
    background: #f0f7ff;
    border-color: #d0e8ff;
    color: #409eff;
  }

  .tag-input {
    width: 120px;
  }

  .add-tag-btn {
    border-style: dashed;
  }
}

@media (max-width: 768px) {
  ::v-deep .el-dialog {
    width: 90% !important;
  }
}
</style>
