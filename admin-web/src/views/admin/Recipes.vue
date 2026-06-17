<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getRecipes, updateRecipeStatus } from '@/api/admin'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Tag from 'primevue/tag'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import ProgressSpinner from 'primevue/progressspinner'
import ConfirmDialog from 'primevue/confirmdialog'
import { useConfirm } from 'primevue/useconfirm'
import axios from 'axios'

const confirm = useConfirm()
const recipes = ref<any[]>([])
const loading = ref(true)
const totalRecords = ref(0)
const currentPage = ref(1)
const pageSize = 10
const showDialog = ref(false)
const editRecipe = ref<any>({ title: '', category: '', difficulty: '初级', cookTime: 30, status: 'draft' })
const isEditing = ref(false)

const difficultyOptions = [
    { label: '初级', value: '初级' },
    { label: '中级', value: '中级' },
    { label: '高级', value: '高级' },
]

onMounted(async () => {
    await fetchRecipes()
})

async function fetchRecipes() {
    loading.value = true
    try {
        const res = await getRecipes({ page: currentPage.value, pageSize })
        recipes.value = res.data.data.records || []
        totalRecords.value = res.data.data.total || 0
    } catch {
        recipes.value = []
    } finally {
        loading.value = false
    }
}

async function onPageChange(event: any) {
    currentPage.value = event.page + 1
    await fetchRecipes()
}

function openNew() {
    isEditing.value = false
    editRecipe.value = { title: '', category: '家常菜', difficulty: '初级', cookTime: 30, status: 'draft' }
    showDialog.value = true
}

function openEdit(recipe: any) {
    isEditing.value = true
    editRecipe.value = { ...recipe }
    showDialog.value = true
}

async function saveRecipe() {
    try {
        if (isEditing.value) {
            await axios.put(`/api/admin/recipes/${editRecipe.value.id}/status`, { status: editRecipe.value.status })
        } else {
            await axios.post('/api/admin/recipes', editRecipe.value)
        }
        showDialog.value = false
        await fetchRecipes()
    } catch { /* ignore */ }
}

function confirmDelete(recipeId: number) {
    confirm.require({
        message: '确定要删除这个菜品吗？',
        header: '确认删除',
        icon: 'pi pi-exclamation-triangle',
        accept: async () => {
            try {
                await axios.delete(`/api/admin/recipes/${recipeId}`)
                await fetchRecipes()
            } catch { /* ignore */ }
        }
    })
}

async function toggleStatus(recipe: any) {
    const newStatus = recipe.status === 'published' ? 'draft' : 'published'
    try {
        await updateRecipeStatus(recipe.id, newStatus)
        await fetchRecipes()
    } catch { /* ignore */ }
}
</script>

<template>
    <div>
        <div class="flex items-center justify-between mb-6">
            <h1 class="text-2xl font-bold text-gray-800">🍳 菜品管理</h1>
            <Button label="+ 新增菜品" @click="openNew" />
        </div>

        <div v-if="loading" class="flex justify-center py-12"><ProgressSpinner /></div>

        <DataTable v-else :value="recipes" stripedRows paginator :rows="pageSize"
            :totalRecords="totalRecords" @page="onPageChange" lazy>
            <Column field="id" header="ID" sortable />
            <Column field="title" header="菜品名称" sortable />
            <Column field="category" header="分类" />
            <Column field="difficulty" header="难度" />
            <Column field="cookTime" header="烹饪时间(分钟)" sortable />
            <Column field="status" header="状态">
                <template #body="p">
                    <Tag :value="p.data.status === 'published' ? '已发布' : '草稿'"
                        :severity="p.data.status === 'published' ? 'success' : 'warn'" />
                </template>
            </Column>
            <Column header="操作">
                <template #body="p">
                    <Button label="编辑" text severity="secondary" size="small" @click="openEdit(p.data)" />
                    <Button :label="p.data.status === 'published' ? '下架' : '发布'" text size="small"
                        :severity="p.data.status === 'published' ? 'warn' : 'success'" @click="toggleStatus(p.data)" />
                    <Button label="删除" text severity="danger" size="small" @click="confirmDelete(p.data.id)" />
                </template>
            </Column>
        </DataTable>

        <Dialog v-model:visible="showDialog" :header="isEditing ? '编辑菜品' : '新增菜品'" :modal="true" class="w-lg">
            <div class="flex flex-col gap-4">
                <InputText v-model="editRecipe.title" placeholder="菜品名称" />
                <InputText v-model="editRecipe.category" placeholder="分类" />
                <Select v-model="editRecipe.difficulty" :options="difficultyOptions" optionLabel="label" optionValue="value" placeholder="难度" />
                <InputText v-model="editRecipe.cookTime" placeholder="烹饪时间(分钟)" />
                <Button label="保存" @click="saveRecipe" />
            </div>
        </Dialog>
    </div>
</template>
