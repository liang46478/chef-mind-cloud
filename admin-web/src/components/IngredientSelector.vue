<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getAllIngredients } from '@/api/recipe'
import MultiSelect from 'primevue/multiselect'
import Button from 'primevue/button'
import Chip from 'primevue/chip'

const router = useRouter()
const allIngredients = ref<any[]>([])
const selectedIds = ref<number[]>([])
const loading = ref(false)

const categorized = computed(() => {
    const map: Record<string, any[]> = {}
    allIngredients.value.forEach(ing => {
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
    router.push(`/search?ingredients=${selectedIds.value.join(',')}`)
}

function clearAll() {
    selectedIds.value = []
}
</script>

<template>
    <div class="bg-white rounded-xl shadow-sm p-4">
        <h3 class="text-lg font-semibold text-gray-800 mb-3">🍅 选择食材做菜</h3>

        <div class="flex flex-wrap gap-2 mb-3">
            <div v-for="(ings, cat) in categorized" :key="cat" class="flex-1 min-w-[120px]">
                <h4 class="text-xs font-medium text-gray-500 mb-1">{{ cat }}</h4>
                <div class="flex flex-wrap gap-1">
                    <Chip v-for="ing in ings" :key="ing.id"
                        :label="ing.name"
                        :class="selectedIds.includes(ing.id) ? 'bg-emerald-100 text-emerald-700 border-emerald-400' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
                        class="cursor-pointer text-sm transition-colors border"
                        @click="selectedIds.includes(ing.id) ? selectedIds = selectedIds.filter(id => id !== ing.id) : selectedIds.push(ing.id)" />
                </div>
            </div>
        </div>

        <div v-if="selectedIds.length" class="flex items-center gap-3 pt-3 border-t">
            <div class="flex flex-wrap gap-1 flex-1">
                <span v-for="name in selectedNames" :key="name"
                    class="inline-flex items-center gap-1 bg-emerald-50 text-emerald-700 text-sm px-2 py-0.5 rounded-full">
                    {{ name }}
                </span>
            </div>
            <Button label="清空" text severity="secondary" size="small" @click="clearAll" />
            <Button label="搜菜谱" severity="success" size="small" @click="search" />
        </div>
        <p v-else class="text-sm text-gray-400 pt-2">点击上方食材标签即可选择，多选后点击「搜菜谱」</p>
    </div>
</template>
