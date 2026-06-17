<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPrompt, savePrompt } from '@/api/admin'
import Card from 'primevue/card'
import Textarea from 'primevue/textarea'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'
import Message from 'primevue/message'

const selectedType = ref('meal-plan')
const promptTypes = [
    { label: '用餐计划生成', value: 'meal-plan' },
    { label: '菜谱生成', value: 'recipe' },
    { label: '食材替换建议', value: 'substitution' },
]

const promptContent = ref('')
const loading = ref(false)
const saving = ref(false)
const successMsg = ref('')

onMounted(async () => {
    await loadPrompt()
})

async function loadPrompt() {
    loading.value = true
    try {
        const res = await getPrompt(selectedType.value)
        promptContent.value = res.data.data || ''
    } catch {
        promptContent.value = ''
    } finally {
        loading.value = false
    }
}

async function switchType() {
    await loadPrompt()
}

async function save() {
    saving.value = true
    successMsg.value = ''
    try {
        await savePrompt(selectedType.value, promptContent.value)
        successMsg.value = '保存成功'
        setTimeout(() => successMsg.value = '', 2000)
    } catch {
        successMsg.value = '保存失败'
    } finally {
        saving.value = false
    }
}
</script>

<template>
    <div>
        <h1 class="text-2xl font-bold text-gray-800 mb-6">⚙️ 提示词管理</h1>

        <Card>
            <template #content>
                <div class="flex flex-col gap-4">
                    <div class="flex items-center gap-4">
                        <label class="font-medium">提示词类型:</label>
                        <Dropdown v-model="selectedType" :options="promptTypes" optionLabel="label" optionValue="value" @change="switchType" />
                    </div>

                    <Message v-if="successMsg" :severity="successMsg === '保存成功' ? 'success' : 'error'" class="mb-2">
                        {{ successMsg }}
                    </Message>

                    <Textarea v-model="promptContent" rows="15" class="w-full font-mono" :disabled="loading" />
                    <div class="text-sm text-gray-500 mb-2">
                        可用变量: {cuisine_preference}, {allergens}, {health_goal}, {meal_history},
                        {meal}, {ingredients}, {cooking_time}
                    </div>
                    <Button label="保存配置" severity="success" :loading="saving" @click="save" />
                </div>
            </template>
        </Card>
    </div>
</template>
