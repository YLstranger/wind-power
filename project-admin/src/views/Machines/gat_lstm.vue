<script setup>
import { onMounted, ref } from 'vue';
import api from '@/apis/api';
import { getCurrentInstance } from 'vue';
let begin = ref('')
let end = ref('')
let wpId = ref()
let timeInterval = ref()
const chart = ref(null)
const chartPredicted = ref(null)
let options = ref([])
let dateTimeArray = ref([])
let powerArray = ref([])
let predictedDateTimeArray = ref([])
let predictedPowerArray = ref([])
let modelName = ref('GAT_LSTM')
let intervalOptions = ref([{
	label: "未来三个小时",
	value: 3
}, {
	label: "未来六个小时",
	value: 6
}, {
	label: "未来九个小时",
	value: 9
}, {
	label: "未来十二个小时",
	value: 12
}
])
const { proxy } = getCurrentInstance()

//获取风机列表
const getOptions = async () => {
	const res = await api.getMachineListApi(
		{
			pageNumber: 1,
			pageSize: 100
		}
	)
	// console.log(res)
	let idArray = res.records.map(item => item.id)
	let nameArray = res.records.map(item => item.name)
	for (let i = 0; i < idArray.length; i++) {
		let optionObject = {
			value: '',
			label: ''
		}
		optionObject.value = idArray[i]
		optionObject.label = nameArray[i]
		options.value.push(optionObject)
	}
}


const getEchartsData = async () => {
	dateTimeArray.value = []
	powerArray.value = []
	const res = await api.getMachineDataByIdApi({
		//模型名字，风机id，开始时间，结束时间，时间间隔
		modelName: modelName.value,
		machineId: wpId.value,
		beginTime: begin.value,
		endTime: end.value,
		timeInterval: timeInterval.value
	});
	console.log("原始数据和预测数据为：", res)
	//拿到的res应该是一个对象，{[原始数据],[预测数据]}
	if (Array.isArray(res.originalData)) {
		res.originalData.forEach(item => {
			dateTimeArray.value.push(item.recordTime)
			// powerArray.value.push(Math.round(item.power * 1000))
			powerArray.value.push(item.power * 1000)
		})
		// intervalComputed(dateTimeArray.value.length)
		console.log(dateTimeArray.value, powerArray.value)
	} else {
		console.error('API response is not an array:', res);
	}

	if (Array.isArray(res.predictData)) {
		res.predictData.forEach(item => {
			predictedDateTimeArray.value.push(item.recordTime)
			// powerArray.value.push(Math.round(item.power * 1000))
			predictedPowerArray.value.push(item.power * 1000)
		})
		// intervalComputed(dateTimeArray.value.length)
		console.log(predictedDateTimeArray.value, predictedPowerArray.value)
	} else {
		console.error('API response is not an array:', res);
	}
}


const initEcharts = async () => {
	await getEchartsData()
	var myChart = proxy.$echarts.init(chart.value)
	let option = {
		tooltip: {
			trigger: 'axis'
		},
		legend: {
			data: ['wpPower']
		},
		grid: {
			left: '3%',
			right: '4%',
			bottom: '5%',
			containLabel: true
		},
		toolbox: {
			feature: {
				saveAsImage: {}
			}
		},
		xAxis: {
			type: 'category',
			boundaryGap: false,
			data: dateTimeArray.value.map(function (str) {
				return str.replace(' ', '\n');
			}),
			axisLabel: {
				//将x轴划分成18个点
				interval: Math.floor(dateTimeArray.value.length / 4),
				align: 'center',
			},
		},
		yAxis: {
			type: 'value'
		},
		series: [
			{
				name: 'wpPower',
				type: 'line',
				stack: 'Total',
				data: powerArray.value
			}
		]
	};
	myChart.setOption(option)

	var myChartPredicted = proxy.$echarts.init(chartPredicted.value)
	let optionPredicted = {
		tooltip: {
			trigger: 'axis'
		},
		legend: {
			data: ['wpPowerPredicted']
		},
		grid: {
			left: '3%',
			right: '4%',
			bottom: '5%',
			containLabel: true
		},
		toolbox: {
			feature: {
				saveAsImage: {}
			}
		},
		xAxis: {
			type: 'category',
			boundaryGap: false,
			data: predictedDateTimeArray.value.map(function (str) {
				return str.replace(' ', '\n');
			}),
			axisLabel: {
				//将x轴划分成18个点
				interval: Math.floor(predictedDateTimeArray.value.length / 4),
				align: 'center',
			},
		},
		yAxis: {
			type: 'value'
		},
		series: [
			{
				name: 'wpPowerPredicted',
				type: 'line',
				stack: 'Total',
				data: predictedPowerArray.value
			}
		]
	};
	myChartPredicted.setOption(optionPredicted)
}

onMounted(() => {
	getOptions()
})
</script>

<template>
	<div class="text">{{ modelName }}集成模型预测</div>
	<div class="content">
		<div class="select">
			<el-select v-model="wpId" placeholder="风机" style="width: 240px">
				<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
			</el-select>
		</div>
		<!-- <div>{{ wpId }}</div> -->
		<div class="block">
			<el-date-picker v-model="begin" type="datetime" placeholder="预测起始日期" format="YYYY-MM-DD HH:mm:ss"
				value-format="YYYY-MM-DD HH:mm:ss" />
		</div>
		<div class="block">
			<el-date-picker v-model="end" type="datetime" placeholder="预测结束日期" format="YYYY-MM-DD HH:mm:ss"
				value-format="YYYY-MM-DD HH:mm:ss" />
		</div>
		<div class="select">
			<el-select v-model="timeInterval" placeholder="选择预测时间段" style="width: 200px">
				<el-option v-for="item in intervalOptions" :key="item.value" :label="item.label" :value="item.value" />
			</el-select>
		</div>
	</div>
	<div class="footer">
		<el-button type="primary" style="margin-top: 5px;" @click="initEcharts">预测</el-button>
	</div>
	<div style="height: 20px;"></div>
	<div class="box" style="height: 100%; display: flex; justify-content: space-between; align-items: center;">
		<div ref="chart" style="width: 43%; height: 50%;"></div>
		<div ref="chartPredicted" style="width: 43%; height: 50%;"></div>
	</div>
</template>

<style lang="scss">
.text {
	font-size: 20px;
}

.content {
	display: flex;


	.select {
		margin-right: 20px;
	}

	.block {
		margin-right: 20px;
	}
}
</style>