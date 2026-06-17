import axios from 'axios'
import type { AxiosInstance, AxiosResponse } from 'axios'

const http: AxiosInstance = axios.create({
    baseURL: '/api',
    timeout: 30000,
    headers: {
        'Content-Type': 'application/json',
    },
})

// 请求拦截器 - 自动附加 JWT Token
http.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

// 响应拦截器
http.interceptors.response.use(
    (response: AxiosResponse) => {
        const res = response.data
        if (res.code === 401) {
            localStorage.removeItem('token')
            localStorage.removeItem('userId')
            localStorage.removeItem('username')
            localStorage.removeItem('nickname')
            window.location.href = '/login'
            return Promise.reject(new Error(res.message || '未登录'))
        }
        return response
    },
    (error) => {
        console.error('API Error:', error)
        return Promise.reject(error)
    }
)

export default http

export interface ApiResult<T> {
    code: number
    message: string
    data: T
}
