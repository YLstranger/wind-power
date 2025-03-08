import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [
		// {path,name,component}
		{
			path: '/',
			component: () => import('@/views/Layout/index.vue'),
			name: 'Layout',
			redirect: '/home',
			// children: []
			//配置原始静态页面，避免build之后部署到nginx上出现404
			children: [
				{
					path: 'lstm',
					component: () => import('@/views/Machines/lstm.vue')
				},
				{
					path: 'home',
					component: () => import('@/views/Home/index.vue')
				},
				{
					path: 'user',
					component: () => import('@/views/User/index.vue')
				},
				{
					path: 'cnn_lstm',
					component: () => import('@/views/Machines/cnn_lstm.vue')
				},
				{
					path: 'sat_lstm',
					component: () => import('@/views/Machines/sat_lstm.vue')
				},
				{
					path: 'gat_lstm',
					component: () => import('@/views/Machines/gat_lstm.vue')
				},
				{
					path: 'msg_xlstm',
					component: () => import('@/views/Machines/msg_xlstm.vue')
				},
			]
		}, {
			path: '/login',
			name: 'login',
			component: () => import('@/views/Login/index.vue')
		}, {
			path: '/register',
			name: 'register',
			component: () => import('@/views/Register/index.vue')
		}
	]
})

//路由守卫
router.beforeEach(to => {
	const token = localStorage.getItem('token')
	//跳转注册页面直接放行
	if (to.name == 'register') {
		return
	}
	else if (!token && to.name != 'login') {
		return { name: 'login' }
	}
})

export default router
