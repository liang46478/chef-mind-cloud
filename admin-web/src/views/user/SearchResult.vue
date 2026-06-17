<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchRecipes, searchRecipesByIngredients, aiGenerateByIngredients } from '@/api/recipe'
import Card from 'primevue/card'
import Tag from 'primevue/tag'
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
const aiGenerating = ref(false)
const aiGenerated = ref(false)
const aiSource = ref('')

// 获取URL中的食材名称（从IngredientSelector传来的）
const ingredientNames = ref<string[]>([])

async function doSearch() {
    const q = route.query.q as string
    const ingredientIds = route.query.ingredients as string
    const names = route.query.names as string
    if (!q && !ingredientIds) return

    query.value = q || ''
    if (names) ingredientNames.value = names.split(',')
    loading.value = true; noResults.value = false; aiGenerated.value = false

    try {
        if (ingredientIds) {
            searchMode.value = 'ingredient'
            const ids = ingredientIds.split(',').map(Number).filter(n => !isNaN(n))
            const res = await searchRecipesByIngredients(ids)
            recipes.value = (res.data.data || []).map((r: any) => ({
                ...r, matchCount: r.match_count || 0
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

async function generateWithAI() {
    aiGenerating.value = true
    aiGenerated.value = true
    try {
        // 从URL参数获取食材名称
        const names = route.query.names as string
        const ingNames = names ? names.split(',') : []

        if (ingNames.length === 0) {
            // 尝试从ingredientIds反查
            const ids = (route.query.ingredients as string || '').split(',').filter(Boolean)
            if (ids.length === 0) { aiGenerating.value = false; return }
            ingNames.push(...ids.map((_, i) => `食材${i+1}`))
        }

        const res = await aiGenerateByIngredients(ingNames)
        const data = res.data.data
        if (data) {
            aiSource.value = data.source || 'ai'
            recipes.value = (data.recipes || []).map((r: any, idx: number) => ({
                ...r, id: `ai-${idx}`, isAiGenerated: true
            }))
            noResults.value = false
        }
    } catch {
        // AI失败也无所谓，保持无结果状态
    } finally { aiGenerating.value = false }
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
            <div v-if="loading && !aiGenerating" class="flex justify-center py-20"><ProgressSpinner /></div>

            <!-- 无结果 - 显示AI生成按钮 -->
            <Message v-else-if="noResults && !aiGenerated" severity="info" class="text-center py-8">
                <div class="flex flex-col items-center gap-4">
                    <i class="pi pi-search text-4xl text-gray-400" />
                    <p class="text-lg text-gray-500">没有找到匹配的菜谱</p>
                    <p class="text-sm text-gray-400">试试选择更多食材或使用 AI 智能生成</p>
                    <div class="flex gap-3">
                        <Button label="← 重新选择食材" severity="secondary" @click="router.push('/')" />
                        <Button label="🤖 AI 生成菜谱" severity="success" :loading="aiGenerating" @click="generateWithAI" />
                    </div>
                </div>
            </Message>

            <!-- AI生成中 -->
            <div v-else-if="aiGenerating" class="flex flex-col items-center justify-center py-20">
                <ProgressSpinner />
                <p class="text-gray-500 mt-4">🤖 AI 正在为你生成菜谱...</p>
            </div>

            <!-- AI生成结果来源标记 -->
            <div v-if="aiGenerated && !noResults" class="mb-4">
                <Tag v-if="aiSource === 'ai'" value="🤖 AI 生成" severity="success" class="mr-2" />
                <Tag v-else-if="aiSource === 'fallback'" value="📋 推荐菜谱" severity="warn" class="mr-2" />
                <Button label="🔄 换一批" severity="info" size="small" :loading="aiGenerating" @click="generateWithAI" />
            </div>

            <!-- 搜索结果 -->
            <div v-if="!noResults || aiGenerated" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                <Card v-for="recipe in recipes" :key="recipe.id"
                      class="cursor-pointer hover:shadow-lg transition-shadow"
                      @click="recipe.isAiGenerated ? null : router.push(`/recipe/${recipe.id}`)">
                    <template #header>
                        <div class="relative">
                            <img :src="recipe.imageUrl || 'https://placehold.co/400x300/10b981/white?text=' + (recipe.name || recipe.title)"
                                 :alt="recipe.name || recipe.title" class="w-full h-40 object-cover" />
                            <div v-if="recipe.videoUrl || recipe.isAiGenerated" class="absolute top-2 right-2 bg-red-500 text-white text-xs px-2 py-1 rounded-full flex items-center gap-1">
                                🎬 {{ recipe.isAiGenerated ? 'AI推荐' : '视频' }}
                            </div>
                        </div>
                    </template>
                    <template #title>
                        <span class="text-base">{{ recipe.name || recipe.title }}</span>
                    </template>
                    <template #content>
                        <div class="flex items-center gap-2 text-sm text-gray-500 flex-wrap">
                            <Tag :value="recipe.difficulty" severity="success" v-if="recipe.difficulty" />
                            <span v-if="recipe.cuisineType">{{ recipe.cuisineType }}</span>
                            <span v-if="recipe.cookTime">⏱{{ recipe.cookTime }}分钟</span>
                            <Tag v-if="recipe.matchCount" :value="`${recipe.matchCount}种食材匹配`" severity="success" />
                        </div>
                        <p class="text-sm text-gray-600 mt-2 line-clamp-2">{{ recipe.description }}</p>
                        <!-- AI生成菜谱显示食材 -->
                        <div v-if="recipe.ingredients" class="flex flex-wrap gap-1 mt-2">
                            <span v-for="(ing, ii) in (recipe.ingredients as any[]).slice(0,5)" :key="ii"
                                  class="text-xs bg-gray-100 rounded px-1.5 py-0.5">{{ ing.name }}</span>
                            <span v-if="recipe.ingredients.length > 5" class="text-xs text-gray-400">+{{ recipe.ingredients.length - 5 }}</span>
                        </div>
                    </template>
                </Card>
            </div>
        </main>
    </div>
</template>
