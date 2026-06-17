<script setup lang="ts">
import { ref } from 'vue'
import { RouterView, useRouter } from 'vue-router'
import Menubar from 'primevue/menubar'

const router = useRouter()

const menuItems = ref([
    { label: '数据看板', icon: 'pi pi-chart-bar', command: () => router.push('/admin/dashboard') },
    { label: '用户管理', icon: 'pi pi-users', command: () => router.push('/admin/users') },
    { label: '菜品管理', icon: 'pi pi-book', command: () => router.push('/admin/recipes') },
    { label: '食材管理', icon: 'pi pi-box', command: () => router.push('/admin/ingredients') },
    { label: '提示词管理', icon: 'pi pi-cog', command: () => router.push('/admin/prompts') },
    { label: '推荐配置', icon: 'pi pi-thumbs-up', command: () => router.push('/admin/recommend-config') },
])
</script>

<template>
    <div class="min-h-screen bg-gray-50">
        <Menubar :model="menuItems" class="rounded-none shadow-sm">
            <template #start>
                <span class="text-xl font-bold text-emerald-600 px-4">ChefMind 管理后台</span>
            </template>
            <template #end>
                <span class="text-sm text-gray-500 px-4">管理员</span>
            </template>
        </Menubar>

        <main class="p-6">
            <RouterView />
        </main>
    </div>
</template>
