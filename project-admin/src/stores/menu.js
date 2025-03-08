import { defineStore } from "pinia";
export const useMenuStore = defineStore('menu', () => {
	let menu1 = [
		{
			path: '/home',
			name: 'home',
			label: '数据管理',
			icon: 'Memo',
			url: 'Home/index'
		},
		{
			path: '/user',
			name: 'user',
			label: '用户管理',
			icon: 'User',
			url: 'User/index'
		},
		{
			label: '功率预测',
			icon: 'Promotion',
			children: [
				{
					path: '/lstm',
					name: 'lstm',
					label: 'LSTM',
					url: 'Machines/lstm'
				},
				{
					path: '/cnn_lstm',
					name: 'cnn_lstm',
					label: 'CNN-LSTM',
					url: 'Machines/cnn_lstm'
				}, {
					path: '/sat_lstm',
					name: 'sat_lstm',
					label: 'SAT-LSTM',
					url: 'Machines/sat_lstm'
				}, {
					path: '/gat_lstm',
					name: 'gat_lstm',
					label: 'GAT-LSTM',
					url: 'Machines/gat_lstm'
				},
				{
					path: '/msg_xlstm',
					name: 'msg_xlstm',
					label: 'MSG-XLSTM',
					url: 'Machines/msg_xlstm'
				},
			]
		}
	]

	let menu2 = [
		{
			path: '/home',
			name: 'home',
			label: '数据管理',
			icon: 'Memo',
			url: 'Home/index'
		},
		{
			label: '功率预测',
			icon: 'Promotion',
			children: [
				{
					path: '/lstm',
					name: 'lstm',
					label: 'LSTM',
					url: 'Machines/lstm'
				},
				{
					path: '/cnn_lstm',
					name: 'cnn_lstm',
					label: 'CNN-LSTM',
					url: 'Machines/cnn_lstm'
				}, {
					path: '/sat_lstm',
					name: 'sat_lstm',
					label: 'SAT-LSTM',
					url: 'Machines/sat_lstm'
				}, {
					path: '/gat_lstm',
					name: 'gat_lstm',
					label: 'GAT-LSTM',
					url: 'Machines/gat_lstm'
				},
				{
					path: '/msg_xlstm',
					name: 'msg_xlstm',
					label: 'MSG-XLSTM',
					url: 'Machines/msg_xlstm'
				},
			]
		}
	]

	let menuList = []
	let menuArray = []
	//将menu存储到localStorage
	const setMenu = (userType) => {
		//管理员
		if (userType === 0) {
			localStorage.setItem('menu', JSON.stringify(menu1))
		}
		//普通用户 
		else if (userType == 1) {
			localStorage.setItem('menu', JSON.stringify(menu2))
		}
	}

	//在路由中渲染menu
	const addMenu = (router) => {
		if (!localStorage.getItem('menu')) {
			return
		}
		menuList = JSON.parse(localStorage.getItem('menu'))

		menuList.forEach(item => {
			if (item.children) {
				item.children = item.children.map(item => {
					let url = `../views/${item.url}.vue`
					item.component = () => import( /* @vite-ignore */url)
					return item
				})
				menuArray.push(...item.children)
			} else {
				let url = `../views/${item.url}.vue`
				item.component = () => import(/* @vite-ignore */url)
				menuArray.push(item)
			}
		})

		menuArray.forEach(item => {
			router.addRoute('Layout', item)
		})
	}
	return {
		menu1,
		menu2,
		setMenu,
		addMenu
	}
}
)
