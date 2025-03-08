import axios from 'axios'
import config from '../config'
import { ElMessage } from 'element-plus'
import router from '@/router'
const NETWORK_ERROR = '网络请求异常,请稍后重试.....'
// 创建一个axios实例对象
const httpInstance = axios.create({
	baseURL: config.baseApi,
})



// axios请求拦截器
httpInstance.interceptors.request.use(config => {
	//获取token
	let token = localStorage.getItem('token')
	// console.log('localStorage中token为', token)

	//将token携带在请求头当中
	// console.log('请求配置信息为：', config)
	// console.log('请求头配置信息为：', config.headers)
	if (token) {
		config.headers['Authorization'] = token
		// console.log("我是赋值之后的请求头中的Authorization:", config.headers['Authorization'])
	}
	return config
}, e => Promise.reject(e))


// axios响应式拦截器  res.data=>Result对象=>code,data,message
//两个回调函数，一个是响应成功，一个是响应失败
httpInstance.interceptors.response.use(res => {
	// console.log("响应成功结果为：", res)
	const { code, data, msg } = res.data
	//code 1：表示成功 其他表示失败
	if (code !== 1) {
		ElMessage({
			type: 'error',
			message: msg || NETWORK_ERROR
		})
		return Promise.reject(msg)
	}
	return data //Result对象的data属性
}, e => {
	console.log('响应失败结果为：', e)
	ElMessage({
		type: 'warning',
		//e.response.data是后端使用HttpServletResponse对象写回的数据
		//response.getWriter().write("用户信息认证失败，退出登录！");
		message: e.response.data || NETWORK_ERROR
	})
	// 401token失效处理
	// 1 清除本地用户数据
	// 2 跳转到登录界面
	if (e.response.status === 401) {
		console.log('token失效，清除token，跳转到登录页面')
		localStorage.clear('token')
		// console.log("路由信息为：", router)
		//这里是重点：不能直接使用useRouter方法拿到router示例（因为useRouter依赖于vue组件的上下文，不能在普通的js文件中使用）
		//想要进行路由跳转的话需要导入router示例
		router.push('/login')
	}
	return Promise.reject(e)
})

// 封装的核心函数
function request (options) {
	options.method = options.method || 'get'
	if (options.method.toLowerCase() == 'get') {
		options.params = options.data
	}
	// 对mock的处理
	let isMock = config.mock
	if (typeof options.mock !== 'undefined') {
		isMock = options.mock
	}
	// 对线上环境做处理
	if (config.env == 'prod') {
		// 不给你用到mock的机会
		httpInstance.defaults.baseURL = config.baseApi
	} else {
		httpInstance.defaults.baseURL = isMock ? config.mockApi : config.baseApi
	}
	return httpInstance(options)
}
export default request