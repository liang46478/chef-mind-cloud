<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getAllIngredients } from '@/api/recipe'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Chip from 'primevue/chip'

const router = useRouter()
const allIngredients = ref<any[]>([])
const selectedIds = ref<number[]>([])
const loading = ref(false)
const searchText = ref('')

const filteredIngredients = computed(() => {
    if (!searchText.value.trim()) return allIngredients.value
    const q = searchText.value.trim().toLowerCase()
    return allIngredients.value.filter(i => i.name.toLowerCase().includes(q))
})

const categorized = computed(() => {
    const map: Record<string, any[]> = {}
    filteredIngredients.value.forEach(ing => {
        const cat = ing.category || '其他'
        if (!map[cat]) map[cat] = []
        map[cat].push(ing)
    })
    return map
})

const selectedNames = computed(() =>
    allIngredients.value.filter(i => selectedIds.value.includes(i.id)).map(i => i.name)
)

onMounted(async () => {
    loading.value = true
    try {
        const res = await getAllIngredients()
        allIngredients.value = res.data.data || []
    } catch { allIngredients.value = [] }
    finally { loading.value = false }
})

function search() {
    if (selectedIds.value.length === 0) return
    // 同时传递IDs和Names，让搜索结果页可以用AI生成
    router.push(`/search?ingredients=${selectedIds.value.join(',')}&names=${selectedNames.value.join(',')}`)
}

function selectCategory(cat: string) {
    const ids = allIngredients.value.filter(i => i.category === cat).map(i => i.id)
    const allSelected = ids.every(id => selectedIds.value.includes(id))
    if (allSelected) {
        selectedIds.value = selectedIds.value.filter(id => !ids.includes(id))
    } else {
        ids.forEach(id => { if (!selectedIds.value.includes(id)) selectedIds.value.push(id) })
    }
}

const categoryKeys = computed(() => Object.keys(categorized.value))

function clearAll() { selectedIds.value = [] }
</script>

<template>
    <div class="bg-white rounded-xl shadow-sm p-4">
        <div class="flex items-center justify-between mb-3">
            <h3 class="text-lg font-semibold text-gray-800">🍅 选择食材做菜</h3>
            <span class="text-sm text-gray-400">{{ allIngredients.length }}种食材可用</span>
        </div>

        <!-- 搜索过滤 -->
        <div class="mb-3">
            <span class="p-input-icon-left w-full">
                <i class="pi pi-search" />
                <InputText v-model="searchText" placeholder="搜索食材..." class="w-full" />
            </span>
        </div>

        <div v-if="loading" class="text-center py-4 text-gray-400">加载食材中...</div>

        <template v-else>
            <!-- 已选食材 -->
            <div v-if="selectedIds.length" class="flex flex-wrap gap-1 mb-3 p-2 bg-emerald-50 rounded-lg">
                <Chip v-for="name in selectedNames" :key="name"
                    :label="name" removable @remove="selectedIds = selectedIds.filter((_, i) => selectedNames.indexOf(name) !== i)"
                    class="bg-emerald-100 text-emerald-700" />
                <span class="text-xs text-emerald-500 self-center ml-1">{{ selectedIds.length }}种</span>
            </div>

            <!-- 食材分类 -->
            <div class="max-h-64 overflow-y-auto space-y-2">
                <div v-for="cat in categoryKeys" :key="cat">
                    <div class="flex items-center justify-between text-xs text-gray-500 mb-1">
                        <span class="font-medium">{{ cat }}</span>
                        <button class="text-emerald-500 hover:underline" @click="selectCategory(cat)">全选</button>
                    </div>
                    <div class="flex flex-wrap gap-1">
                        <Chip v-for="ing in categorized[cat]" :key="ing.id"
                            :label="ing.name"
                            :class="selectedIds.includes(ing.id) ? 'bg-emerald-100 text-emerald-700 border-emerald-400' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
                            class="cursor-pointer text-xs transition-colors border"
                            @click="selectedIds.includes(ing.id) ? selectedIds = selectedIds.filter(id => id !== ing.id) : selectedIds.push(ing.id)" />
                    </div>
                </div>
            </div>

            <!-- 操作按钮 -->
            <div v-if="selectedIds.length" class="flex items-center gap-3 pt-3 mt-3 border-t">
                <div class="text-sm text-emerald-600 font-medium flex-1">已选 {{ selectedIds.length }} 种</div>
                <Button label="清空" text severity="secondary" size="small" @click="clearAll" />
                <Button label="搜菜谱" severity="success" size="small" @click="search" />
            </div>
            <p v-else class="text-sm text-gray-400 pt-2">点击食材标签选择，支持多选 + 搜索过滤</p>
        </template>
    </div>
</template>
