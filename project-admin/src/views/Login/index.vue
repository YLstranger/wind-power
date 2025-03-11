<script setup>
import {onMounted, ref,onBeforeUnmount} from "vue";
import {useRouter} from "vue-router";
import {useMenuStore} from "@/stores/menu";
import {useUserStore} from "@/stores/user";
import 'element-plus/theme-chalk/el-message.css';
import {jwtDecode} from "jwt-decode";
import api from "@/apis/api";

const menuStore = useMenuStore()
const userStore = useUserStore()
let loginForm = ref({
  username: "",
  password: "",
  email: "",
  inputCode: ""
});
let initialCode = ref("")
const router = useRouter()
const COUNTDOWN_KEY = 'verify_code_endtime'; // 本地存储键名
const DURATION = 60; // 总倒计时秒数

// 动态添加动画类
let codeAnimation = ref('')

const countdown = ref(0);
const isCountdown = ref(false);
let timer = null;

//获取验证码功能开发
// 1. 页面加载时恢复倒计时状态
onMounted(() => {
  const savedEndTime = localStorage.getItem(COUNTDOWN_KEY);
  if (savedEndTime) {
    const remaining = Math.floor((Number(savedEndTime) - Date.now()) / 1000);
    if (remaining > 0) startCountdown(remaining); // 存在未完成的倒计时则恢复
  }
});

// 2. 组件卸载时清除定时器
onBeforeUnmount(() => {
  if (timer) clearInterval(timer);
});

// 3. 启动倒计时（核心逻辑）
const startCountdown = (initial = DURATION) => {
  isCountdown.value = true;
  countdown.value = initial;
  const endTime = Date.now() + initial * 1000; // 计算倒计时结束时间戳

  // 持久化存储结束时间
  localStorage.setItem(COUNTDOWN_KEY, endTime.toString());

  timer = setInterval(() => {
    const remaining = Math.max(0, Math.floor((endTime - Date.now()) / 1000));

    if (remaining <= 0) {
      clearInterval(timer);
      isCountdown.value = false;
      localStorage.removeItem(COUNTDOWN_KEY); // 倒计时结束清除存储
    } else {
      countdown.value = remaining; // 更新剩余秒数
    }
  }, 1000);
};

// 4. 获取验证码逻辑
const getInitialCode = async () => {
  if (isCountdown.value) return;

  try {
    await api.getCodeApi({ email: loginForm.value.email });
    startCountdown(); // 启动持久化倒计时
  } catch (error) {
    localStorage.removeItem(COUNTDOWN_KEY); // 失败时清除存储
    isCountdown.value = false;
  }
};

//进行登录并且将token存储在localstorage中
const login = async () => {
  if (loginForm.value.username.length == 0 || loginForm.value.password.length == 0) {
    ElMessage.error("账号或密码不能为空")
    return
  } else if (loginForm.value.inputCode !== initialCode.value) {
    ElMessage.error("验证码错误，请重新输入")
    return
  }
  //请求登录接口拿到token
  await userStore.doLogin(loginForm.value).finally(() => {
    loginForm.value = {}
    initialCode.value = ""
  })
  // console.log("我是token" + userStore.token)
  let token = localStorage.getItem('token')
  let userType = jwtDecode(token).userType;
  // 打印出解析后用户类型 0：管理员 1：普通用户
  // console.log("用户类型为：", userType);
  //渲染左侧菜单数据
  menuStore.setMenu(userType)
  router.push('/')
}

const register = () => {
  console.log(router)
  router.push('/register')
}

</script>

<template>
  <el-form :model="loginForm" class="login-container">
    <h3>系统登录</h3>
    <el-form-item>
      <el-input type="input" placeholder="请输入账号" v-model="loginForm.username">
      </el-input>
    </el-form-item>
    <el-form-item>
      <el-input type="password" placeholder="请输入密码" v-model="loginForm.password">
      </el-input>
    </el-form-item>
    <el-form-item>
      <el-input type="input" placeholder="请输入邮箱" v-model="loginForm.email">
      </el-input>
    </el-form-item>
    <el-form-item>
      <!-- // TODO验证码的逻辑需要进行修改-->
      <el-input type="input" placeholder="请输入验证码" v-model="loginForm.inputCode">
      </el-input>
    </el-form-item>
    <div class="codeContainer">
      <!-- 模板部分 -->
      <div
          class="text"
          @click="getInitialCode"
          :class="{ 'disabled': isCountdown }"
          :disabled="isCountdown"
      >
        {{ countdown > 0 ? `${countdown}秒后重试` : '点击获取验证码' }}
      </div>
      <!-- 动态绑定 class -->
      <div class="code" :class="codeAnimation">{{ initialCode }}</div>
    </div>
    <el-form-item>
      <el-button type="primary" class="login-btn" @click="login"> 登录</el-button>
    </el-form-item>
    <el-form-item>
      <div class="register-btn">
        <el-button type="primary" :text="true" class="no-hover" @click="register"> 注册</el-button>
      </div>
    </el-form-item>
  </el-form>
</template>

<style lang="scss" scoped>
.login-container {
  width: 300px;
  background-color: #fff;
  border: 1px solid #eaeaea;
  border-radius: 15px;
  padding: 35px 35px 15px 35px;
  box-shadow: 0 0 25px #cacaca;
  margin: 180px auto;

  .codeContainer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 5px auto 20px;

    .text {
      line-height: 1.6;
      letter-spacing: 0.05em;
      color: #333;
      font-size: 12px;
      width: 30%;
      height: 40%;
      margin: 5px 5px 20px 0px;
      text-align: center;
    }

    .disabled {
      cursor: not-allowed;
      opacity: 0.6;
    }

    .code {
      border: 1px solid #ccc;
      border-radius: 10px;
      box-shadow: #f4f4f4;
      font-family: 'Helvetica Neue', Arial, sans-serif;
      font-size: 16px;
      line-height: 1.6;
      letter-spacing: 0.1em;
      color: #333;
      background-color: #f4f4f4;
      font-size: 20px;
      width: 180px;
      height: 40px;
      margin: 5px 0px 20px 0px;
      text-align: center;
      line-height: 40px;

      &.fade-in {
        animation: fadeIn 1s ease-out;
      }
    }
  }

  h3 {
    font-weight: 700;
    font-size: 20px;
    text-align: center;
    margin-bottom: 20px;
    color: var(--el-color-primary)
  }


  .login-btn {
    width: 100%;
  }

  :deep(.register-btn) {
    display: flex;
    justify-content: flex-start;
    align-items: center;

    .no-hover {
      &:hover {
        color: #409EFF;
        background-color: #fff;
        border-color: #409EFF
      }

      padding: 0px;
    }
  }

  @keyframes fadeIn {
    0% {
      opacity: 0;
      transform: scale(0.9) rotate(0deg);
      background-color: #f4f4f4;
      color: #333;
    }

    25% {
      opacity: 0.5;
      transform: scale(1.1) rotate(5deg);
      background-color: #f0f0f0;
      color: #409EFF;
      /* 文字颜色渐变 */
    }

    50% {
      opacity: 0.7;
      transform: scale(1.2) rotate(-5deg);
      /* 轻微旋转 */
      background-color: #e6f7ff;
      /* 更换背景颜色 */
      color: #409EFF;
      /* 文字颜色渐变 */
    }

    75% {
      opacity: 0.9;
      transform: scale(1.1) rotate(5deg);
      /* 再次旋转 */
      background-color: #e0f7ff;
      color: #409EFF;
    }

    100% {
      opacity: 1;
      transform: scale(1) rotate(0deg);
      /* 还原旋转 */
      background-color: #f4f4f4;
      /* 恢复背景颜色 */
      color: #333;
      /* 恢复文字颜色 */
    }
  }

}
</style>