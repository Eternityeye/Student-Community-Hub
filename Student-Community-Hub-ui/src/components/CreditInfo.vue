<template>
  <div class="credit-info-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span class="title">信誉信息</span>
      </div>

      <!-- 信誉分卡片 -->
      <el-row :gutter="20" class="credit-stats">
        <el-col :span="12" class="stat-item">
          <div class="stat-content">
            <div class="stat-label">信誉分</div>
            <div class="stat-value" :style="{ color: getScoreColor(creditInfo.creditScore) }">
              {{ creditInfo.creditScore }}
            </div>
            <div class="stat-progress">
              <el-progress
                :percentage="creditInfo.creditScore"
                :color="getProgressColor"
              ></el-progress>
            </div>
          </div>
        </el-col>

        <el-col :span="12" class="stat-item">
          <div class="stat-content">
            <div class="stat-label">纠纷次数</div>
            <div class="stat-value">{{ creditInfo.disputeCount }}</div>
            <div class="stat-hint">
              <span v-if="creditInfo.disputeCount === 0" style="color: #67c23a;">
                目前信誉良好 ✓
              </span>
              <span v-else-if="creditInfo.disputeCount > 3" style="color: #f56c6c;">
                ⚠ 建议改善
              </span>
              <span v-else style="color: #e6a23c;">
                需要注意
              </span>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 禁止接单提示 -->
      <el-alert
        v-if="creditInfo.banUntil"
        type="error"
        :title="getBanMessage()"
        :closable="false"
        style="margin: 20px 0;"
      ></el-alert>

      <!-- 信誉历史 -->
      <div v-if="creditLogs.length > 0" class="credit-history">
        <h4 style="margin: 20px 0 15px;">信誉变动历史（最近10条）</h4>
        <el-timeline>
          <el-timeline-item
            v-for="log in creditLogs"
            :key="log.logId"
            :timestamp="formatDate(log.createTime)"
            placement="top"
          >
            <p>
              <strong>{{ getChangeTypeText(log.changeType) }}</strong>
              <span :style="{ color: log.changeScore < 0 ? '#f56c6c' : '#67c23a' }">
                {{ log.changeScore > 0 ? '+' : '' }}{{ log.changeScore }}分
              </span>
            </p>
            <p style="color: #666; font-size: 12px; margin-top: 5px;">
              {{ log.reason }}
            </p>
          </el-timeline-item>
        </el-timeline>
      </div>

      <el-empty v-else description="暂无信誉变动记录" style="margin-top: 30px;"></el-empty>

      <!-- 帮助文案 -->
      <div class="help-section">
        <h4>信誉机制说明</h4>
        <ul>
          <li>初始信誉分：100分</li>
          <li>每次纠纷被判定有责任：扣10分</li>
          <li>信誉分≤0：禁止接单30天</li>
          <li>禁止期满自动解禁恢复接单权限</li>
          <li>保持良好信誉有利于获得更多任务机会</li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getUserCredit, getCreditLog } from '@/api/dispute'
import { formatDate as formatDateUtil } from '@/utils/datetime'

export default {
  name: 'CreditInfo',
  props: {
    userId: [String, Number]
  },
  data() {
    return {
      creditInfo: {
        creditScore: 100,
        disputeCount: 0,
        banUntil: null,
        banReason: ''
      },
      creditLogs: [],
      loading: false
    }
  },
  computed: {
    getProgressColor() {
      return (percentage) => {
        if (percentage >= 80) return '#67c23a'
        if (percentage >= 60) return '#e6a23c'
        return '#f56c6c'
      }
    }
  },
  watch: {
    userId: {
      handler(newVal) {
        if (newVal) {
          this.fetchCreditInfo()
        }
      },
      immediate: true
    }
  },
  methods: {
    fetchCreditInfo() {
      if (!this.userId) {
        return
      }

      this.loading = true

      // 获取信誉信息
      getUserCredit(this.userId).then(res => {
        this.creditInfo = res.data || this.creditInfo
      }).catch(() => {
        // 处理错误，保持默认值
      }).finally(() => {
        this.loading = false
      })

      // 获取信誉历史
      getCreditLog(this.userId).then(res => {
        this.creditLogs = res.data || []
      }).catch(() => {
        // 处理错误
      })
    },
    getScoreColor(score) {
      if (score >= 80) return '#67c23a'
      if (score >= 60) return '#e6a23c'
      return '#f56c6c'
    },
    getBanMessage() {
      const banTime = this.creditInfo.banUntil
      const now = new Date()
      const banDate = new Date(banTime)

      if (banDate > now) {
        const daysLeft = Math.ceil((banDate - now) / (1000 * 60 * 60 * 24))
        return `⚠️ 你的账号已被禁止接单，原因：${this.creditInfo.banReason || '信誉分不足'}。预计${daysLeft}天后自动解禁。`
      }
      return '禁止接单期已满，可重新接单'
    },
    getChangeTypeText(type) {
      const types = {
        'dispute_loss': '纠纷扣分',
        'appeal_resolve': '申诉解禁',
        'manual_restore': '管理员恢复'
      }
      return types[type] || type
    },
    formatDate(date) {
      return formatDateUtil(date, 'YYYY-MM-DD HH:mm:ss')
    }
  }
}
</script>

<style scoped>
.credit-info-container {
  margin: 20px 0;
}

.title {
  font-size: 16px;
  font-weight: bold;
}

.credit-stats {
  margin-bottom: 30px;
}

.stat-item {
  margin-bottom: 20px;
}

.stat-content {
  padding: 30px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  text-align: center;
  min-height: 160px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-item:nth-child(2) .stat-content {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  margin: 10px 0;
}

.stat-hint {
  font-size: 12px;
  margin-top: 8px;
  opacity: 0.9;
  margin-top: 14px;
}

.stat-progress {
  margin-top: 15px;
}

.stat-progress >>> .el-progress__bar {
  background-color: rgba(255, 255, 255, 0.3);
}

.stat-progress >>> .el-progress__text {
  color: white;
}

.credit-history {
  margin-top: 30px;
}

.credit-history h4 {
  color: #333;
  font-weight: 500;
}

.help-section {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.help-section h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 14px;
}

.help-section ul {
  margin: 10px 0;
  padding-left: 20px;
}

.help-section li {
  margin-bottom: 8px;
  color: #666;
  font-size: 13px;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .credit-stats {
    grid-template-columns: 1fr;
  }

  .stat-content {
    min-height: 140px;
    padding: 25px 15px;
  }

  .stat-value {
    font-size: 28px;
  }

  .credit-history {
    margin-top: 20px;
  }

  .help-section {
    margin-top: 15px;
    padding: 12px;
  }

  .help-section h4 {
    font-size: 13px;
  }

  .help-section li {
    font-size: 12px;
    margin-bottom: 6px;
  }
}
</style>
