<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRecipeDetail } from '@/api/recipe'
import type { Recipe } from '@/api/recipe'
import VideoPlayer from '@/components/VideoPlayer.vue'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Chip from 'primevue/chip'
import Tag from 'primevue/tag'
import ProgressSpinner from 'primevue/progressspinner'

const route = useRoute()
const router = useRouter()
const recipe = ref<Recipe | null>(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
    try {
        const id = Number(route.params.id)
        const res = await getRecipeDetail(id)
        recipe.value = res.data.data
        if (!recipe.value) {
            error.value = '菜谱不存在'
        }
    } catch (e: any) {
        error.value = e.response?.data?.message || '加载失败，请稍后重试'
    } finally {
        loading.value = false
    }
})
</script>

<template>
    <div class="min-h-screen bg-gray-50">
        <div v-if="loading" class="flex justify-center py-20">
            <ProgressSpinner />
        </div>

        <div v-else-if="error" class="max-w-4xl mx-auto px-4 py-8 text-center">
            <i class="pi pi-exclamation-circle text-5xl text-gray-300 mb-4" />
            <p class="text-lg text-gray-500">{{ error }}</p>
            <Button label="← 返回首页" text severity="secondary" @click="router.push('/')" class="mt-4" />
        </div>

        <template v-else-if="recipe">
            <div class="max-w-4xl mx-auto px-4 py-8">
                <Button label="← 返回首页" text severity="secondary" @click="router.push('/')" class="mb-4" />

                <Card>
                    <template #header>
                        <img :src="recipe.imageUrl || 'https://placehold.co/800x400/10b981/white?text=' + recipe.title"
                             :alt="recipe.title" class="w-full h-64 object-cover rounded-t-xl" />
                    </template>
                    <template #title>
                        <div class="flex items-center justify-between">
                            <h1 class="text-3xl font-bold">{{ recipe.title }}</h1>
                            <Tag :value="recipe.difficulty"
                                 :severity="recipe.difficulty === '初级' ? 'success' : recipe.difficulty === '中级' ? 'warn' : 'danger'" />
                        </div>
                    </template>
                    <template #subtitle>
                        <div class="flex gap-4 mt-2 text-gray-500 flex-wrap">
                            <span v-if="recipe.cuisineType">🍳 {{ recipe.cuisineType }}</span>
                            <span v-if="recipe.prepTime">⏱ 准备 {{ recipe.prepTime }}分钟 / 烹饪 {{ recipe.cookTime }}分钟</span>
                            <span v-if="recipe.servings">👥 {{ recipe.servings }}人份</span>
                            <span v-if="recipe.calories">🔥 {{ recipe.calories }}千卡</span>
                        </div>
                    </template>
                    <template #content>
                        <p v-if="recipe.description" class="text-gray-600 mb-6">{{ recipe.description }}</p>

                        <!-- 做菜视频 -->
                        <div v-if="recipe.videoUrl" class="mb-8">
                            <h2 class="text-xl font-semibold mb-3">🎬 做菜视频</h2>
                            <VideoPlayer :videoUrl="recipe.videoUrl" :title="recipe.title" />
                        </div>

                        <!-- 标签 -->
                        <div v-if="recipe.tags && recipe.tags.length" class="flex gap-2 mb-6">
                            <Chip v-for="tag in recipe.tags" :key="tag" :label="tag" />
                        </div>

                        <!-- 食材 -->
                        <h2 class="text-xl font-semibold mb-3">🛒 食材清单</h2>
                        <div v-if="recipe.ingredients && recipe.ingredients.length"
                             class="grid grid-cols-2 md:grid-cols-3 gap-2 mb-6">
                            <div v-for="ing in recipe.ingredients" :key="ing.id || ing.name"
                                 class="bg-gray-50 rounded-lg p-3 flex justify-between">
                                <span>{{ ing.name }}</span>
                                <span class="text-gray-500">{{ ing.quantity }}{{ ing.unit }}</span>
                            </div>
                        </div>
                        <p v-else class="text-gray-400 mb-6">暂无食材信息</p>

                        <!-- 步骤 -->
                        <h2 class="text-xl font-semibold mb-3">📖 烹饪步骤</h2>
                        <div v-if="recipe.steps && recipe.steps.length" class="space-y-4">
                            <div v-for="step in recipe.steps" :key="step.stepNumber"
                                 class="flex gap-4 items-start bg-gray-50 rounded-lg p-4">
                                <span class="bg-emerald-500 text-white rounded-full w-8 h-8 flex items-center justify-center shrink-0">
                                    {{ step.stepNumber }}
                                </span>
                                <div class="flex-1">
                                    <p class="text-gray-700">{{ step.instruction }}</p>
                                    <div v-if="step.timerMinutes" class="text-sm text-emerald-600 mt-1">
                                        ⏱ 约{{ step.timerMinutes }}分钟
                                    </div>
                                    <!-- 步骤视频 -->
                                    <div v-if="step.videoUrl" class="mt-2">
                                        <VideoPlayer :videoUrl="step.videoUrl" :title="`步骤${step.stepNumber}`" />
                                    </div>
                                </div>
                            </div>
                        </div>
                        <p v-else class="text-gray-400">暂无步骤信息</p>
                    </template>
                </Card>
            </div>
        </template>
    </div>
</template>
