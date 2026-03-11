<template>
  <el-dialog
    :visible.sync="visible"
    width="90%"
    top="5vh"
    custom-class="notice-view-dialog"
    @close="$emit('update:show', false)">
    <div slot="title" class="nv-title">{{ detail.noticeTitle }}</div>
    <div class="nv-meta">
      <span class="nv-meta-item"><i class="el-icon-user"></i>{{ detail.createBy }}</span>
      <span class="nv-meta-item"><i class="el-icon-time"></i>{{ detail.createTime }}</span>
    </div>
    <div class="nv-divider"></div>
    <div class="nv-body" v-html="detail.noticeContent"></div>
  </el-dialog>
</template>

<script>
export default {
  name: 'NoticeViewDialog',
  props: {
    show: { type: Boolean, default: false },
    detail: { type: Object, default: () => ({}) }
  },
  computed: {
    visible: {
      get() { return this.show },
      set(val) { this.$emit('update:show', val) }
    }
  }
}
</script>

<style lang="scss">
.notice-view-dialog {
  max-width: 800px;

  .el-dialog__header {
    padding: 20px 24px 16px;
    border-bottom: none;
  }
  .el-dialog__body {
    padding: 0 24px 24px;
    height: calc(85vh - 120px);
    overflow-y: auto;
  }

  @media (max-width: 768px) {
    width: 95% !important;
    margin: 0 auto !important;

    .el-dialog__header {
      padding: 16px 16px 12px;
    }
    .el-dialog__body {
      padding: 0 16px 16px;
      height: calc(90vh - 100px);
    }
  }
}
</style>

<style scoped lang="scss">
.nv-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
}

.nv-meta {
  display: flex;
  gap: 24px;
  padding: 12px 0 16px;
  color: #909399;
  font-size: 14px;
}

.nv-meta-item {
  display: flex;
  align-items: center;
  gap: 6px;

  i { font-size: 14px; }
}

.nv-divider {
  height: 1px;
  background: #e4e7ed;
  margin-bottom: 20px;
}

.nv-body {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
  min-height: 100px;

  ::v-deep img {
    max-width: 100%;
    height: auto;
    border-radius: 4px;
    margin: 16px 0;
  }
  ::v-deep p { margin: 12px 0; }
  ::v-deep h1, ::v-deep h2, ::v-deep h3 {
    margin: 20px 0 12px;
    color: #303133;
  }
  ::v-deep ul, ::v-deep ol {
    padding-left: 24px;
    margin: 12px 0;
  }
  ::v-deep code {
    background: #f5f7fa;
    padding: 2px 6px;
    border-radius: 3px;
    font-family: monospace;
  }
}

@media (max-width: 768px) {
  .nv-title {
    font-size: 18px;
  }

  .nv-meta {
    gap: 16px;
    font-size: 13px;
    flex-wrap: wrap;
  }

  .nv-divider {
    margin-bottom: 16px;
  }

  .nv-body {
    font-size: 14px;
    line-height: 1.7;

    ::v-deep img {
      margin: 12px 0;
    }
    ::v-deep h1, ::v-deep h2, ::v-deep h3 {
      margin: 16px 0 10px;
    }
    ::v-deep ul, ::v-deep ol {
      padding-left: 20px;
    }
  }
}
</style>
