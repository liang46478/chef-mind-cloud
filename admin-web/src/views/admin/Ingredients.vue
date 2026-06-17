<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getIngredients } from '@/api/admin'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import InputGroup from 'primevue/inputgroup'
import ProgressSpinner from 'primevue/progressspinner'
import axios from 'axios'

const ingredients = ref<any[]>([])
const loading = ref(true)
const totalRecords = ref(0)
const currentPage = ref(1)
const pageSize = 10
const showDialog = ref(false)
const editIng = ref<any>({ name: '', category: '', unit: '克', calories: 0 })
const isEditing = ref(false)

onMounted(async () => {
    await fetchIngredients()
})

async function fetchIngredients() {
    loading.value = true
    try {
        const res = await getIngredients({ page: currentPage.value, pageSize })
        ingredients.value = res.data.data.records || []
        totalRecords.value = res.data.data.total || 0
    } catch {
        ingredients.value = []
    } finally {
        loading.value = false
    }
}

async function onPageChange(event: any) {
    currentPage.value = event.page + 1
    await fetchIngredients()
}

function openNew() {
    isEditing.value = false
    editIng.value = { name: '', category: '', unit: '克', calories: 0 }
    showDialog.value = true
}

function openEdit(ing: any) {
    isEditing.value = true
    editIng.value = { ...ing }
    showDialog.value = true
}

async function saveIngredient() {
    try {
        if (isEditing.value) {
            await axios.put(`/api/admin/ingredients/${editIng.value.id}`, editIng.value)
        } else {
            await axios.post('/api/admin/ingredients', editIng.value)
        }
        showDialog.value = false
        await fetchIngredients()
    } catch { /* ignore */ }
}

async function confirmDelete(id: number) {
    try {
        await axios.delete(`/api/admin/ingredients/${id}`)
        await fetchIngredients()
    } catch { /* ignore */ }
}
</script>

<template>
    <div>
        <div class="flex items-center justify-between mb-6">
            <h1 class="text-2xl font-bold text-gray-800">📦 食材管理</h1>
            <Button label="+ 新增食材" @click="openNew" />
        </div>

        <div v-if="loading" class="flex justify-center py-12"><ProgressSpinner /></div>

        <DataTable v-else :value="ingredients" stripedRows paginator :rows="pageSize"
            :totalRecords="totalRecords" @page="onPageChange" lazy>
            <Column field="id" header="ID" />
            <Column field="name" header="食材名称" sortable />
            <Column field="category" header="分类" sortable />
            <Column field="unit" header="单位" />
            <Column field="calories" header="热量(千卡/100g)" sortable />
            <Column header="操作">
                <template #body="p">
                    <Button label="编辑" text severity="secondary" size="small" @click="openEdit(p.data)" />
                    <Button label="删除" text severity="danger" size="small" @click="confirmDelete(p.data.id)" />
                </template>
            </Column>
        </DataTable>

        <Dialog v-model:visible="showDialog" :header="isEditing ? '编辑食材' : '新增食材'" :modal="true" class="w-md">
            <div class="flex flex-col gap-4">
                <InputText v-model="editIng.name" placeholder="食材名称" />
                <InputText v-model="editIng.category" placeholder="分类(如：蔬菜、肉类)" />
                <InputText v-model="editIng.unit" placeholder="单位" />
                <InputNumber v-model="editIng.calories" placeholder="热量(千卡/100g)" class="w-full" />
                <Button label="保存" @click="saveIngredient" />
            </div>
        </Dialog>
    </div>
</template>
