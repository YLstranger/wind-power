<script setup>
import { ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter()
let avatarPath = '../../../assets/images/avatar.jpg'
let avatar = ref('')

//获取初始头像路径
const getImageUrl = () => {
	// 使用 URL 构造函数来解析路径
	if (localStorage.getItem('avatar')) {
		avatar.value = localStorage.getItem('avatar')
	} else {
		avatar.value = new URL(avatarPath, import.meta.url).href
	}
}

let ausorToken = {
	Authorization: localStorage.getItem('token')
}

const handleAvatarSuccess = response => {
	// console.log('响应结果为：', response)
	// console.log('初始头像路径为：', avatar.value)
	avatar.value = response.data
	ElMessage.success('更换头像成功')
	localStorage.setItem('avatar', avatar.value)
	// console.log('头像路径为：', avatar.value)
}



const logOut = () => {
	localStorage.removeItem('token')
	localStorage.removeItem('menu')
	localStorage.removeItem('avatar')
	router.push('/login')
}

onMounted(() => {
	getImageUrl()
})

</script>

<template>
	<div>风电预测平台</div>
	<el-dropdown>
		<span class="el-dropdown-link">
			<div class="avatar">
				<img :src="avatar" title="头像" alt="">
			</div>
		</span>
		<template #dropdown>
			<el-dropdown-menu>
				<el-dropdown-item>
					<el-upload action="/api/upload" :headers="ausorToken" :show-file-list="false"
						:on-success="handleAvatarSuccess">
						更换头像
					</el-upload>
				</el-dropdown-item>
				<el-dropdown-item @click="logOut">
					退出登录
				</el-dropdown-item>
			</el-dropdown-menu>
		</template>
	</el-dropdown>

</template>

<style lang="scss" scoped>
.el-dropdown-link {
	cursor: pointer;
	color: var(--el-color-primary);
	display: flex;
	align-items: center;
}

:deep(.el-tooltip__trigger:focus-visible) {
	outline: unset;
}

.avatar {
	width: 40px;
	height: 40px;
	border-radius: 50%;
	overflow: hidden;

	img {
		width: 100%;
		height: 100%;
	}
}
</style>