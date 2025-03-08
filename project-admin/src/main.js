import './assets/css/reset.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import { useMenuStore } from './stores/menu'
import App from './App.vue'
import router from './router'
// 引入 Mock 数据规则文件,这条非常重要，不然接口会显示404
// import '@/apis/mock'

//导入element-plus相关依赖
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'

import * as echarts from 'echarts';


const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component)
}
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

const menuStore = useMenuStore()

// 用户登录之后拿到menu数据，才能配置新的路由        
menuStore.addMenu(router)
app.use(router)

app.mount('#app')

//全局挂载echarts
app.config.globalProperties.$echarts = echarts
