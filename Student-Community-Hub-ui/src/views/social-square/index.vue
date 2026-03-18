<template>
  <div class="social-square-container">
    <div class="content-wrapper">
      <!-- 左侧帖子流 -->
      <div class="posts-section">
        <div
          v-loading="loading"
          class="posts-list"
          @scroll="handleScroll"
        >
          <post-card
            v-for="post in postList"
            :key="post.postId"
            :post="post"
            :show-delete="post.userId === $store.state.user.id"
            @like-changed="handleLikeChanged"
            @comment-added="handleCommentAdded"
            @post-deleted="handlePostDeleted"
          />
          <div v-if="loadingMore" class="loading-more">
            <i class="el-icon-loading"></i>
            <span>加载中...</span>
          </div>
          <div v-if="postList.length >= total && postList.length > 0" class="no-more">
            没有更多了
          </div>
          <div v-if="postList.length === 0 && !loading" class="empty-state">
            <i class="el-icon-document"></i>
            <p>暂无帖子</p>
          </div>
        </div>
      </div>

      <!-- 右侧发帖按钮 -->
      <div class="sidebar-section">
        <div class="sidebar-sticky">
          <el-button
            type="primary"
            size="large"
            icon="el-icon-edit"
            class="create-post-btn"
            @click="showCreateDialog = true"
          ><span class="btn-text">发布帖子</span></el-button>

          <div class="sidebar-card">
            <div class="card-title">热门标签</div>
            <div class="hot-tags">
              <el-tag
                v-for="tag in hotTags"
                :key="tag"
                size="small"
                class="hot-tag"
              >#{{ tag }}</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 发帖弹窗 -->
    <create-post-dialog
      :visible.sync="showCreateDialog"
      @post-created="handlePostCreated"
    />
  </div>
</template>

<script>
import PostCard from './components/PostCard.vue'
import CreatePostDialog from './components/CreatePostDialog.vue'
import { listPosts } from '@/api/social/post'

export default {
  name: 'SocialSquare',
  components: {
    PostCard,
    CreatePostDialog
  },
  data() {
    return {
      loading: false,
      postList: [],
      showCreateDialog: false,
      hotTags: ['日常', '学习', '前端', '生活', '分享'],
      queryParams: {
        pageNum: 1,
        pageSize: 5
      },
      total: 0,
      loadingMore: false
    }
  },
  created() {
    this.getPostList()
  },
  methods: {
    getPostList() {
      this.loading = true
      listPosts(this.queryParams).then(response => {
        this.postList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    loadMorePosts() {
      if (this.loadingMore || this.postList.length >= this.total) {
        return
      }

      this.loadingMore = true
      this.queryParams.pageNum += 1

      listPosts(this.queryParams).then(response => {
        this.postList = [...this.postList, ...response.rows]
        this.total = response.total
        this.loadingMore = false
      }).catch(() => {
        this.loadingMore = false
        this.queryParams.pageNum -= 1
      })
    },
    handleLikeChanged({ postId, isLiked }) {
      const post = this.postList.find(p => p.postId === postId)
      if (post) {
        post.isLiked = isLiked
        post.likeCount = isLiked ? post.likeCount + 1 : post.likeCount - 1
      }
    },
    handleCommentAdded(postId) {
      const post = this.postList.find(p => p.postId === postId)
      if (post) {
        post.commentCount += 1
      }
      this.getPostList()
    },
    handlePostCreated() {
      this.queryParams.pageNum = 1
      this.getPostList()
    },
    handlePostDeleted(postId) {
      // 从列表中移除被删除的帖子
      this.postList = this.postList.filter(p => p.postId !== postId)
      this.total -= 1
    },
    handleScroll(e) {
      const { scrollTop, scrollHeight, clientHeight } = e.target
      // 滚动到底部前100px时加载更多
      if (scrollTop + clientHeight >= scrollHeight - 100) {
        this.loadMorePosts()
      }
    }
  }
}
</script>

<style scoped lang="scss">
.social-square-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
}

.posts-section {
  .posts-list {
    max-height: calc(100vh - 60px);
    overflow-y: auto;
    padding-right: 8px;

    /* 隐藏滚动条 */
    &::-webkit-scrollbar {
      width: 0;
      display: none;
    }
    scrollbar-width: none;
    -ms-overflow-style: none;
  }

  .empty-state {
    text-align: center;
    padding: 60px 20px;
    color: #999;

    i {
      font-size: 64px;
      margin-bottom: 16px;
    }

    p {
      font-size: 16px;
    }
  }

  .loading-more {
    text-align: center;
    padding: 20px;
    color: #999;
    font-size: 14px;

    i {
      margin-right: 8px;
    }
  }

  .no-more {
    text-align: center;
    padding: 20px;
    color: #999;
    font-size: 14px;
  }
}

.sidebar-section {
  .sidebar-sticky {
    position: sticky;
    top: 20px;
    max-height: calc(100vh - 60px);
    overflow-y: auto;

    /* 隐藏滚动条 */
    &::-webkit-scrollbar {
      width: 0;
      display: none;
    }
    scrollbar-width: none;
    -ms-overflow-style: none;
  }

  .create-post-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 24px;
    margin-bottom: 20px;
  }

  .sidebar-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
    }

    .hot-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .hot-tag {
        cursor: pointer;
        background: #f0f7ff;
        border-color: #d0e8ff;
        color: #409eff;
        transition: all 0.3s;

        &:hover {
          background: #409eff;
          color: #fff;
        }
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .content-wrapper {
    grid-template-columns: 1fr 280px;
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .social-square-container {
    padding: 12px;
  }

  .content-wrapper {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .sidebar-section {
    position: fixed;
    bottom: 20px;
    right: 20px;
    z-index: 100;

    .sidebar-sticky {
      position: static;
      max-height: none;
    }

    .create-post-btn {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      padding: 0;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);

      .btn-text {
        display: none;
      }

      i {
        margin: 0;
        font-size: 24px;
      }
    }

    .sidebar-card {
      display: none;
    }
  }

  .posts-section .posts-list {
    max-height: calc(100vh - 40px);
  }
}
</style>
