<template>
  <div class="welcome-container">
    <div class="header-section">
      <div class="user-info">
        <img :src="userAvatar" class="avatar" />
        <div class="user-text">
          <h1 class="greeting">{{ greeting }}，{{ userName }}</h1>
          <p class="subtitle">{{ currentDate }} {{ currentWeek }}</p>
        </div>
      </div>
      <div class="time-display">{{ currentTime }}</div>
    </div>

    <div class="stats-grid">
      <div class="stat-card blue">
        <div class="stat-icon"><i class="el-icon-user-solid"></i></div>
        <div class="stat-content">
          <div class="stat-label">用户名</div>
          <div class="stat-value">{{ userInfo.userName || '-' }}</div>
        </div>
      </div>
      <div class="stat-card cyan">
        <div class="stat-icon"><i class="el-icon-s-custom"></i></div>
        <div class="stat-content">
          <div class="stat-label">角色权限</div>
          <div class="stat-value">{{ roleNames }}</div>
        </div>
      </div>
      <div class="stat-card light-blue">
        <div class="stat-icon"><i class="el-icon-time"></i></div>
        <div class="stat-content">
          <div class="stat-label">登录时间</div>
          <div class="stat-value">{{ loginTime }}</div>
        </div>
      </div>
      <div class="stat-card sky">
        <div class="stat-icon"><i class="el-icon-location-information"></i></div>
        <div class="stat-content">
          <div class="stat-label">用户ID</div>
          <div class="stat-value">{{ userInfo.id || '-' }}</div>
        </div>
      </div>
    </div>

    <div class="content-grid">
      <div class="panel quick-actions">
        <div class="panel-header">
          <i class="el-icon-s-grid"></i>
          <span>快捷操作</span>
        </div>
        <div class="action-list">
          <div class="action-item" @click="$router.push('/system/user')">
            <i class="el-icon-user"></i>
            <span>用户管理</span>
          </div>
          <div class="action-item" @click="$router.push('/system/role')">
            <i class="el-icon-s-custom"></i>
            <span>角色管理</span>
          </div>
          <div class="action-item" @click="$router.push('/system/menu')">
            <i class="el-icon-menu"></i>
            <span>菜单管理</span>
          </div>
          <div class="action-item" @click="$router.push('/system/config')">
            <i class="el-icon-setting"></i>
            <span>系统配置</span>
          </div>
          <div class="action-item" @click="$router.push('/monitor/server')">
            <i class="el-icon-monitor"></i>
            <span>服务监控</span>
          </div>
          <div class="action-item" @click="$router.push('/system/user/profile')">
            <i class="el-icon-user-solid"></i>
            <span>个人中心</span>
          </div>
        </div>
      </div>

      <div class="panel system-info">
        <div class="panel-header">
          <i class="el-icon-info"></i>
          <span>系统信息</span>
        </div>
        <div class="info-list">
          <div class="info-row">
            <span class="info-key">平台名称</span>
            <span class="info-val">校园互助社交平台</span>
          </div>
          <div class="info-row">
            <span class="info-key">系统版本</span>
            <span class="info-val">v3.9.1</span>
          </div>
          <div class="info-row">
            <span class="info-key">当前角色</span>
            <span class="info-val">{{ roleNames }}</span>
          </div>
          <div class="info-row">
            <span class="info-key">登录账号</span>
            <span class="info-val">{{ userInfo.userName }}</span>
          </div>
        </div>
      </div>

      <div class="panel notice">
        <div class="panel-header">
          <i class="el-icon-bell"></i>
          <span>校园公告</span>
        </div>
        <div class="notice-content" v-loading="noticeLoading">
          <div class="notice-item" v-for="item in noticeList" :key="item.noticeId" @click="viewNotice(item)">
            <div class="notice-dot"></div>
            <div class="notice-text">{{ item.noticeTitle }}</div>
            <div class="notice-time">{{ item.createTime }}</div>
          </div>
          <div v-if="!noticeLoading && noticeList.length === 0" class="notice-empty">暂无公告</div>
        </div>
      </div>
    </div>

    <el-dialog
      :visible.sync="noticeDialogVisible"
      width="800px"
      top="8vh"
      custom-class="notice-dialog">
      <div slot="title" class="dialog-title-wrapper">
        <span class="dialog-title">{{ noticeDetail.noticeTitle }}</span>
      </div>
      <div class="dialog-meta">
        <span class="meta-item">
          <i class="el-icon-user"></i>
          {{ noticeDetail.createBy }}
        </span>
        <span class="meta-item">
          <i class="el-icon-time"></i>
          {{ noticeDetail.createTime }}
        </span>
      </div>
      <div class="dialog-divider"></div>
      <div class="dialog-content" v-html="noticeDetail.noticeContent"></div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { listNotice, getNotice } from '@/api/system/notice'

export default {
  name: 'Index',
  data() {
    return {
      greeting: '',
      currentTime: '',
      currentDate: '',
      currentWeek: '',
      loginTime: '',
      noticeList: [],
      noticeLoading: false,
      noticeDialogVisible: false,
      noticeDetail: {}
    }
  },
  computed: {
    ...mapGetters(['name', 'nickName', 'avatar', 'roles']),
    userName() {
      return this.nickName || this.name || '管理员'
    },
    userAvatar() {
      return this.avatar || require('@/assets/images/profile.jpg')
    },
    userInfo() {
      return this.$store.state.user
    },
    roleNames() {
      return this.roles.join('、') || '暂无角色'
    }
  },
  mounted() {
    this.updateTime()
    this.loginTime = this.formatDateTime(new Date())
    setInterval(this.updateTime, 1000)
    this.getNoticeList()
  },
  methods: {
    updateTime() {
      const now = new Date()
      const hour = now.getHours()
      if (hour < 6) this.greeting = '凌晨好'
      else if (hour < 9) this.greeting = '早上好'
      else if (hour < 12) this.greeting = '上午好'
      else if (hour < 14) this.greeting = '中午好'
      else if (hour < 18) this.greeting = '下午好'
      else if (hour < 22) this.greeting = '晚上好'
      else this.greeting = '夜深了'

      this.currentTime = now.toLocaleTimeString('zh-CN', { hour12: false })
      this.currentDate = now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
      this.currentWeek = '星期' + ['日', '一', '二', '三', '四', '五', '六'][now.getDay()]
    },
    formatDateTime(date) {
      return date.toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-')
    },
    getNoticeList() {
      this.noticeLoading = true
      listNotice({ pageNum: 1, pageSize: 5, status: '0' }).then(res => {
        this.noticeList = res.rows || []
        this.noticeLoading = false
      }).catch(() => {
        this.noticeLoading = false
      })
    },
    viewNotice(item) {
      getNotice(item.noticeId).then(res => {
        this.noticeDetail = res.data
        this.noticeDialogVisible = true
      })
    }
  }
}
</script>

<style scoped lang="scss">
.welcome-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  padding: 24px;
  box-sizing: border-box;
}

.header-section {
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 20px rgba(25, 118, 210, 0.3);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 4px solid #fff;
  object-fit: cover;
}

.user-text {
  color: #fff;
}

.greeting {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.time-display {
  font-size: 36px;
  font-weight: 700;
  color: #fff;
  font-family: 'Arial', sans-serif;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
  }
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;

  i {
    font-size: 28px;
    color: #fff;
  }
}

.blue .stat-icon { background: linear-gradient(135deg, #1976d2, #2196f3); }
.cyan .stat-icon { background: linear-gradient(135deg, #0097a7, #00bcd4); }
.light-blue .stat-icon { background: linear-gradient(135deg, #0288d1, #03a9f4); }
.sky .stat-icon { background: linear-gradient(135deg, #0277bd, #039be5); }

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
}

.panel {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e4e7ed;

  i {
    font-size: 20px;
    color: #1976d2;
  }
}

.action-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px;
  border-radius: 8px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.3s;

  i {
    font-size: 20px;
    color: #1976d2;
  }

  span {
    font-size: 14px;
    color: #606266;
  }

  &:hover {
    background: #e3f2fd;
    transform: translateX(4px);
  }
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.info-key {
  font-size: 14px;
  color: #909399;
}

.info-val {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.notice-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background 0.2s;

  &:hover {
    background: #f5f7fa;
  }
}

.notice-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #1976d2;
  margin-top: 6px;
  flex-shrink: 0;
}

.notice-text {
  flex: 1;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.notice-time {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.notice-empty {
  text-align: center;
  color: #909399;
  font-size: 14px;
  padding: 20px 0;
}

::v-deep .notice-dialog {
  .el-dialog__body {
    height: calc(85vh - 120px) !important;
    flex: none !important;
  }
}

.dialog-meta {
  display: flex;
  gap: 24px;
  padding: 16px 0;
  color: #909399;
  font-size: 14px;

  .meta-item {
    display: flex;
    align-items: center;
    gap: 6px;

    i {
      font-size: 14px;
    }
  }
}

.dialog-divider {
  height: 1px;
  background: #e4e7ed;
  margin-bottom: 20px;
}

.dialog-content {
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

  ::v-deep p {
    margin: 12px 0;
  }

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

@media (max-width: 1024px) {
  .header-section {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .user-info {
    flex-direction: column;
  }

  .time-display {
    font-size: 28px;
  }
}

@media (max-width: 768px) {
  .welcome-container {
    padding: 16px;
  }

  .header-section {
    padding: 24px;
  }

  .greeting {
    font-size: 22px;
  }

  .avatar {
    width: 64px;
    height: 64px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .content-grid {
    grid-template-columns: 1fr;
  }

  .action-list {
    grid-template-columns: 1fr;
  }
}
</style>
