/**
 * 整个项目api的管理
 * 
 */

import request from '../utils/http'

export default {
	// 用户登录
	loginApi: (data) => {
		return request({
			url: '/user/login',
			method: 'post',
			mock: false,
			data
		})
	},

	//获取用户列表
	getUserListApi: (data) => {
		return request({
			url: '/user/page',
			method: 'get',
			mock: false,
			data
		})
	},

	//编辑用户信息
	editUserInfoApi: (data) => {
		return request({
			url: '/user/update',
			method: 'put',
			mock: false,
			data
		})
	},

	//删除用户信息
	deleteUserInfoApi: (data) => {
		return request({
			url: `/user/delete/${data}`,
			method: 'delete',
			mock: false,
		})
	},

	//分页查询风机列表数据
	getMachineListApi: (data) => {
		return request({
			url: '/machine/page',
			method: 'get',
			mock: false,
			data
		})
	},

	//删除风机信息
	deleteMachineInfoApi: (data) => {
		return request({
			url: `/machine/delete/${data}`,
			method: 'delete',
			mock: false,
		})
	},

	//根据风机id获取风机数据
	getMachineDataByIdApi: (data) => {
		return request({
			url: "/machine/getMachineData",
			method: 'get',
			mock: false,
			data
		})
	},

	//获取验证码
	getCodeApi: () => {
		return request({
			url: '/user/code',
			method: 'get',
			mock: false,
		})
	},

	//用户注册
	registerApi: (data) => {
		return request({
			url: '/user/register',
			method: 'post',
			mock: false,
			data
		})
	},

	//根据id查询用户信息
	getUserInfoByIdApi: (data) => {
		return request({
			url: `/user/getById/${data}`,
			method: 'get',
			mock: false,
		})
	}

}