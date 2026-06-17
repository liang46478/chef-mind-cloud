<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { generateMealPlan, getMealPlan } from '@/api/mealPlan'
import type { MealPlan } from '@/api/mealPlan'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'

const router = useRouter()
const planType = ref('weekly')
const planTypes = [
    { label: '本周计划', value: 'weekly' },
    { label: '下周计划', value: 'next-week' },
]

const loading = ref(false)
const generating = ref(false)
const error = ref('')
const mealPlan = ref<MealPlan | null>(null)
const mealPlanItems = ref<any[]>([])

onMounted(async () => {
    await fetchPlan()
})

async function fetchPlan() {
    loading.value = true
    error.value = ''
    try {
        const res = await getMealPlan(planType.value)
        const plan = res.data.data
        if (plan) {
            mealPlan.value = plan
            // 转换 items 为表格格式
            mealPlanItems.value = plan.items || []
        } else {
            mealPlan.value = null
            mealPlanItems.value = []
        }
    } catch {
        mealPlan.value = null
        mealPlanItems.value = []
    } finally {
        loading.value = false
    }
}

async function generatePlan() {
    generating.value = true
    error.value = ''
    try {
        const res = await generateMealPlan()
        mealPlan.value = res.data.data
        mealPlanItems.value = res.data.data?.items || []
    } catch (e: any) {
        error.value = e.response?.data?.message || '生成失败，请检查 DeepSeek API 配置'
    } finally {
        generating.value = false
    }
}
</script>

<template>
    <div class="min-h-screen bg-gray-50">
        <header class="bg-white shadow-sm">
            <div class="max-w-5xl mx-auto px-4 py-4 flex items-center justify-between">
                <Button label="← 返回首页" text severity="secondary" @click="router.push('/')" />
                <h1 class="text-xl font-semibold text-gray-800">📋 我的用餐计划</h1>
                <div></div>
            </div>
        </header>

        <main class="max-w-5xl mx-auto px-4 py-8">
            <Card class="mb-6">
                <template #content>
                    <div class="flex items-center justify-between">
                        <div class="flex items-center gap-4">
                            <Dropdown v-model="planType" :options="planTypes" optionLabel="label" optionValue="value" @change="fetchPlan" />
                        </div>
                        <Button label="🤖 AI 生成计划" severity="success" :loading="generating" @click="generatePlan" />
                    </div>
                </template>
            </Card>

            <Message v-if="error" severity="warn" class="mb-4">{{ error }}</Message>

            <div v-if="loading" class="flex justify-center py-12"><ProgressSpinner /></div>

            <Card v-else-if="!mealPlan">
                <template #content>
                    <div class="text-center py-8 text-gray-400">
                        <i class="pi pi-calendar-plus text-4xl mb-4" />
                        <p class="text-lg">暂无用餐计划</p>
                        <p class="text-sm mt-2">点击上方「AI 生成计划」按钮开始</p>
                    </div>
                </template>
            </Card>

            <template v-else>
                <Card class="mb-4">
                    <template #header>
                        <div class="px-6 pt-4">
                            <h2 class="text-lg font-semibold">{{ mealPlan.name || '本周餐食计划' }}</h2>
                            <p class="text-sm text-gray-500">{{ mealPlan.startDate }} ~ {{ mealPlan.endDate }}</p>
                        </div>
                    </template>
                </Card>

                <Card v-if="mealPlanItems.length">
                    <template #header><div class="px-6 pt-4"><h2 class="text-lg font-semibold">每日安排</h2></div></template>
                    <template #content>
                        <DataTable :value="mealPlanItems" stripedRows>
                            <Column field="date" header="日期" />
                            <Column field="mealType" header="餐别">
                                <template #body="p">
                                    <span :class="p.data.mealType === 'breakfast' ? 'text-amber-500' : p.data.mealType === 'lunch' ? 'text-emerald-500' : 'text-blue-500'">
                                        {{ {breakfast:'早餐', lunch:'午餐', dinner:'晚餐'}[p.data.mealType] || p.data.mealType }}
                                    </span>
                                </template>
                            </Column>
                            <Column field="recipeName" header="菜品" />
                            <Column field="status" header="状态">
                                <template #body="p">
                                    <span :class="p.data.status === 'completed' ? 'text-emerald-500' : 'text-gray-400'">
                                        {{ {pending:'待完成', completed:'已完成'}[p.data.status] || p.data.status }}
                                    </span>
                                </template>
                            </Column>
                        </DataTable>
                    </template>
                </Card>
            </template>
        </main>
    </div>
</template>
