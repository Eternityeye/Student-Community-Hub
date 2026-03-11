<template>
  <div class="container" :class="{'sign-up-mode': isSignUpMode}">
    <div class="forms-container">
      <div class="signin-signup">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="sign-in-form">
          <h2 class="title">登 录</h2>
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" type="text" auto-complete="off" placeholder="账号">
              <svg-icon slot="prefix" icon-class="user" class="input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" auto-complete="off" placeholder="密码" @keyup.enter.native="handleLogin">
              <svg-icon slot="prefix" icon-class="password" class="input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input v-model="loginForm.code" auto-complete="off" placeholder="验证码" style="width:63%" @keyup.enter.native="handleLogin">
              <svg-icon slot="prefix" icon-class="validCode" class="input-icon" />
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img" />
            </div>
          </el-form-item>
          <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住密码</el-checkbox>
          <el-button :loading="loading" class="btn solid" @click.native.prevent="handleLogin">
            <span v-if="!loading">登 录</span>
            <span v-else>登 录 中...</span>
          </el-button>
        </el-form>

        <el-form ref="registerForm" :model="registerForm" class="sign-up-form">
          <h2 class="title">注 册</h2>
          <el-form-item prop="studentId">
            <el-input v-model="registerForm.studentId" type="text" auto-complete="off" placeholder="学号">
              <svg-icon slot="prefix" icon-class="user" class="input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="name">
            <el-input v-model="registerForm.name" type="text" auto-complete="off" placeholder="姓名">
              <svg-icon slot="prefix" icon-class="user" class="input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="registerForm.password" type="password" auto-complete="off" placeholder="密码">
              <svg-icon slot="prefix" icon-class="password" class="input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="registerForm.confirmPassword" type="password" auto-complete="off" placeholder="确认密码" @keyup.enter.native="handleRegister">
              <svg-icon slot="prefix" icon-class="password" class="input-icon" />
            </el-input>
          </el-form-item>
          <el-button :loading="registerLoading" class="btn solid" @click.native.prevent="handleRegister">
            <span v-if="!registerLoading">注 册</span>
            <span v-else>注 册 中...</span>
          </el-button>
        </el-form>
      </div>
    </div>

    <div class="panels-container">
      <div class="panel left-panel">
        <div class="content">
          <h3>新用户?</h3>
          <p>好兄弟,你来了,我们的网站就差你的加入了,点击下方注册按钮加入我们吧!!</p>
          <button class="btn transparent" @click="toggleMode">注 册</button>
        </div>
        <img src="@/assets/svg/2.svg" class="image" alt="" />
      </div>
      <div class="panel right-panel">
        <div class="content">
          <h3>已经是我们自己人了吗?</h3>
          <p>那好兄弟,你直接点击登录按钮,登录到我们这优秀的系统里!!</p>
          <button class="btn transparent" @click="toggleMode">登 录</button>
        </div>
        <img src="@/assets/svg/1.svg" class="image" alt="" />
      </div>
    </div>

    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'

export default {
  name: "Login",
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      isSignUpMode: false,
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      registerForm: {
        studentId: "",
        name: "",
        password: "",
        confirmPassword: ""
      },
      registerRules: {
        studentId: [
          { required: true, message: "请输入学号" },
          { pattern: /^\d{8,12}$/, message: "学号应为8到12位数字" }
        ],
        name: [
          { required: true, message: "请输入姓名" },
          { min: 2, max: 20, message: "姓名长度在 2 到 20 个字符" }
        ],
        password: [
          { required: true, message: "请输入密码" },
          { min: 6, max: 20, message: "密码长度在 6 到 20 个字符" }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword }
        ]
      },
      loading: false,
      registerLoading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    toggleMode() {
      this.isSignUpMode = !this.isSignUpMode
    },
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    },
    handleRegister() {
      const { studentId, name, password, confirmPassword } = this.registerForm
      if (!studentId) {
        this.$message.error('请输入学号')
        return
      }
      if (!/^\d{8,12}$/.test(studentId)) {
        this.$message.error('学号应为8到12位数字')
        return
      }
      if (!name) {
        this.$message.error('请输入姓名')
        return
      }
      if (name.length < 2 || name.length > 20) {
        this.$message.error('姓名长度在 2 到 20 个字符')
        return
      }
      if (!password) {
        this.$message.error('请输入密码')
        return
      }
      if (password.length < 6 || password.length > 20) {
        this.$message.error('密码长度在 6 到 20 个字符')
        return
      }
      if (!confirmPassword) {
        this.$message.error('请再次输入密码')
        return
      }
      if (confirmPassword !== password) {
        this.$message.error('两次输入密码不一致')
        return
      }
      this.registerLoading = true
      // TODO: 调用注册接口
      setTimeout(() => {
        this.$message.success('注册功能开发中，请稍后再试')
        this.registerLoading = false
      }, 1000)
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  position: relative;
  width: 100%;
  background-color: #fff;
  min-height: 100vh;
  overflow: hidden;
  font-family: "Poppins", "PingFang SC", sans-serif;

  &::before {
    content: "";
    position: absolute;
    height: 2000px;
    width: 2000px;
    top: -10%;
    right: 48%;
    transform: translateY(-50%);
    background: linear-gradient(to right, #90c8f8, #1c83e0);
    transition: 1.8s ease-in-out;
    border-radius: 50%;
    z-index: 6;
  }
}

.forms-container {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
}

.signin-signup {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  left: 75%;
  width: 50%;
  transition: 1s 0.7s ease-in-out;
  display: grid;
  grid-template-columns: 1fr;
  z-index: 5;
}

.sign-in-form {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 5rem;
  overflow: hidden;
  grid-column: 1 / 2;
  grid-row: 1 / 2;
  transition: all 0.2s 0.7s;
  z-index: 2;

  .title {
    font-size: 2.2rem;
    color: #444;
    margin-bottom: 10px;
    font-weight: 700;
  }

  ::v-deep .el-form-item {
    width: 100%;
    max-width: 380px;
    margin-bottom: 4px;

    .el-form-item__content {
      line-height: normal;
    }

    .el-form-item__error {
      padding-left: 15px;
    }
  }

  ::v-deep .el-input {
    .el-input__inner {
      background-color: #f0f0f0;
      border: none;
      border-radius: 55px;
      height: 55px;
      padding-left: 50px;
      font-size: 1rem;
      font-weight: 600;
      color: #333;
      box-shadow: none;

      &::placeholder {
        color: #aaa;
        font-weight: 500;
      }

      &:focus {
        box-shadow: none;
        border: none;
        background-color: #e8e8e8;
      }
    }

    .el-input__prefix {
      left: 15px;
      display: flex;
      align-items: center;
      color: #acacac;
    }
  }

  .input-icon {
    font-size: 1.1rem;
    color: #acacac;
    width: 18px;
    height: 18px;
  }

  .remember-me {
    align-self: center;
    margin: 15px 0 10px 0;
    color: #666;
    font-size: 0.9rem;
    clear: both;
    width: 100%;
    max-width: 380px;
  }
}

.sign-up-form {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 5rem;
  overflow: hidden;
  grid-column: 1 / 2;
  grid-row: 1 / 2;
  transition: all 0.2s 0.7s;
  opacity: 0;
  z-index: 1;

  .title {
    font-size: 2.2rem;
    color: #444;
    margin-bottom: 10px;
    font-weight: 700;
  }

  ::v-deep .el-form-item {
    width: 100%;
    max-width: 380px;
    margin-bottom: 4px;

    .el-form-item__content {
      line-height: normal;
    }

    .el-form-item__error {
      padding-left: 15px;
    }
  }

  ::v-deep .el-input {
    .el-input__inner {
      background-color: #f0f0f0;
      border: none;
      border-radius: 55px;
      height: 55px;
      padding-left: 50px;
      font-size: 1rem;
      font-weight: 600;
      color: #333;
      box-shadow: none;

      &::placeholder {
        color: #aaa;
        font-weight: 500;
      }

      &:focus {
        box-shadow: none;
        border: none;
        background-color: #e8e8e8;
      }
    }

    .el-input__prefix {
      left: 15px;
      display: flex;
      align-items: center;
      color: #acacac;
    }
  }

  .input-icon {
    font-size: 1.1rem;
    color: #acacac;
    width: 18px;
    height: 18px;
  }
}

.login-code {
  width: 33%;
  height: 55px;
  float: right;
  display: flex;
  align-items: center;
}

.login-code-img {
  height: 40px;
  border-radius: 8px;
  cursor: pointer;
  transition: opacity 0.3s;

  &:hover {
    opacity: 0.8;
  }
}

.btn {
  width: 150px;
  background: linear-gradient(to right, #90c8f8, #1c83e0);
  border: none;
  outline: none;
  height: 49px;
  border-radius: 49px;
  color: #fff;
  text-transform: uppercase;
  font-weight: 600;
  margin: 10px 0;
  cursor: pointer;
  transition: 0.5s;
  font-size: 1rem;
  letter-spacing: 2px;

  &:hover {
    background: linear-gradient(to right, #1c83e0, #90c8f8);
    box-shadow: 0 4px 15px rgba(28, 131, 224, 0.4);
  }

  &.transparent {
    background: none;
    border: 2px solid #fff;
    width: 130px;
    height: 41px;
    font-size: 0.8rem;
    color: #fff;
    border-radius: 41px;
    margin: 0;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
      box-shadow: none;
    }
  }
}

.panels-container {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
}

.panel {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-around;
  text-align: center;
  z-index: 6;
}

.left-panel {
  pointer-events: all;
  padding: 3rem 17% 2rem 12%;
}

.right-panel {
  pointer-events: none;
  padding: 3rem 12% 2rem 17%;
}

.right-panel .image,
.right-panel .content {
  transform: translateX(2000px);
}

.panel .content {
  color: #fff;
  transition: transform 0.9s ease-in-out;
  transition-delay: 0.6s;
}

.panel h3 {
  font-weight: 600;
  line-height: 1;
  font-size: 1.5rem;
}

.panel p {
  font-size: 0.95rem;
  padding: 0.7rem 0;
}

.image {
  width: 100%;
  transition: transform 1.1s ease-in-out;
  transition-delay: 0.4s;
}

.el-login-footer {
  height: 50px;
  line-height: 50px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: rgba(255, 255, 255, 0.9);
  font-family: Arial, sans-serif;
  font-size: 13px;
  letter-spacing: 1px;
  z-index: 10;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

/* 切换到注册模式的动画 */
.container.sign-up-mode::before {
  transform: translate(100%, -50%);
  right: 52%;
}

.container.sign-up-mode .left-panel .image,
.container.sign-up-mode .left-panel .content {
  transform: translateX(-2000px);
}

.container.sign-up-mode .signin-signup {
  left: 25%;
}

.container.sign-up-mode .sign-up-form {
  opacity: 1;
  z-index: 2;
}

.container.sign-up-mode .sign-in-form {
  opacity: 0;
  z-index: 1;
}

.container.sign-up-mode .right-panel .image,
.container.sign-up-mode .right-panel .content {
  transform: translateX(0%);
}

.container.sign-up-mode .left-panel {
  pointer-events: none;
}

.container.sign-up-mode .right-panel {
  pointer-events: all;
}

@media (max-width: 870px) {
  .signin-signup {
    width: 100%;
    top: 95%;
    transform: translate(-50%, -100%);
    left: 50%;
    transition: 1s 0.8s ease-in-out;
  }

  .signin-signup,
  .container.sign-up-mode .signin-signup {
    left: 50%;
  }

  .panels-container {
    grid-template-columns: 1fr;
    grid-template-rows: 1fr 2fr 1fr;
  }

  .panel {
    flex-direction: row;
    justify-content: space-around;
    align-items: center;
    padding: 2.5rem 8%;
    grid-column: 1 / 2;
  }

  .right-panel {
    grid-row: 3 / 4;
  }

  .left-panel {
    grid-row: 1 / 2;
  }

  .left-panel .image,
  .left-panel .content {
    transform: translateY(0px);
  }

  .image {
    width: 200px;
    transition: transform 0.9s ease-in-out;
    transition-delay: 0.6s;
  }

  .panel .content {
    padding-right: 15%;
    transition: transform 0.9s ease-in-out;
    transition-delay: 0.8s;
  }

  .container::before {
    width: 2000px;
    height: 2000px;
    transform: translateX(-50%);
    left: 50%;
    bottom: 60%;
    right: initial;
    top: initial;
    transition: 2s ease-in-out;
  }

  .container.sign-up-mode::before {
    transform: translate(-50%, 100%);
    bottom: 32%;
    right: initial;
  }

  .container.sign-up-mode .left-panel .image,
  .container.sign-up-mode .left-panel .content {
    transform: translateY(-300px);
  }

  .container.sign-up-mode .right-panel .image,
  .container.sign-up-mode .right-panel .content {
    transform: translateY(0px);
  }

  .right-panel .image,
  .right-panel .content {
    transform: translateY(300px);
  }

  .container.sign-up-mode .signin-signup {
    top: 5%;
    transform: translate(-50%, 0);
  }

  .sign-in-form {
    padding: 0 1.5rem;
  }

  .sign-up-form {
    padding: 0 1.5rem;
  }
}

@media (max-width: 570px) {
  .image {
    display: none;
  }

  .panel .content {
    padding: 0.5rem 1rem;
  }

  .container::before {
    width: 2000px;
    height: 2000px;
    bottom: 63%;
    left: 50%;
  }

  .container.sign-up-mode::before {
    bottom: 35%;
    left: 50%;
  }
}
</style>
