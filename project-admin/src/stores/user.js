import {defineStore} from "pinia";
import api from "@/apis/api";

export const useUserStore = defineStore('user', () => {
        //定义用户数据
        let token = ''

        //进行登录，将请求结果赋值给token
        const doLogin = async ({username, password, email, inputCode}) => {
            const res = await api.loginApi({username, password, email, inputCode})
            // console.log('登录响应结果：', res)
            token = res
            localStorage.setItem('token', token)
        }

        //以对象的形式将数据导出
        return {
            token,
            doLogin,
        }
    }
)