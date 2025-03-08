<script setup>
let menuList = JSON.parse(localStorage.getItem('menu'))


const hasChildren = () => {
	return menuList.filter(item => item.children)
}

const noChildren = () => {
	return menuList.filter(item => !item.children)
}


</script>

<template>
	<el-menu active-text-color="#ffd04b" background-color="#545c64" class="el-menu-vertical-demo" default-active="1"
		text-color="#fff" :router="true">
		<el-menu-item v-for="item in noChildren()" :index="item.path">
			<component :is="item.icon" class="el-icon"></component>
			<span>{{ item.label }}</span>
		</el-menu-item>
		<el-sub-menu index="1">
			<template #title>
				<component :is="hasChildren()[0]?.icon" class="el-icon"></component>
				<span>{{ hasChildren()[0]?.label }}</span>
			</template>
			<el-menu-item v-for="item in hasChildren()[0]?.children" :index="item.path" :key="item.path">
				<component :is="item.icon" class="el-icon"></component>
				<span>{{ item.label }}</span>
			</el-menu-item>
		</el-sub-menu>
	</el-menu>
</template>

<style lang=scss>
.el-menu-vertical-demo {
	width: 100%;
	height: 100%;
	padding: 0px;
}
</style>