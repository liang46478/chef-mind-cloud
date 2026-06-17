<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRecipeDetail } from '@/api/recipe'
import { suggestSubstitution } from '@/api/mealPlan'
import type { Recipe } from '@/api/recipe'
import VideoPlayer from '@/components/VideoPlayer.vue'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Chip from 'primevue/chip'
import Tag from 'primevue/tag'
import ProgressSpinner from 'primevue/progressspinner'
import Dialog from 'primevue/dialog'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import axios from 'axios'

const route = useRoute(); const router = useRouter()
const recipe = ref<Recipe | null>(null); const loading = ref(true); const error = ref('')

// 收藏
const isFavorited = ref(false); const favoriteCount = ref(0)
const userId = computed(() => Number(localStorage.getItem('userId')) || 0)

// 评论
const comments = ref<any[]>([]); const commentText = ref(''); const commentsLoading = ref(false)

// 食材替换
const subDialog = ref(false); const subLoading = ref(false); const subIngredient = ref('')
const subResults = ref<any[]>([]); const subReason = ref('缺食材')
const subReasons = [
    { label: '缺食材', value: '缺食材' }, { label: '过敏', value: '过敏不能吃' },
    { label: '不喜欢', value: '不喜欢这个味道' }, { label: '想换口味', value: '想换口味' },
]

onMounted(async () => {
    try {
        const id = Number(route.params.id)
        const res = await getRecipeDetail(id)
        recipe.value = res.data.data
        if (!recipe.value) error.value = '菜谱不存在'
        else {
            await loadFavorite(); await loadComments(); await loadFavoriteCount()
        }
    } catch { error.value = '加载失败' }
    finally { loading.value = false }
})

async function loadFavorite() {
    if (!userId.value) return
    try {
        const r = await axios.get(`/api/recipe/interact/favorite/check?userId=${userId.value}&recipeId=${route.params.id}`)
        isFavorited.value = r.data.data
    } catch { /* ignore */ }
}
async function loadFavoriteCount() {
    try {
        const r = await axios.get(`/api/recipe/interact/favorite/count?recipeId=${route.params.id}`)
        favoriteCount.value = r.data.data || 0
    } catch { /* ignore */ }
}
async function toggleFavorite() {
    if (!userId.value) { router.push('/login'); return }
    try {
        if (isFavorited.value) await axios.delete(`/api/recipe/interact/favorite?userId=${userId.value}&recipeId=${route.params.id}`)
        else await axios.post(`/api/recipe/interact/favorite?userId=${userId.value}&recipeId=${route.params.id}`)
        isFavorited.value = !isFavorited.value
        await loadFavoriteCount()
    } catch { /* ignore */ }
}

async function loadComments() {
    commentsLoading.value = true
    try {
        const r = await axios.get(`/api/recipe/interact/comments?recipeId=${route.params.id}`)
        comments.value = r.data.data || []
    } catch { comments.value = [] }
    finally { commentsLoading.value = false }
}
async function addComment() {
    if (!commentText.value.trim() || !userId.value) return
    try {
        await axios.post('/api/recipe/interact/comment', { userId: userId.value, recipeId: Number(route.params.id), content: commentText.value })
        commentText.value = ''
        await loadComments()
    } catch { /* ignore */ }
}

async function openSubstitution(ingredientName: string) {
    subIngredient.value = ingredientName; subDialog.value = true; subResults.value = []
    if (recipe.value) { subLoading.value = true; try { const res = await suggestSubstitution(recipe.value.title, ingredientName, subReason.value); subResults.value = res.data.data || [] } catch { subResults.value = [] } finally { subLoading.value = false } }
}
async function searchSubstitution() {
    if (!recipe.value || !subIngredient.value) return; subLoading.value = true
    try { const res = await suggestSubstitution(recipe.value.title, subIngredient.value, subReason.value); subResults.value = res.data.data || [] } catch { subResults.value = [] } finally { subLoading.value = false } }
</script>

<template>
    <div class="min-h-screen bg-gray-50">
        <div v-if="loading" class="flex justify-center py-20"><ProgressSpinner /></div>
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
                        <img :src="recipe.imageUrl || 'https://placehold.co/800x400/10b981/white?text=' + recipe.title" :alt="recipe.title" class="w-full h-64 object-cover rounded-t-xl" />
                    </template>
                    <template #title>
                        <div class="flex items-center justify-between">
                            <h1 class="text-3xl font-bold">{{ recipe.title }}</h1>
                            <Tag :value="recipe.difficulty" :severity="recipe.difficulty === '初级' ? 'success' : recipe.difficulty === '中级' ? 'warn' : 'danger'" />
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
                        <!-- 收藏和分享 -->
                        <div class="flex items-center gap-4 mb-4">
                            <Button :icon="isFavorited ? 'pi pi-heart-fill' : 'pi pi-heart'"
                                :label="String(favoriteCount)" :severity="isFavorited ? 'danger' : 'secondary'"
                                text @click="toggleFavorite" />
                        </div>
                        <p v-if="recipe.description" class="text-gray-600 mb-6">{{ recipe.description }}</p>
                        <div v-if="recipe.videoUrl" class="mb-8">
                            <h2 class="text-xl font-semibold mb-3">🎬 做菜视频</h2>
                            <VideoPlayer :videoUrl="recipe.videoUrl" :title="recipe.title" />
                        </div>
                        <div v-if="recipe.tags?.length" class="flex gap-2 mb-6"><Chip v-for="tag in recipe.tags" :key="tag" :label="tag" /></div>

                        <!-- 食材 -->
                        <h2 class="text-xl font-semibold mb-3">🛒 食材清单</h2>
                        <div v-if="recipe.ingredients?.length" class="grid grid-cols-2 md:grid-cols-3 gap-2 mb-6">
                            <div v-for="ing in recipe.ingredients" :key="ing.id || ing.name" class="bg-gray-50 rounded-lg p-3 flex justify-between items-center">
                                <span>{{ ing.name }}</span>
                                <div class="flex items-center gap-2">
                                    <span class="text-gray-500">{{ ing.quantity }}{{ ing.unit }}</span>
                                    <Button icon="pi pi-refresh" text severity="info" size="small" v-tooltip.top="'查找替代食材'" @click="openSubstitution(ing.name)" />
                                </div>
                            </div>
                        </div>
                        <p v-else class="text-gray-400 mb-6">暂无食材信息</p>

                        <!-- 步骤 -->
                        <h2 class="text-xl font-semibold mb-3">📖 烹饪步骤</h2>
                        <div v-if="recipe.steps?.length" class="space-y-4">
                            <div v-for="step in recipe.steps" :key="step.stepNumber" class="flex gap-4 items-start bg-gray-50 rounded-lg p-4">
                                <span class="bg-emerald-500 text-white rounded-full w-8 h-8 flex items-center justify-center shrink-0">{{ step.stepNumber }}</span>
                                <div class="flex-1">
                                    <p class="text-gray-700">{{ step.instruction }}</p>
                                    <div v-if="step.timerMinutes" class="text-sm text-emerald-600 mt-1">⏱ 约{{ step.timerMinutes }}分钟</div>
                                    <div v-if="step.videoUrl" class="mt-2"><VideoPlayer :videoUrl="step.videoUrl" :title="`步骤${step.stepNumber}`" /></div>
                                </div>
                            </div>
                        </div>
                        <p v-else class="text-gray-400">暂无步骤信息</p>

                        <!-- 评论 -->
                        <h2 class="text-xl font-semibold mt-8 mb-3">💬 评论 ({{ comments.length }})</h2>
                        <div v-if="userId" class="flex gap-2 mb-4">
                            <Textarea v-model="commentText" rows="2" class="flex-1" placeholder="写下你的评论..." />
                            <Button label="发表" severity="success" @click="addComment" :disabled="!commentText.trim()" class="self-end" />
                        </div>
                        <div v-else class="mb-4 text-gray-400"><Button label="登录后评论" text severity="secondary" @click="router.push('/login')" /></div>
                        <div v-if="commentsLoading" class="flex justify-center py-4"><ProgressSpinner /></div>
                        <div v-else-if="!comments.length" class="text-gray-400 text-sm">暂无评论，来第一个发言吧</div>
                        <div v-else class="space-y-3">
                            <div v-for="c in comments" :key="c.id" class="bg-gray-50 rounded-lg p-3">
                                <div class="flex items-center gap-2 text-sm text-gray-500 mb-1">
                                    <span class="font-medium text-gray-700">用户 #{{ c.user_id }}</span>
                                    <span>{{ c.created_at?.substring(0, 10) }}</span>
                                </div>
                                <p class="text-gray-700">{{ c.content }}</p>
                            </div>
                        </div>
                    </template>
                </Card>
            </div>
        </template>

        <Dialog v-model:visible="subDialog" :header="`🔄 替代食材: ${subIngredient}`" :modal="true" class="w-lg">
            <div class="flex gap-2 mb-4"><Select v-model="subReason" :options="subReasons" optionLabel="label" optionValue="value" class="flex-1" /><Button label="查询" @click="searchSubstitution" :loading="subLoading" /></div>
            <div v-if="subLoading" class="flex justify-center py-4"><ProgressSpinner /></div>
            <div v-else-if="subResults.length" class="space-y-3">
                <div v-for="(r,i) in subResults" :key="i" class="border rounded-lg p-4 bg-gray-50">
                    <div class="flex items-center gap-2 mb-1"><span class="text-gray-400 line-through">{{ r.original }}</span><i class="pi pi-arrow-right text-emerald-500" /><span class="font-semibold text-emerald-600">{{ r.substitute }}</span></div>
                    <div class="text-sm text-gray-500"><span v-if="r.ratio">替代比例: {{ r.ratio }} | </span><span>{{ r.taste_change }}</span></div>
                </div>
            </div>
            <p v-else class="text-center text-gray-400 py-4">点击「查询」获取替代建议</p>
        </Dialog>
    </div>
</template>
