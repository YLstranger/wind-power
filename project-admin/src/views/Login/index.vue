<script setup>
import { onMounted, ref, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { useMenuStore } from "@/stores/menu";
import { useUserStore } from "@/stores/user";
import 'element-plus/theme-chalk/el-message.css';
import { jwtDecode } from "jwt-decode";
import api from "@/apis/api";

const menuStore = useMenuStore();
const userStore = useUserStore();
const loginForm = ref({
  username: "",
  password: "",
  email: "",
  inputCode: ""
});
const initialCode = ref("");
const router = useRouter();
const COUNTDOWN_KEY = 'verify_code_endtime';
const DURATION = 60;

// 动画控制
const codeAnimation = ref('');
const countdown = ref(0);
const isCountdown = ref(false);
let timer = null;

onMounted(() => {
  const savedEndTime = localStorage.getItem(COUNTDOWN_KEY);
  if (savedEndTime) {
    const remaining = Math.floor((Number(savedEndTime) - Date.now()) / 1000);
    if (remaining > 0) startCountdown(remaining);
  }
});

onBeforeUnmount(() => {
  if (timer) clearInterval(timer);
});

const startCountdown = (initial = DURATION) => {
  isCountdown.value = true;
  countdown.value = initial;
  const endTime = Date.now() + initial * 1000;

  localStorage.setItem(COUNTDOWN_KEY, endTime.toString());

  timer = setInterval(() => {
    const remaining = Math.max(0, Math.floor((endTime - Date.now()) / 1000));
    if (remaining <= 0) {
      clearInterval(timer);
      isCountdown.value = false;
      localStorage.removeItem(COUNTDOWN_KEY);
    } else {
      countdown.value = remaining;
    }
  }, 1000);
};

const getInitialCode = async () => {
  if (!loginForm.value.email) {
    ElMessage.warning("请输入邮箱地址");
    return;
  }
  if (isCountdown.value) return;

  try {
    const res = await api.getCodeApi({ email: loginForm.value.email });
    initialCode.value = res.data.code; // 假设后端返回{ code: "1234" }

    // 触发动画
    codeAnimation.value = 'fade-in';
    setTimeout(() => codeAnimation.value = '', 1000);

    startCountdown();
    ElMessage.success("验证码已发送");
  } catch (error) {
    ElMessage.error("验证码发送失败");
    localStorage.removeItem(COUNTDOWN_KEY);
    isCountdown.value = false;
  }
};

const login = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.error("账号或密码不能为空");
    return;
  }
  if (!loginForm.value.inputCode) {
    ElMessage.error("请输入验证码");
    return;
  }

  try {
    await userStore.doLogin(loginForm.value);
    const token = localStorage.getItem('token');
    const userType = jwtDecode(token).userType;
    menuStore.setMenu(userType);
    router.push('/');
  } finally {
    loginForm.value.password = "";
    loginForm.value.inputCode = "";
    initialCode.value = "";
  }
};

const register = () => {
  router.push('/register');
};
</script>

<template>
  <el-form :model="loginForm" class="login-container">
    <h3>系统登录</h3>
    <el-form-item>
      <el-input v-model="loginForm.username" placeholder="请输入账号"/>
    </el-form-item>
    <el-form-item>
      <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password/>
    </el-form-item>
    <el-form-item>
      <el-input v-model="loginForm.email" placeholder="请输入邮箱" type="email"/>
    </el-form-item>
    <el-form-item>
      <div class="code-group">
        <el-input v-model="loginForm.inputCode" placeholder="请输入验证码"/>
        <div class="code-wrapper">
          <div
              class="code-trigger"
              :class="{ disabled: isCountdown }"
              @click="getInitialCode"
          >
            {{ countdown > 0 ? `${countdown}秒后重试` : '获取验证码' }}
          </div>
          <transition name="fade">
            <div class="code-display" :class="codeAnimation">
              {{ initialCode || '等待验证码...' }}
            </div>
          </transition>
        </div>
      </div>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" class="login-btn" @click="login">登录</el-button>
    </el-form-item>
    <el-form-item>
      <el-button type="text" class="register-btn" @click="register">立即注册</el-button>
    </el-form-item>
  </el-form>
</template>

<style lang="scss" scoped>
.login-container {
  width: 380px;
  margin: 100px auto;
  padding: 30px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);

  h3 {
    text-align: center;
    margin-bottom: 30px;
    color: #2c3e50;
    font-size: 24px;
  }

  .code-group {
    position: relative;
    width: 100%;

    .code-wrapper {
      display: flex;
      gap: 10px;
      margin-top: 10px;
    }

    .code-trigger {
      flex: 1;
      height: 40px;
      line-height: 40px;
      text-align: center;
      background: #409EFF;
      color: white;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: #66b1ff;
      }

      &.disabled {
        background: #c0c4cc;
        cursor: not-allowed;
      }
    }

    .code-display {
      flex: 1;
      height: 40px;
      line-height: 40px;
      text-align: center;
      background: #f0f9ff;
      border: 1px solid #409EFF;
      border-radius: 6px;
      font-weight: bold;
      color: #409EFF;
      letter-spacing: 2px;

      &.fade-in {
        animation: fadeIn 0.8s ease-out;
      }
    }
  }

  .login-btn {
    width: 100%;
    height: 45px;
    font-size: 16px;
  }

  .register-btn {
    width: 100%;
    text-align: center;
    color: #409EFF;
  }

  @keyframes fadeIn {
    0% {
      opacity: 0;
      transform: translateY(-10px);
    }
    50% {
      opacity: 1;
      transform: translateY(0);
    }
    100% {
      opacity: 1;
    }
  }
}
</style>
