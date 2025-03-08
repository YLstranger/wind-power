<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/apis/api';
import { ElMessage } from 'element-plus';

let avatarPath = '../../assets/images/avatar.jpg'
let avatar = ref('')
let username = ref('')
let createTime = ref('')
let userDataList = ref([])
let total = ref(250)


let page = ref({
	//每一页容纳的数据条数
	pageSize: 10,
	//当前页码
	pageNumber: 1
})

let ausorToken = {
	authorization: localStorage.getItem('token')
}

let formIsShow = ref(false)

let formData = ref({
	id: '',
	username: '',
	email: '',
	userType: '',
	avatar: '',
})

//分页获取用户数据
const getUserList = async () => {
	let res = await api.getUserListApi(page.value)
	// console.log(res)
	res.records.forEach((item) => {
		let datePart1 = item.createTime.slice(0, 3).join('-')
		let timePart1 = item.createTime.slice(3).join(':')
		item.createTime = `${datePart1} ${timePart1}`

		let datePart2 = item.updateTime.slice(0, 3).join('-')
		let timePart2 = item.updateTime.slice(3).join(':')
		item.updateTime = `${datePart2} ${timePart2}`

		// if (!item.avatar) {
		// 	console.log('没有头像', item.avatar)
		// 	item.avatar = new URL(avatarPath, import.meta.url).href
		// 	console.log(item.avatar)
		// }
	})
	console.log(res)
	userDataList.value = res.records
	total.value = res.total
}

let filterTableData = computed(() =>
	userDataList.value.filter(
		(data) =>
			(!username.value ||
				data.username.toLowerCase().includes(username.value.toLowerCase()))
			&&
			(!createTime.value ||
				data.createTime.toLowerCase().includes(createTime.value.toLowerCase()))
	)
)

const handleEdit = async row => {
	formIsShow.value = true
	//根据用户id查询用户信息并封装
	let res = await api.getUserInfoByIdApi(row.id)
	console.log('编辑用户根据id查询信息为：', res)
	formData.value = res
	formData.value.id = row.id
	console.log('编辑', row)
}



const handleDelete = async (row) => {
	console.log('删除', row.id)
	//删除数据
	await api.deleteUserInfoApi(row.id)

	ElMessage.warning("删除成功！")
	//刷新用户列表
	getUserList()

}

const handleAvatarSuccess = response => {
	console.log('上传成功', response)
	formData.value.avatar = response.data
}

const cancelEdit = () => {
	formIsShow.value = false
	formData.value = {}
}

const submitForm = async () => {
	console.log('提交表单')
	//修改用户信息
	await api.editUserInfoApi(formData.value)

	//刷新用户列表
	getUserList()
	formIsShow.value = false
}

const initAvatar = () => {
	avatar.value = new URL(avatarPath, import.meta.url).href
}


onMounted(() => {
	getUserList()
	initAvatar()
})
</script>
<template>
	<div class="header">
		<div class="item"><el-input v-model="username" style="width: 240px" placeholder="输入用户名进行搜索" /></div>
		<div class="item"><el-input v-model="createTime" style="width: 240px" placeholder="输入创建时间进行搜索" /></div>
	</div>

	<div class="table">
		<el-table :data="filterTableData" style="width: 100%" :border="true" max-height="400px">
			<el-table-column label="id" prop="id" />
			<el-table-column label="用户名" prop="username" />
			<el-table-column label="邮箱" prop="email" />
			<el-table-column label="用户类型" prop="userType" />
			<el-table-column label="用户头像" prop="avatar">
				<template #default="scope">
					<div class="avatar">
						<img v-if="scope.row.avatar" :src="scope.row.avatar" alt="" />
						<img v-else :src="avatar" alt="默认图片" title="默认图片" />
					</div>
				</template>
			</el-table-column>
			<el-table-column label="创建时间" prop="createTime" />
			<el-table-column label="更新时间" prop="updateTime" />
			<el-table-column label="操作">
				<template #default="scope">
					<el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
					<el-popconfirm width="220" :icon="InfoFilled" icon-color="#626AEF" title="您确定删除该用户吗？">
						<template #reference>
							<el-button type="danger" size="small">删除</el-button>
						</template>
						<template #actions="{ confirm, cancel }">
							<el-button size="small" @click="cancel">取消</el-button>
							<el-button type="danger" size="small" @click="confirm, handleDelete(scope.row)">
								确定
							</el-button>
						</template>
					</el-popconfirm>
				</template>
			</el-table-column>
		</el-table>
		<el-dialog v-model="formIsShow" title="编辑用户信息" width="500" align-center>
			<el-form :model="formData" label-width="120px">
				<el-form-item label="用户名">
					<el-input v-model="formData.username" />
				</el-form-item>
				<el-form-item label="邮箱">
					<el-input v-model="formData.email" />
				</el-form-item>
				<el-form-item label="用户类型">
					<el-input v-model="formData.userType" placeholder="请输入管理员或者普通用户" />
				</el-form-item>
				<el-form-item label="上传头像" style="align-items: center;">
					<el-upload action="/api/upload" :headers="ausorToken" :show-file-list="false"
						:on-success="handleAvatarSuccess">
						<img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
						<!-- 头像没有上传的显示 -->
						<div v-else class="avatar-uploader">
							<el-icon class="avatar-uploader-icon">
								<Plus />
							</el-icon>
						</div>
					</el-upload>
				</el-form-item>
			</el-form>
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="cancelEdit">取消编辑</el-button>
					<el-button type="primary" @click="submitForm">
						确定
					</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
	<div style="height: 10px;"></div>
	<el-pagination :page-size="page.pageSize" :pager-count="11" layout="prev, pager, next" :total="total"
		v-model:current-page="page.pageNumber" @current-change="getUserList" />
</template>

<style lang="scss">
.header {
	display: flex;
	align-items: center;
	margin: 0px 0px 20px;

	.item {
		margin: 0px 20px 0px 0px;
	}
}

/* 头像框样式 */
.avatar-uploader {
	width: 100px;
	height: 100px;
	background-color: #f0f0f0;
	border-radius: 50%;
	display: flex;
	justify-content: center;
	align-items: center;
	border: 2px solid #dcdfe6;
	position: relative;
	overflow: hidden;
	cursor: pointer;
	transition: all 0.3s;
}

.avatar-uploader:hover {
	border-color: #d1d1d1;
	/* 浅灰色 */
	box-shadow: 0 0 8px rgba(209, 209, 209, 0.2);
	/* 灰色阴影 */
}

/* 上传图标 */
.avatar-uploader-icon {
	font-size: 36px;
	color: #909399;
}

.avatar {
	width: 100px;
	height: 100px;
	display: flex;
	align-items: center;
	justify-content: center;

	img {
		width: 100%;
		height: 100%;
	}
}

.page {
	margin: 10px 0px 0px 0px;
}
</style>