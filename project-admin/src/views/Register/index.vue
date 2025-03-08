<script setup>
import { ref } from "vue";
import 'element-plus/theme-chalk/el-message.css';
import api from "@/apis/api";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

let registerForm = ref({
	username: "",
	password: "",
	email: ""
});

let initialCode = ref("")
let code = ref("")
let againPassword = ref("")

let router = useRouter()

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

//编写注册逻辑
const register = async () => {
	if (registerForm.value.username.length == 0 || registerForm.value.password.length == 0) {
		ElMessage.error("账号或密码不能为空")
		getInitialCode()
		return
	} else if (registerForm.value.email.length == 0) {
		ElMessage.error("邮箱不能为空")
		getInitialCode()
		return
	} else if (registerForm.value.password !== againPassword.value) {
		ElMessage.error("两次输入的密码不一致，请重新输入")
		getInitialCode()
		return
	}
	else if (code.value !== initialCode.value) {
		ElMessage.error("验证码错误，请重新输入")
		getInitialCode()
		return
	}
	await api.registerApi(registerForm.value)
	ElMessage.success("注册成功")
	router.push('/login')
}



</script>

<template>
	<el-form :model="registerForm" class="login-container">
		<h3>用户注册</h3>
		<el-form-item>
			<el-input type="input" placeholder="请输入账号" v-model="registerForm.username">
			</el-input>
		</el-form-item>
		<el-form-item>
			<el-input type="input" placeholder="请输入邮箱" v-model="registerForm.email">
			</el-input>
		</el-form-item>
		<el-form-item>
			<el-input type="password" placeholder="请输入密码" v-model="registerForm.password">
			</el-input>
		</el-form-item>
		<el-form-item>
			<el-input type="password" placeholder="请再次输入密码" v-model="againPassword">
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
			<el-button type="primary" class="register-btn" @click="register"> 注册 </el-button>
		</el-form-item>
		<el-form-item>
			<div class="login-btn">
				<router-link to="/login">
					<el-button type="primary" :text="true" class="no-hover">
						登录
					</el-button>
				</router-link>
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
	margin: 100px auto 20px;

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



	.register-btn {
		width: 100%;
	}

	:deep(.login-btn) {
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