<script setup lang="ts">
import { ref, onMounted } from 'vue'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Card from 'primevue/card'
import Tag from 'primevue/tag'
import { getUsers } from '@/api/admin'
import axios from 'axios'

const users = ref<any[]>([])
const loading = ref(true)
const selectedUser = ref<any>(null)
const userPlans = ref<any[]>([])
const planItems = ref<any[]>([])
const planLoading = ref(false)

onMounted(async () => {
    loading.value = true
    try {
        const r = await getUsers({ page: 1, pageSize: 50 })
        users.value = r.data.data.records || []
    } catch { users.value = [] }
    finally { loading.value = false }
})

async function selectUser(user: any) {
    selectedUser.value = user
    planLoading.value = true
    try {
        const r = await axios.get(`/api/meal-plan/current?userId=${user.id}`)
        if (r.data.data) {
            userPlans.value = [r.data.data]
            const items = await axios.get(`/api/meal-plan/${r.data.data.id}/items`)
            planItems.value = items.data.data || []
        } else {
            userPlans.value = []
            planItems.value = []
        }
    } catch { userPlans.value = []; planItems.value = [] }
    finally { planLoading.value = false }
}
</script>

<template>
    <div>
        <h1 class="text-2xl font-bold text-gray-800 mb-6">📋 用餐计划管理</h1>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card class="md:col-span-1">
                <template #title>用户列表</template>
                <template #content>
                    <div v-if="loading" class="text-center py-4 text-gray-400">加载中...</div>
                    <div v-else class="space-y-2">
                        <div v-for="u in users" :key="u.id"
                            class="p-2 rounded-lg cursor-pointer"
                            :class="selectedUser?.id === u.id ? 'bg-emerald-50 text-emerald-700 font-medium' : 'hover:bg-gray-50'"
                            @click="selectUser(u)">
                            <span>{{ u.nickname || u.username }}</span>
                            <Tag :value="u.status === 'active' ? '正常' : '禁用'" :severity="u.status === 'active' ? 'success' : 'danger'" class="ml-2" />
                        </div>
                    </div>
                </template>
            </Card>

            <Card class="md:col-span-2">
                <template #title>{{ selectedUser ? `${selectedUser.nickname || selectedUser.username} 的用餐计划` : '选择一个用户' }}</template>
                <template #content>
                    <div v-if="!selectedUser" class="text-center py-8 text-gray-400">请从左侧选择一个用户</div>
                    <div v-else-if="planLoading" class="flex justify-center py-8"><ProgressSpinner /></div>
                    <div v-else-if="!userPlans.length" class="text-center py-8 text-gray-400">该用户暂无用餐计划</div>
                    <template v-else>
                        <div v-for="plan in userPlans" :key="plan.id" class="mb-4">
                            <div class="grid grid-cols-3 gap-4 mb-4">
                                <Card><template #content><p class="text-xs text-gray-500">计划</p><p class="font-semibold">{{ plan.name }}</p></template></Card>
                                <Card><template #content><p class="text-xs text-gray-500">日期</p><p class="font-semibold">{{ plan.startDate }} ~ {{ plan.endDate }}</p></template></Card>
                                <Card><template #content><p class="text-xs text-gray-500">状态</p><p class="font-semibold text-emerald-600">{{ plan.status }}</p></template></Card>
                            </div>
                        </div>
                        <DataTable :value="planItems" stripedRows>
                            <Column field="date" header="日期" />
                            <Column field="mealType" header="餐别"><template #body="p">{{ {breakfast:'早餐', lunch:'午餐', dinner:'晚餐'}[p.data.mealType] || p.data.mealType }}</template></Column>
                            <Column field="recipeName" header="菜品" />
                            <Column field="status" header="状态"><template #body="p">{{ {pending:'待完成', completed:'已完成'}[p.data.status] || p.data.status }}</template></Column>
                        </DataTable>
                    </template>
                </template>
            </Card>
        </div>
    </div>
</template>
