<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMealRecords, addMealRecord } from '@/api/user'
import type { MealRecord } from '@/api/user'
import Card from 'primevue/card'
import Button from 'primevue/button'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Rating from 'primevue/rating'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import Select from 'primevue/select'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'

const router = useRouter()
const records = ref<MealRecord[]>([])
const loading = ref(true)
const error = ref('')

const showDialog = ref(false)
const newRecord = ref({ mealType: '', recipeName: '', recipeId: 0, rating: 3, feedback: '' })

const mealTypeOptions = [
    { label: '早餐', value: 'breakfast' },
    { label: '午餐', value: 'lunch' },
    { label: '晚餐', value: 'dinner' },
    { label: '加餐', value: 'snack' },
]

onMounted(async () => {
    await fetchRecords()
})

async function fetchRecords() {
    loading.value = true
    error.value = ''
    try {
        const userId = localStorage.getItem('userId')
        const res = await getMealRecords(Number(userId) || 1)
        records.value = res.data.data || []
    } catch {
        records.value = []
        error.value = '加载失败，请确认已登录'
    } finally {
        loading.value = false
    }
}

function openAddDialog() {
    newRecord.value = { mealType: '', recipeName: '', recipeId: 0, rating: 3, feedback: '' }
    showDialog.value = true
}

async function saveRecord() {
    if (!newRecord.value.mealType || !newRecord.value.recipeName) return
    try {
        const userId = Number(localStorage.getItem('userId')) || 1
        await addMealRecord({
            userId,
            mealType: newRecord.value.mealType,
            recipeName: newRecord.value.recipeName,
            rating: newRecord.value.rating,
            feedback: newRecord.value.feedback,
        } as any)
        showDialog.value = false
        await fetchRecords()
    } catch (e: any) {
        error.value = '保存失败: ' + (e.response?.data?.message || '请重试')
    }
}
</script>

<template>
    <div class="min-h-screen bg-gray-50">
        <header class="bg-white shadow-sm">
            <div class="max-w-5xl mx-auto px-4 py-4 flex items-center justify-between">
                <Button label="← 返回首页" text severity="secondary" @click="router.push('/')" />
                <h1 class="text-xl font-semibold text-gray-800">📝 我的就餐记录</h1>
                <Button label="+ 记录就餐" @click="openAddDialog" />
            </div>
        </header>

        <main class="max-w-5xl mx-auto px-4 py-8">
            <Message v-if="error" severity="warn" class="mb-4">{{ error }}</Message>

            <div v-if="loading" class="flex justify-center py-12"><ProgressSpinner /></div>

            <Card v-else-if="!records.length">
                <template #content>
                    <div class="text-center py-8 text-gray-400">
                        <i class="pi pi-book text-4xl mb-4" />
                        <p class="text-lg">暂无就餐记录</p>
                        <p class="text-sm mt-2">点击右上角「记录就餐」开始记录</p>
                    </div>
                </template>
            </Card>

            <Card v-else>
                <template #content>
                    <DataTable :value="records" stripedRows paginator :rows="10">
                        <Column field="ateAt" header="日期" />
                        <Column field="mealType" header="餐别">
                            <template #body="p">
                                {{ {breakfast:'早餐', lunch:'午餐', dinner:'晚餐', snack:'加餐'}[p.data.mealType] || p.data.mealType }}
                            </template>
                        </Column>
                        <Column field="recipeName" header="菜品" />
                        <Column field="rating" header="评分">
                            <template #body="p">
                                <Rating :modelValue="p.data.rating" readonly :cancel="false" />
                            </template>
                        </Column>
                        <Column field="feedback" header="评价" />
                    </DataTable>
                </template>
            </Card>
        </main>

        <Dialog v-model:visible="showDialog" header="记录就餐" :modal="true" class="w-96">
            <div class="flex flex-col gap-4">
                <div>
                    <label class="block text-sm font-medium mb-1">餐别</label>
                    <Select v-model="newRecord.mealType" :options="mealTypeOptions" optionLabel="label" optionValue="value" class="w-full" placeholder="选择餐别" />
                </div>
                <div>
                    <label class="block text-sm font-medium mb-1">菜品名称</label>
                    <InputText v-model="newRecord.recipeName" placeholder="如：宫保鸡丁" class="w-full" />
                </div>
                <div>
                    <label class="block text-sm font-medium mb-1">评分</label>
                    <Rating v-model="newRecord.rating" :cancel="false" />
                </div>
                <div>
                    <label class="block text-sm font-medium mb-1">评价</label>
                    <Textarea v-model="newRecord.feedback" rows="3" class="w-full" />
                </div>
                <Button label="保存" @click="saveRecord" />
            </div>
        </Dialog>
    </div>
</template>
