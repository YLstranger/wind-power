/**
 * 环境配置文件
 * 一般在企业级项目里面有三个环境
 * 开发环境
 * 测试环境
 * 线上环境(生产环境)
 */
// 当前的环境
const env = import.meta.env.MODE || 'prod'

const EnvConfig = {
	development: {
		baseApi: '/api',
		mockApi: '/api',
	},
	test: {
		baseApi: '//test.future.com/api',
		mockApi: 'https://www.fastmock.site/mock/0a4e41ea2ea30dca8177e93954b11663/api',
	},
	pro: {
		baseApi: '//future.com/api',
		mockApi: 'https://www.fastmock.site/mock/0a4e41ea2ea30dca8177e93954b11663/api',
	},
}

export default {
	env,
	// mock的总开关,
	mock: false,
	...EnvConfig[env]
}

