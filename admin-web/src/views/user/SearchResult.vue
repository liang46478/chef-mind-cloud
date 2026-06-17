<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchRecipes, searchRecipesByIngredients } from '@/api/recipe'
import Card from 'primevue/card'
import Tag from 'primevue/tag'
import Chip from 'primevue/chip'
import Message from 'primevue/message'
import ProgressSpinner from 'primevue/progressspinner'
import Button from 'primevue/button'

const route = useRoute()
const router = useRouter()

const recipes = ref<any[]>([])
const loading = ref(true)
const noResults = ref(false)
const query = ref('')
const searchMode = ref<'text' | 'ingredient'>('text')

async function doSearch() {
    const q = route.query.q as string
    const ingredientIds = route.query.ingredients as string
    if (!q && !ingredientIds) return

    query.value = q || `已选食材(ID: ${ingredientIds})`
    loading.value = true; noResults.value = false

    try {
        if (ingredientIds) {
            searchMode.value = 'ingredient'
            const ids = ingredientIds.split(',').map(Number).filter(n => !isNaN(n))
            const res = await searchRecipesByIngredients(ids)
            recipes.value = (res.data.data || []).map((r: any) => ({
                ...r,
                matchCount: r.match_count || 0
            }))
        } else {
            searchMode.value = 'text'
            const res = await searchRecipes(q!)
            recipes.value = res.data.data || []
        }
        noResults.value = recipes.value.length === 0
    } catch {
        recipes.value = []; noResults.value = true
    } finally { loading.value = false }
}

onMounted(doSearch)
watch(() => [route.query.q, route.query.ingredients], doSearch)
</script>

<template>
    <div class="min-h-screen bg-gray-50">
        <header class="bg-white shadow-sm">
            <div class="max-w-6xl mx-auto px-4 py-4 flex items-center justify-between">
                <Button label="← 返回首页" text severity="secondary" @click="router.push('/')" />
                <h1 class="text-xl font-semibold text-gray-800">
                    {{ searchMode === 'ingredient' ? '🍅 食材匹配结果' : `搜索: "${query}"` }}
                </h1>
                <div></div>
            </div>
        </header>

        <main class="max-w-6xl mx-auto px-4 py-8">
            <div v-if="loading" class="flex justify-center py-20"><ProgressSpinner /></div>

            <Message v-else-if="noResults" severity="info" class="text-center py-8">
                <div class="flex flex-col items-center gap-4">
                    <i class="pi pi-search text-4xl text-gray-400" />
                    <p class="text-lg text-gray-500">没有找到匹配的菜谱</p>
                    <p class="text-sm text-gray-400">试试选择更多食材或更换食材组合</p>
                    <Button label="← 重新选择食材" severity="success" @click="router.push('/')" />
                </div>
            </Message>

            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                <Card v-for="recipe in recipes" :key="recipe.id"
                      class="cursor-pointer hover:shadow-lg transition-shadow"
                      @click="router.push(`/recipe/${recipe.id}`)">
                    <template #header>
                        <div class="relative">
                            <img :src="recipe.imageUrl || 'https://placehold.co/400x300/10b981/white?text=' + recipe.title"
                                 :alt="recipe.title" class="w-full h-40 object-cover" />
                            <div v-if="recipe.videoUrl" class="absolute top-2 right-2 bg-red-500 text-white text-xs px-2 py-1 rounded-full flex items-center gap-1">
                                🎬 视频
                            </div>
                        </div>
                    </template>
                    <template #title>
                        <div class="flex items-center justify-between">
                            <span class="text-base">{{ recipe.title }}</span>
                            <Tag v-if="recipe.matchCount" :value="`${recipe.matchCount}种食材匹配`" severity="success" />
                        </div>
                    </template>
                    <template #content>
                        <div class="flex items-center gap-2 text-sm text-gray-500 flex-wrap">
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
