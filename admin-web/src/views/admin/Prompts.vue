<script setup lang="ts">
import { ref } from 'vue'
import Card from 'primevue/card'
import Textarea from 'primevue/textarea'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'

const selectedPrompt = ref('meal-plan')
const promptTypes = [
    { label: '用餐计划生成', value: 'meal-plan' },
    { label: '菜谱生成', value: 'recipe' },
    { label: '食材替换建议', value: 'substitution' },
]

const promptContent = ref(`你是一个专业的营养师和厨师。请根据用户的以下信息生成一周的餐食计划：
- 口味偏好：{cuisine_preference}
- 忌口/过敏源：{allergens}
- 健康目标：{health_goal}
- 历史就餐记录：{meal_history}

请生成一日三餐的详细安排，包括菜品名称、简要做法和营养说明。`)

function savePrompt() {
    // TODO: 调用 API 保存
    console.log('Save prompt:', selectedPrompt.value, promptContent.value)
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
                        <Dropdown v-model="selectedPrompt" :options="promptTypes" optionLabel="label" optionValue="value" />
                    </div>
                    <Textarea v-model="promptContent" rows="12" class="w-full font-mono" />
                    <div class="text-sm text-gray-500 mb-2">
                        可用变量: {cuisine_preference}, {allergens}, {health_goal}, {meal_history}
                    </div>
                    <Button label="保存配置" severity="success" @click="savePrompt" />
                </div>
            </template>
        </Card>
    </div>
</template>
