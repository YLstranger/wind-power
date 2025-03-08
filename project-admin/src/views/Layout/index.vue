<script setup>
import Head from './components/Head.vue'
import Aside from './components/Aside.vue'

</script>

<template>
	<div class="common-layout">
		<el-container>
			<el-header>

				<Head></Head>
			</el-header>
			<el-container>
				<el-aside width="200px" height="100%">
					<Aside></Aside>
				</el-aside>
				<el-main>
					<RouterView></RouterView>
				</el-main>
			</el-container>
		</el-container>
	</div>
	<RouterView></RouterView>
</template>

<style lang="scss">
.common-layout {
	height: 100%;

	.el-container {
		height: 100%;


		.el-header {
			color: white;
			background-color: #333;
			font-size: 20px;
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 0 30px;
		}

		.el-aside {
			padding: 0px;

		}
	}
}
</style>