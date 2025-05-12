import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'

import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
	plugins: [
		vue(),
		AutoImport({
			resolvers: [ElementPlusResolver()],
		}),
		Components({
			resolvers: [ElementPlusResolver()],
		}),
	],
	resolve: {
		alias: {
			'@': fileURLToPath(new URL('./src', import.meta.url))
		}
	},
	server: {
		proxy: {
			// 代理所有以 '/api' 开头的请求到后端服务器
			'/api': {
				target: 'http://localhost:9093', // 后端 API 服务地址
				changeOrigin: true,              // 是否改变源头，设置为 true 可以避免一些跨域问题
			},
		},
	}
})
