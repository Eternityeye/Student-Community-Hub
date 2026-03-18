<template>
  <div class="user-posts">
    <!-- 空状态 -->
    <div v-if="!loading && postList.length === 0" class="empty-state">
      <el-empty
        description="还没有发布过帖子"
        :image-size="100"
      ></el-empty>
    </div>

    <!-- 帖子列表 -->
    <div v-else class="posts-container">
      <post-card
        v-for="post in postList"
        :key="post.postId"
        :post="post"
        :show-delete="true"
        @post-deleted="removePost"
        @like-changed="handleLikeChanged"
        @comment-added="handleCommentAdded"
      />
    </div>

    <!-- 加载更多按钮 -->
    <div v-if="postList.length > 0 && hasMore" class="load-more">
      <el-button
        :loading="loading"
        @click="loadMorePosts"
      >
        加载更多
      </el-button>
    </div>
  </div>
</template>

<script>
import PostCard from '@/views/social-square/components/PostCard.vue'
import { listMyPosts } from '@/api/social/post'

export default {
  name: 'UserPosts',
  components: {
    PostCard
  },
  data() {
    return {
      postList: [],
      pageNum: 1,
      pageSize: 5,
      loading: false,
      hasMore: true
    }
  },
  created() {
    this.loadPosts()
  },
  methods: {
    loadPosts() {
      this.pageNum = 1
      this.postList = []
      this.loadMorePosts()
    },
    loadMorePosts() {
      if (this.loading) return

      this.loading = true
      listMyPosts({
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }).then(response => {
        if (response.rows && response.rows.length > 0) {
          this.postList.push(...response.rows)
          this.pageNum++
          // 判断是否还有更多数据
          this.hasMore = response.rows.length === this.pageSize
        } else {
          this.hasMore = false
        }
      }).catch(() => {
        this.$message.error('加载帖子失败')
      }).finally(() => {
        this.loading = false
      })
    },
    removePost(postId) {
      // 从列表中移除删除的帖子
      this.postList = this.postList.filter(post => post.postId !== postId)
    },
    handleLikeChanged({ postId, isLiked }) {
      // 更新帖子的点赞状态
      const post = this.postList.find(p => p.postId === postId)
      if (post) {
        post.isLiked = isLiked
        if (isLiked) {
          post.likeCount++
        } else {
          post.likeCount--
        }
      }
    },
    handleCommentAdded(postId) {
      // 评论已添加，刷新该帖子的评论数
      const post = this.postList.find(p => p.postId === postId)
      if (post) {
        post.commentCount++
      }
    }
  }
}
</script>

<style scoped lang="scss">
.user-posts {
  padding: 20px 0;

  .empty-state {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 300px;
  }

  .posts-container {
    .post-card {
      margin-bottom: 16px;
    }
  }

  .load-more {
    display: flex;
    justify-content: center;
    padding: 20px 0;

    button {
      padding: 8px 30px;
    }
  }
}
</style>
