<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { useMenuStore } from "@/stores/menu";
import { useUserStore } from "@/stores/user";
import 'element-plus/theme-chalk/el-message.css';
import { jwtDecode } from "jwt-decode";
import api from "@/apis/api";

const menuStore = useMenuStore()
const userStore = useUserStore()
let loginForm = ref({
	username: "",
	password: "",
});
let initialCode = ref("")
let code = ref("")
const router = useRouter()

// 动态添加动画类
let codeAnimation = ref('')

//获取验证码
const getInitialCode = async () => {
	initialCode.value = await api.getCodeApi()
	// 添加动画类来触发动画
	codeAnimation.value = 'fade-in';
	// 在动画结束后移除动画类
	setTimeout(() => {
		codeAnimation.value = '';
	}, 1000); // 动画持续时间为 1 秒
}

//进行登录并且将token存储在localstorage中
const login = async () => {
	if (loginForm.value.username.length == 0 || loginForm.value.password.length == 0) {
		ElMessage.error("账号或密码不能为空")
		return
	} else if (code.value !== initialCode.value) {
		ElMessage.error("验证码错误，请重新输入")
		getInitialCode()
		return
	}
	//请求登录接口拿到token
	await userStore.doLogin(loginForm.value).finally(() => {
		loginForm.value = {}
		code.value = ""
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
			<el-input type="input" placeholder="请输入验证码" v-model="code">
			</el-input>
		</el-form-item>
		<div class="codeContainer">
			<div class="text" @click="getInitialCode">点击获取验证码</div>
			<!-- 动态绑定 class -->
			<div class="code" :class="codeAnimation">{{ initialCode }}</div>
		</div>
		<el-form-item>
			<el-button type="primary" class="login-btn" @click="login"> 登录 </el-button>
		</el-form-item>
		<el-form-item>
			<div class="register-btn">
				<el-button type="primary" :text="true" class="no-hover" @click="register"> 注册 </el-button>
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