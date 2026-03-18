<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6" :xs="24" class="sidebar-col">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>个人信息</span>
          </div>
          <div>
            <div class="text-center">
              <userAvatar />
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <svg-icon icon-class="user" />用户名称
                <div class="pull-right">{{ user.userName }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="phone" />手机号码
                <div class="pull-right">{{ user.phonenumber }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="email" />用户邮箱
                <div class="pull-right">{{ user.email }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="tree" />所属学院
                <div class="pull-right" v-if="user.dept">{{ user.dept.deptName }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="peoples" />所属角色
                <div class="pull-right">{{ roleGroup }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="date" />创建日期
                <div class="pull-right">{{ user.createTime }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-card>
          <div slot="header" class="clearfix">
            <span>基本资料</span>
          </div>
          <el-tabs v-model="selectedTab">
            <el-tab-pane label="基本资料" name="userinfo">
              <userInfo :user="user" />
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="resetPwd">
              <resetPwd />
            </el-tab-pane>
            <el-tab-pane label="我的帖子" name="myPosts">
              <userPosts />
            </el-tab-pane>
            <el-tab-pane label="我的任务" name="myTasks">
              <userTasks />
            </el-tab-pane>
            <el-tab-pane label="我的纠纷" name="myDisputes">
              <userDisputes />
            </el-tab-pane>
          </el-tabs>
        </el-card>

        <!-- 信誉信息卡片 -->
        <credit-info v-if="user.userId" :user-id="user.userId" style="margin-top: 20px;" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import userAvatar from "./userAvatar"
import userInfo from "./userInfo"
import resetPwd from "./resetPwd"
import userPosts from "./userPosts"
import userTasks from "./userTasks"
import userDisputes from "./userDisputes"
import CreditInfo from "@/components/CreditInfo"
import { getUserProfile } from "@/api/system/user"

export default {
  name: "Profile",
  components: {
    userAvatar,
    userInfo,
    resetPwd,
    userPosts,
    userTasks,
    userDisputes,
    CreditInfo
  },
  data() {
    return {
      user: {},
      roleGroup: {},
      postGroup: {},
      selectedTab: "userinfo"
    }
  },
  created() {
    const activeTab = this.$route.params && this.$route.params.activeTab
    if (activeTab) {
      this.selectedTab = activeTab
    }
    this.getUser()
  },
  methods: {
    getUser() {
      getUserProfile().then(response => {
        this.user = response.data
        this.roleGroup = response.roleGroup
        this.postGroup = response.postGroup
      })
    }
  }
}
</script>

<style scoped>
.sidebar-col {
  position: sticky;
  top: 0;
  max-height: 100vh;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .sidebar-col {
    position: static;
    max-height: none;
    overflow-y: visible;
  }
}
</style>
