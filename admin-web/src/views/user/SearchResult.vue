<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchRecipes } from '@/api/recipe'
import type { Recipe } from '@/api/recipe'
import Card from 'primevue/card'
import Tag from 'primevue/tag'
import Message from 'primevue/message'
import ProgressSpinner from 'primevue/progressspinner'
import Button from 'primevue/button'

const route = useRoute()
const router = useRouter()

const recipes = ref<Recipe[]>([])
const loading = ref(true)
const noResults = ref(false)
const query = ref('')

async function doSearch(q: string) {
    if (!q) return
    query.value = q
    loading.value = true
    noResults.value = false
    try {
        const res = await searchRecipes(q)
        recipes.value = res.data.data || []
        noResults.value = recipes.value.length === 0
    } catch {
        recipes.value = []
        noResults.value = true
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    const q = route.query.q as string
    if (q) doSearch(q)
})

watch(() => route.query.q, (q) => {
    if (q) doSearch(q as string)
})
</script>

<template>
    <div class="min-h-screen bg-gray-50">
        <header class="bg-white shadow-sm">
            <div class="max-w-5xl mx-auto px-4 py-4 flex items-center justify-between">
                <Button label="← 返回首页" text severity="secondary" @click="router.push('/')" />
                <h1 class="text-xl font-semibold text-gray-800">搜索: "{{ query }}"</h1>
                <div></div>
            </div>
        </header>

        <main class="max-w-5xl mx-auto px-4 py-8">
            <!-- 加载中 -->
            <div v-if="loading" class="flex justify-center py-20">
                <ProgressSpinner />
            </div>

            <!-- 无结果 -->
            <Message v-else-if="noResults" severity="info" class="text-center py-8">
                <div class="flex flex-col items-center gap-4">
                    <i class="pi pi-search text-4xl text-gray-400" />
                    <p class="text-lg text-gray-500">没有找到 "{{ query }}" 的相关菜谱</p>
                    <p class="text-sm text-gray-400">试试其他关键词，如"鸡肉"、"川菜"、"快手菜"</p>
                </div>
            </Message>

            <!-- 搜索结果 -->
            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                <Card v-for="recipe in recipes" :key="recipe.id"
                      class="cursor-pointer hover:shadow-lg transition-shadow"
                      @click="router.push(`/recipe/${recipe.id}`)">
                    <template #header>
                        <img :src="recipe.imageUrl || 'https://placehold.co/400x300/10b981/white?text=' + recipe.title"
                             :alt="recipe.title" class="w-full h-40 object-cover" />
                    </template>
                    <template #title>
                        <span class="text-base">{{ recipe.title }}</span>
                    </template>
                    <template #content>
                        <div class="flex items-center gap-2 text-sm text-gray-500">
                            <Tag :value="recipe.difficulty" severity="success" v-if="recipe.difficulty" />
                            <span v-if="recipe.cuisineType">{{ recipe.cuisineType }}</span>
                            <span v-if="recipe.cookTime">⏱{{ recipe.cookTime }}分钟</span>
                        </div>
                        <p class="text-sm text-gray-600 mt-2 line-clamp-2">{{ recipe.description }}</p>
                    </template>
                </Card>
            </div>
        </main>
    </div>
</template>
