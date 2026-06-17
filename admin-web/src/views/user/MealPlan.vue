<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { generateMealPlan, getMealPlan, adjustMealPlan, analyzeNutrition, getShoppingList } from '@/api/mealPlan'
import type { MealPlan } from '@/api/mealPlan'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'
import Dialog from 'primevue/dialog'

const router = useRouter()
const planType = ref('weekly')
const planTypes = [
    { label: '本周计划', value: 'weekly' },
    { label: '下周计划', value: 'next-week' },
]

const loading = ref(false)
const generating = ref(false)
const adjusting = ref(false)
const error = ref('')
const mealPlan = ref<MealPlan | null>(null)
const mealPlanItems = ref<any[]>([])

// 营养分析
const nutritionDialog = ref(false)
const nutritionData = ref<any>(null)
const nutritionLoading = ref(false)

// 采购清单
const shoppingDialog = ref(false)
const shoppingList = ref<any[]>([])
const shoppingLoading = ref(false)

onMounted(async () => { await fetchPlan() })

async function fetchPlan() {
    loading.value = true; error.value = ''
    try {
        const res = await getMealPlan(planType.value)
        const plan = res.data.data
        mealPlan.value = plan
        mealPlanItems.value = plan?.items || []
    } catch { mealPlan.value = null; mealPlanItems.value = [] }
    finally { loading.value = false }
}

async function generatePlan() {
    generating.value = true; error.value = ''
    try {
        const res = await generateMealPlan()
        mealPlan.value = res.data.data
        mealPlanItems.value = res.data.data?.items || []
    } catch (e: any) { error.value = e.response?.data?.message || '生成失败' }
    finally { generating.value = false }
}

async function adjustPlan() {
    adjusting.value = true; error.value = ''
    try {
        const history = mealPlanItems.value.map((i: any) => `${i.date} ${i.mealType}: ${i.recipeName}`).join('\n')
        const res = await adjustMealPlan(
            localStorage.getItem('cuisinePreference') || '', localStorage.getItem('allergies') || '',
            localStorage.getItem('healthGoal') || '', history)
        if (res.data.data) {
            mealPlanItems.value = res.data.data.map((d: any, idx: number) => ({
                id: idx, date: d.date || '', mealType: 'breakfast', recipeName: d.lunch || '', notes: d.notes || '', status: 'pending' }))
        }
    } catch (e: any) { error.value = e.response?.data?.message || '调整失败' }
    finally { adjusting.value = false }
}

async function openNutrition() {
    if (!mealPlanItems.value.length) return
    nutritionLoading.value = true; nutritionDialog.value = true
    try {
        const res = await analyzeNutrition(JSON.stringify(mealPlanItems.value))
        nutritionData.value = res.data.data
    } catch { nutritionData.value = { assessment: '分析失败', suggestions: ['请稍后重试'] } }
    finally { nutritionLoading.value = false }
}

async function openShoppingList() {
    shoppingLoading.value = true; shoppingDialog.value = true
    try {
        const userId = Number(localStorage.getItem('userId')) || 1
        const res = await getShoppingList(userId, planType.value)
        shoppingList.value = res.data.data || []
    } catch { shoppingList.value = [] }
    finally { shoppingLoading.value = false }
}
</script>

<template>
    <div class="min-h-screen bg-gray-50">
        <header class="bg-white shadow-sm">
            <div class="max-w-6xl mx-auto px-4 py-4 flex items-center justify-between">
                <Button label="← 返回首页" text severity="secondary" @click="router.push('/')" />
                <h1 class="text-xl font-semibold text-gray-800">📋 我的用餐计划</h1>
                <div></div>
            </div>
        </header>

        <main class="max-w-6xl mx-auto px-4 py-8">
            <Card class="mb-6">
                <template #content>
                    <div class="flex items-center justify-between flex-wrap gap-3">
                        <Dropdown v-model="planType" :options="planTypes" optionLabel="label" optionValue="value" @change="fetchPlan" />
                        <div class="flex gap-2">
                            <Button label="🤖 AI 生成" severity="success" :loading="generating" @click="generatePlan" />
                            <Button label="📊 营养分析" severity="info" @click="openNutrition" :disabled="!mealPlanItems.length" />
                            <Button label="🛒 采购清单" severity="help" @click="openShoppingList" :disabled="!mealPlanItems.length" />
                        </div>
                    </div>
                </template>
            </Card>

            <Message v-if="error" severity="warn" class="mb-4">{{ error }}</Message>

            <div v-if="loading" class="flex justify-center py-12"><ProgressSpinner /></div>
            <Card v-else-if="!mealPlan" class="text-center py-8 text-gray-400">
                <i class="pi pi-calendar-plus text-4xl mb-4" />
                <p class="text-lg">暂无用餐计划</p>
                <p class="text-sm mt-2">点击上方「AI 生成」按钮开始</p>
            </Card>

            <template v-else>
                <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
                    <Card><template #content><p class="text-sm text-gray-500">计划名称</p><p class="font-semibold">{{ mealPlan.name || '本周餐食' }}</p></template></Card>
                    <Card><template #content><p class="text-sm text-gray-500">开始日期</p><p class="font-semibold">{{ mealPlan.startDate }}</p></template></Card>
                    <Card><template #content><p class="text-sm text-gray-500">结束日期</p><p class="font-semibold">{{ mealPlan.endDate }}</p></template></Card>
                    <Card><template #content><p class="text-sm text-gray-500">状态</p><p class="font-semibold text-emerald-600">{{ mealPlan.status === 'active' ? '进行中' : mealPlan.status }}</p></template></Card>
                </div>

                <Card v-if="mealPlanItems.length">
                    <template #header>
                        <div class="px-6 pt-4 flex items-center justify-between">
                            <h2 class="text-lg font-semibold">每日安排</h2>
                            <Button label="🔄 根据记录调整" severity="warning" :loading="adjusting" @click="adjustPlan" size="small" />
                        </div>
                    </template>
                    <template #content>
                        <DataTable :value="mealPlanItems" stripedRows>
                            <Column field="date" header="日期" />
                            <Column field="mealType" header="餐别">
                                <template #body="p">
                                    <span :class="p.data.mealType === 'breakfast' ? 'text-amber-500' : p.data.mealType === 'lunch' ? 'text-emerald-500' : 'text-blue-500'">
                                        {{ {breakfast:'🍳 早餐', lunch:'🍚 午餐', dinner:'🍲 晚餐'}[p.data.mealType] || p.data.mealType }}
                                    </span>
                                </template>
                            </Column>
                            <Column field="recipeName" header="菜品" />
                            <Column field="notes" header="备注" />
                            <Column field="status" header="状态">
                                <template #body="p">
                                    <span :class="p.data.status === 'completed' ? 'text-emerald-500' : 'text-gray-400'">{{ {pending:'待完成', completed:'✅ 已完成'}[p.data.status] || p.data.status }}</span>
                                </template>
                            </Column>
                        </DataTable>
                    </template>
                </Card>
            </template>
        </main>

        <!-- 营养分析弹窗 -->
        <Dialog v-model:visible="nutritionDialog" header="📊 营养分析" :modal="true" class="w-lg">
            <div v-if="nutritionLoading" class="flex justify-center py-8"><ProgressSpinner /></div>
            <div v-else-if="nutritionData" class="flex flex-col gap-4">
                <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
                    <Card><template #content><p class="text-xs text-gray-500">总热量</p><p class="text-xl font-bold">{{ nutritionData.totalCalories || '-' }}</p></template></Card>
                    <Card><template #content><p class="text-xs text-gray-500">蛋白质</p><p class="text-xl font-bold">{{ nutritionData.protein || '-' }}</p></template></Card>
                    <Card><template #content><p class="text-xs text-gray-500">碳水</p><p class="text-xl font-bold">{{ nutritionData.carbs || '-' }}</p></template></Card>
                    <Card><template #content><p class="text-xs text-gray-500">脂肪</p><p class="text-xl font-bold">{{ nutritionData.fat || '-' }}</p></template></Card>
                </div>
                <Card><template #content><p class="text-sm font-medium">总体评价</p><p class="text-gray-600">{{ nutritionData.assessment || '-' }}</p></template></Card>
                <Card v-if="nutritionData.suggestions?.length"><template #content><p class="text-sm font-medium mb-2">改进建议</p><ul class="list-disc pl-4 text-gray-600 space-y-1"><li v-for="(s,i) in nutritionData.suggestions" :key="i">{{ s }}</li></ul></template></Card>
            </div>
        </Dialog>

        <!-- 采购清单弹窗 -->
        <Dialog v-model:visible="shoppingDialog" header="🛒 采购清单" :modal="true" class="w-lg">
            <div v-if="shoppingLoading" class="flex justify-center py-8"><ProgressSpinner /></div>
            <div v-else-if="!shoppingList.length" class="text-center text-gray-400 py-4">暂无数据</div>
            <div v-else class="flex flex-col gap-4">
                <div v-for="(group, gi) in shoppingList" :key="gi">
                    <h3 class="font-semibold text-gray-700 mb-2">{{ group.category }}</h3>
                    <div class="flex flex-wrap gap-2">
                        <span v-for="(item, ii) in group.items" :key="ii" class="bg-gray-100 rounded-full px-3 py-1 text-sm text-gray-700">{{ item }}</span>
                    </div>
                </div>
            </div>
        </Dialog>
    </div>
</template>
