<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getRecommendedRecipes } from '@/api/recipe'
import Button from 'primevue/button'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Carousel from 'primevue/carousel'
import Avatar from 'primevue/avatar'
import ProgressSpinner from 'primevue/progressspinner'

const router = useRouter()
const auth = useAuthStore()
const searchQuery = ref('')
const featuredRecipes = ref<any[]>([])
const loading = ref(true)

onMounted(async () => {
    try {
        const res = await getRecommendedRecipes()
        featuredRecipes.value = res.data.data || []
    } catch {
        featuredRecipes.value = []
    } finally {
        loading.value = false
    }
})

function goToDetail(id: number) {
    router.push(`/recipe/${id}`)
}

function search() {
    if (searchQuery.value.trim()) {
        router.push(`/search?q=${encodeURIComponent(searchQuery.value.trim())}`)
    }
}

function logout() {
    auth.logout()
}
</script>

<template>
    <div class="min-h-screen bg-gray-50">
        <header class="bg-white shadow-sm">
            <div class="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
                <h1 class="text-2xl font-bold text-emerald-600 cursor-pointer" @click="router.push('/')">ChefMind</h1>
                <div class="flex items-center gap-4">
                    <span class="p-input-icon-left">
                        <i class="pi pi-search" />
                        <InputText v-model="searchQuery" placeholder="搜索菜品..." @keyup.enter="search" />
                    </span>
                    <template v-if="auth.isLoggedIn">
                        <Button label="生成计划" severity="success" @click="router.push('/meal-plan')" class="hidden sm:flex" />
                        <Button label="就餐记录" severity="secondary" text @click="router.push('/meal-records')" class="hidden sm:flex" />
                        <div class="flex items-center gap-2">
                            <Avatar :label="auth.displayName.charAt(0)" size="small" style="background-color: #10b981; color: white" shape="circle" />
                            <span class="text-sm text-gray-700 hidden md:inline">{{ auth.displayName }}</span>
                        </div>
                        <Button label="退出" severity="secondary" text size="small" @click="logout" />
                    </template>
                    <template v-else>
                        <Button label="登录" severity="secondary" text @click="router.push('/login')" />
                        <Button label="注册" @click="router.push('/login')" />
                    </template>
                </div>
            </div>
        </header>

        <main class="max-w-7xl mx-auto px-4 py-8">
            <section class="mb-8">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-xl font-semibold text-gray-800">🔥 为你推荐</h2>
                    <Button label="生成用餐计划" severity="success" @click="router.push('/meal-plan')" />
                </div>
                <div v-if="loading" class="flex justify-center py-8"><ProgressSpinner /></div>
                <Carousel v-else-if="featuredRecipes.length" :value="featuredRecipes" :numVisible="3" :numScroll="1" circular>
                    <template #item="slotProps">
                        <div class="mx-2">
                            <Card class="cursor-pointer hover:shadow-lg transition-shadow" @click="goToDetail(slotProps.data.id)">
                                <template #header>
                                    <img :src="slotProps.data.imageUrl || 'https://placehold.co/400x300/10b981/white?text=' + slotProps.data.title" :alt="slotProps.data.title" class="w-full h-48 object-cover" />
                                </template>
                                <template #title>
                                    <span class="text-lg">{{ slotProps.data.title }}</span>
                                </template>
                                <template #content>
                                    <div class="flex gap-2 text-sm text-gray-500">
                                        <span v-if="slotProps.data.cookTime">⏱ {{ slotProps.data.cookTime }}分钟</span>
                                        <span v-if="slotProps.data.difficulty">📊 {{ slotProps.data.difficulty }}</span>
                                    </div>
                                </template>
                            </Card>
                        </div>
                    </template>
                </Carousel>
                <p v-else class="text-gray-400 text-center py-8">暂无推荐菜品，快去发现美食吧！</p>
            </section>

            <section class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <Card class="cursor-pointer hover:border-emerald-500 transition-colors" @click="router.push('/meal-plan')">
                    <template #title>📋 用餐计划</template>
                    <template #content>
                        <p class="text-gray-600">AI 智能生成你的每周餐食计划，营养均衡每一天</p>
                    </template>
                </Card>
                <Card class="cursor-pointer hover:border-emerald-500 transition-colors" @click="router.push('/meal-records')">
                    <template #title>📝 就餐记录</template>
                    <template #content>
                        <p class="text-gray-600">记录每日饮食，让推荐越来越懂你的口味</p>
                    </template>
                </Card>
                <Card class="cursor-pointer hover:border-emerald-500 transition-colors" @click="router.push('/profile')">
                    <template #title>👤 个人中心</template>
                    <template #content>
                        <p class="text-gray-600">管理饮食偏好、过敏源和营养目标</p>
                    </template>
                </Card>
            </section>
        </main>
    </div>
</template>
