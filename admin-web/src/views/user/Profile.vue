<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserProfile, updatePreference } from '@/api/user'
import type { UserInfo } from '@/api/user'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import SelectButton from 'primevue/selectbutton'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'

const router = useRouter()
const loading = ref(true)
const saving = ref(false)
const error = ref('')
const success = ref('')

const user = ref<UserInfo>({
    id: 0, username: '', nickname: '', avatar: '', email: '', phone: '',
    cuisinePreference: '', allergens: '', healthGoal: '均衡营养',
})

const healthGoals = [
    { label: '减脂', value: '减脂' },
    { label: '增肌', value: '增肌' },
    { label: '均衡营养', value: '均衡营养' },
]

onMounted(async () => {
    try {
        const res = await getUserProfile()
        if (res.data.data) {
            user.value = { ...user.value, ...res.data.data }
        }
    } catch {
        error.value = '加载用户信息失败'
    } finally {
        loading.value = false
    }
})

async function saveProfile() {
    saving.value = true
    error.value = ''
    success.value = ''
    try {
        await updatePreference({
            cuisineType: user.value.cuisinePreference,
            taste: '',
            spicinessLevel: '',
            healthGoal: user.value.healthGoal,
            allergens: user.value.allergens ? [user.value.allergens] : [],
        })
        success.value = '保存成功'
    } catch (e: any) {
        error.value = e.response?.data?.message || '保存失败'
    } finally {
        saving.value = false
    }
}
</script>

<template>
    <div class="min-h-screen bg-gray-50">
        <header class="bg-white shadow-sm">
            <div class="max-w-3xl mx-auto px-4 py-4 flex items-center justify-between">
                <Button label="← 返回首页" text severity="secondary" @click="router.push('/')" />
                <h1 class="text-xl font-semibold text-gray-800">👤 个人中心</h1>
                <div></div>
            </div>
        </header>

        <main class="max-w-3xl mx-auto px-4 py-8">
            <div v-if="loading" class="flex justify-center py-12"><ProgressSpinner /></div>

            <Card v-else>
                <template #content>
                    <Message v-if="success" severity="success" class="mb-4">{{ success }}</Message>
                    <Message v-if="error" severity="error" class="mb-4">{{ error }}</Message>

                    <div class="flex flex-col gap-6">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
                            <InputText :modelValue="user.username" class="w-full" disabled />
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">昵称</label>
                            <InputText v-model="user.nickname" class="w-full" />
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
                            <InputText v-model="user.email" class="w-full" />
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">手机号</label>
                            <InputText v-model="user.phone" class="w-full" />
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">偏好菜系</label>
                            <InputText v-model="user.cuisinePreference" class="w-full" placeholder="如：川菜、粤菜..." />
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">过敏源</label>
                            <InputText v-model="user.allergens" class="w-full" placeholder="如：花生、海鲜..." />
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">健康目标</label>
                            <SelectButton v-model="user.healthGoal" :options="healthGoals" optionLabel="label" optionValue="value" />
                        </div>
                        <Button label="保存设置" severity="success" :loading="saving" @click="saveProfile" class="mt-4" />
                    </div>
                </template>
            </Card>
        </main>
    </div>
</template>
