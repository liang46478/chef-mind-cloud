import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import http from '@/api/request'

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || '')
    const userId = ref(Number(localStorage.getItem('userId') || 0))
    const username = ref(localStorage.getItem('username') || '')
    const nickname = ref(localStorage.getItem('nickname') || '')

    const isLoggedIn = computed(() => !!token.value)
    const displayName = computed(() => nickname.value || username.value)

    async function login(loginUsername: string, password: string) {
        const res = await http.post('/user/auth/login', { username: loginUsername, password })
        const data = res.data.data
        token.value = data.token
        userId.value = data.userId
        username.value = data.username
        nickname.value = data.nickname

        localStorage.setItem('token', data.token)
        localStorage.setItem('userId', String(data.userId))
        localStorage.setItem('username', data.username)
        localStorage.setItem('nickname', data.nickname || '')
        return data
    }

    async function register(regUsername: string, password: string, regNickname?: string) {
        const res = await http.post('/user/auth/register', {
            username: regUsername,
            password,
            nickname: regNickname || regUsername
        })
        const data = res.data.data
        token.value = data.token
        userId.value = data.userId
        username.value = data.username
        nickname.value = data.nickname

        localStorage.setItem('token', data.token)
        localStorage.setItem('userId', String(data.userId))
        localStorage.setItem('username', data.username)
        localStorage.setItem('nickname', data.nickname || '')
        return data
    }

    function logout() {
        token.value = ''
        userId.value = 0
        username.value = ''
        nickname.value = ''
        localStorage.removeItem('token')
        localStorage.removeItem('userId')
        localStorage.removeItem('username')
        localStorage.removeItem('nickname')
    }

    return { token, userId, username, nickname, isLoggedIn, displayName, login, register, logout }
})
