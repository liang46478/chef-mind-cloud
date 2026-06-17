<script setup lang="ts">
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'

const ingredients = ref([
    { id: 1, name: '鸡胸肉', category: '肉类', unit: '克', calories: 167 },
    { id: 2, name: '花生米', category: '干货', unit: '克', calories: 567 },
    { id: 3, name: '番茄', category: '蔬菜', unit: '个', calories: 18 },
])

import { ref } from 'vue'
</script>

<template>
    <div>
        <div class="flex items-center justify-between mb-6">
            <h1 class="text-2xl font-bold text-gray-800">📦 食材管理</h1>
            <Button label="+ 新增食材" />
        </div>

        <DataTable :value="ingredients" stripedRows>
            <Column field="id" header="ID" />
            <Column field="name" header="食材名称" sortable />
            <Column field="category" header="分类" sortable />
            <Column field="unit" header="单位" />
            <Column field="calories" header="热量(千卡/100g)" sortable />
            <Column header="操作">
                <template #body>
                    <Button label="编辑" text severity="secondary" />
                    <Button label="删除" text severity="danger" />
                </template>
            </Column>
        </DataTable>
    </div>
</template>
