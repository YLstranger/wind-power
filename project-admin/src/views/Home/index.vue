<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/apis/api';
import axios from 'axios';
import { ElMessage } from 'element-plus';
// import 'element-plus/dist/index.css'
const wind_machine = ref('')
const createTime = ref('')
let total = ref(0)
let tableData = ref([])

let ausorToken = {
	Authorization: localStorage.getItem('token')
}

let page = ref({
	//每一页容纳的数据条数
	pageSize: 10,
	//当前页码
	pageNumber: 1
})


const handleDelete = async row => {
	// console.log("行数据id为:", row.id)
	await api.deleteMachineInfoApi(row.id)
	ElMessage.warning('删除成功')
	getTableData()
}

// 将文件流转为 Blob 并触发下载
function downloadExcelFile (fileData) {
	const blob = new Blob([fileData], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
	const url = URL.createObjectURL(blob);

	// 创建一个链接并模拟点击
	const link = document.createElement('a');
	link.href = url;
	link.download = '风机数据.xlsx';  // 这里可以根据需要自定义文件名
	document.body.appendChild(link);
	link.click();

	// 清理
	document.body.removeChild(link);
	URL.revokeObjectURL(url);  // 释放对象 URL
}

const handleExport = row => {
	// console.log("导出数据的风机id为：", row.id)
	axios.get('/api/machine/export', {
		headers: {
			Authorization: localStorage.getItem('token'),
			machineId: row.id  // 将 token 添加到请求头中
		},
		responseType: 'arraybuffer' // 让 axios 获取文件流
	})
		.then((response) => {
			// 处理成功，获取后端返回的 Excel 文件流
			const fileData = response.data;

			// 将文件流保存为 Excel 文件并触发浏览器下载
			downloadExcelFile(fileData);
		})
		.catch((error) => {
			console.error('下载 Excel 文件失败:', error);
			// 在这里可以提示用户下载失败
		});
}

const getTableData = async () => {
	let res = await api.getMachineListApi(page.value)
	// console.log('获取数据', res.records)
	res.records.forEach((item) => {
		item.createTime = item.createTime.slice(0, 3).join('-')
	})
	tableData.value = res.records
	total.value = res.total
}

let filterTableData = computed(() =>
	tableData.value.filter(
		(data) =>
			(!wind_machine.value ||
				data.name.toLowerCase().includes(wind_machine.value.toLowerCase()))
			&&
			(!createTime.value ||
				data.createTime.toLowerCase().includes(createTime.value.toLowerCase()))
	)
)

const handleFileSuccess = () => {
	ElMessage({
		type: 'success',
		message: '上传成功',
	})
	getTableData()
}

onMounted(() => {
	getTableData()
})

</script>
<template>
	<div class="header">
		<div class="item">
			<el-upload class="upload-demo" action="/api/machine/add" :headers="ausorToken" :show-file-list="false"
				:on-success="handleFileSuccess">
				<el-button type="primary">添加风机数据</el-button>
			</el-upload>
		</div>
		<div class="item"><el-input v-model="wind_machine" style="width: 240px" placeholder="输入风机名称进行搜索" /></div>
		<div class="item"><el-input v-model="createTime" style="width: 240px" placeholder="输入创建时间进行搜索" /></div>
	</div>
	<div class="table">
		<el-table :data="filterTableData" style="width: 100%" :border="true" max-height="400px">
			<el-table-column label="序号" prop="id" />
			<el-table-column label="风机编号" prop="serialNumber" />
			<el-table-column label="风机名称" prop="name" />
			<el-table-column label="创建时间" prop="createTime" />
			<el-table-column label="操作">
				<template #default="scope">
					<el-button size="small" @click="handleExport(scope.row)">
						导出数据
					</el-button>
					<el-popconfirm width="220" :icon="InfoFilled" icon-color="#626AEF" title="您确定删除该风机吗？">
						<template #reference>
							<el-button size="small" type="danger">
								删除风机
							</el-button>
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
		<div style="height: 10px;"></div>
		<el-pagination :page-size="page.pageSize" :pager-count="11" layout="prev, pager, next" :total="total"
			v-model:current-page="page.pageNumber" @current-change="getTableData" />
	</div>

</template>

<style lang="scss">
.header {
	display: flex;
	align-items: center;
	margin: 0px 0px 30px;

	.item {
		margin: 0px 20px 0px 0px;

		.upload-demo {
			height: 32px;
		}
	}
}

.page {
	margin: 20px 0px 0px 0px;
}
</style>