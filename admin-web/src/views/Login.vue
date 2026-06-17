<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Password from 'primevue/password'
import Message from 'primevue/message'

const router = useRouter()
const auth = useAuthStore()

const isLogin = ref(true)
const username = ref('')
const password = ref('')
const nickname = ref('')
const loading = ref(false)
const errorMsg = ref('')

async function handleSubmit() {
    errorMsg.value = ''
    loading.value = true
    try {
        if (isLogin.value) {
            await auth.login(username.value, password.value)
        } else {
            await auth.register(username.value, password.value, nickname.value)
        }
        router.push('/')
    } catch (err: any) {
        errorMsg.value = err.response?.data?.message || '操作失败，请重试'
    } finally {
        loading.value = false
    }
}

function toggleMode() {
    isLogin.value = !isLogin.value
    errorMsg.value = ''
}
</script>

<template>
    <div class="min-h-screen bg-gradient-to-br from-emerald-50 to-teal-100 flex items-center justify-center p-4">
        <Card class="w-full max-w-md shadow-xl">
            <template #title>
                <div class="text-center">
                    <h1 class="text-3xl font-bold text-emerald-600 mb-1">ChefMind</h1>
                    <p class="text-gray-500 text-sm font-normal">{{ isLogin ? '登录账户' : '注册新账户' }}</p>
                </div>
            </template>

            <template #content>
                <Message v-if="errorMsg" severity="error" class="mb-4">
                    {{ errorMsg }}
                </Message>

                <form @submit.prevent="handleSubmit" class="flex flex-col gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
                        <InputText v-model="username" class="w-full" placeholder="请输入用户名" :disabled="loading" />
                    </div>

                    <div v-if="!isLogin">
                        <label class="block text-sm font-medium text-gray-700 mb-1">昵称（选填）</label>
                        <InputText v-model="nickname" class="w-full" placeholder="显示名称" :disabled="loading" />
                    </div>

                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
                        <Password v-model="password" class="w-full" inputClass="w-full" placeholder="请输入密码"
                            :feedback="!isLogin" toggleMask :disabled="loading" />
                    </div>

                    <Button :label="isLogin ? '登录' : '注册'" type="submit" severity="success"
                        class="w-full mt-2" :loading="loading" />
                </form>

                <div class="text-center mt-4">
                    <Button :label="isLogin ? '没有账户？去注册' : '已有账户？去登录'"
                        link severity="secondary" @click="toggleMode" />
                </div>

                <div class="text-center mt-2">
                    <Button label="返回首页" link severity="secondary" @click="router.push('/')" />
                </div>
            </template>
        </Card>
    </div>
</template>
