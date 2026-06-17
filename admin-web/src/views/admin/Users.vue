<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUsers, toggleUserStatus } from '@/api/admin'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Tag from 'primevue/tag'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import ProgressSpinner from 'primevue/progressspinner'

const users = ref<any[]>([])
const loading = ref(true)
const totalRecords = ref(0)
const currentPage = ref(1)
const pageSize = 10
const searchKeyword = ref('')

onMounted(async () => {
    await fetchUsers()
})

async function fetchUsers() {
    loading.value = true
    try {
        const res = await getUsers({ page: currentPage.value, pageSize })
        users.value = res.data.data.records || []
        totalRecords.value = res.data.data.total || 0
    } catch {
        users.value = []
    } finally {
        loading.value = false
    }
}

async function onPageChange(event: any) {
    currentPage.value = event.page + 1
    await fetchUsers()
}

async function doSearch() {
    currentPage.value = 1
    await fetchUsers()
}

async function toggleStatus(userId: number) {
    try {
        await toggleUserStatus(userId)
        await fetchUsers()
    } catch { /* ignore */ }
}

function onRowClick(user: any) {
    // 预留编辑功能
}
</script>

<template>
    <div>
        <div class="flex items-center justify-between mb-6">
            <h1 class="text-2xl font-bold text-gray-800">👥 用户管理</h1>
            <span class="p-input-icon-left">
                <i class="pi pi-search" />
                <InputText v-model="searchKeyword" placeholder="搜索用户..." @keyup.enter="doSearch" />
            </span>
        </div>

        <div v-if="loading" class="flex justify-center py-12"><ProgressSpinner /></div>

        <div v-else-if="!users.length" class="text-center py-12 text-gray-400">
            <i class="pi pi-users text-4xl mb-4" />
            <p>暂无用户数据</p>
        </div>

        <DataTable v-else :value="users" stripedRows paginator :rows="pageSize"
            :totalRecords="totalRecords" @page="onPageChange" lazy>
            <Column field="id" header="ID" sortable />
            <Column field="username" header="用户名" sortable />
            <Column field="nickname" header="昵称" />
            <Column field="email" header="邮箱" />
            <Column field="status" header="状态">
                <template #body="p">
                    <Tag :value="p.data.status === 'active' ? '正常' : '已禁用'"
                        :severity="p.data.status === 'active' ? 'success' : 'danger'" />
                </template>
            </Column>
            <Column field="created" header="注册时间" sortable />
            <Column header="操作">
                <template #body="p">
                    <Button label="禁用" :severity="p.data.status === 'active' ? 'danger' : 'success'"
                        size="small" @click="toggleStatus(p.data.id)" />
                </template>
            </Column>
        </DataTable>
    </div>
</template>
